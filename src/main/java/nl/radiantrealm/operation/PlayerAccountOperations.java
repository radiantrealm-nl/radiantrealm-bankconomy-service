package nl.radiantrealm.operation;

import nl.radiantrealm.controllers.Cache;
import nl.radiantrealm.controllers.Database;
import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.enumerator.AuditType;
import nl.radiantrealm.library.http.enumerator.StatusCode;
import nl.radiantrealm.library.processor.Process;
import nl.radiantrealm.library.processor.ProcessHandler;
import nl.radiantrealm.library.processor.ProcessResult;
import nl.radiantrealm.library.utils.Logger;
import nl.radiantrealm.record.AuditLog;
import nl.radiantrealm.record.PlayerAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerAccountOperations {
    private static final Logger logger = Logger.getLogger(PlayerAccountOperations.class);

    public record CREATE_ACCOUNT(PlayerAccount playerAccount) implements ProcessHandler {
        private static final String SQL = String.format(
                "INSERT INTO %S VALUES (?, ?, ?)",
                AccountType.PLAYER_ACCOUNT.tableName
        );

        @Override
        public ProcessResult handle(Process process) throws Exception {
            Connection connection = Database.getConnection(false);

            try (connection) {
                try (PreparedStatement statement = connection.prepareStatement(SQL)) {
                    statement.setString(1, playerAccount.playerUUID().toString());
                    statement.setBigDecimal(2, playerAccount.playerBalance());
                    statement.setString(3, playerAccount.playerName());
                    statement.executeUpdate();
                }

                Database.insertAuditLog(connection, AuditLog.createAuditLog(
                        AuditType.PLAYER_ACCOUNT_CREATE,
                        playerAccount.playerUUID(),
                        playerAccount.playerName()
                ));

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Failed to insert player account.", e);
                return ProcessResult.error(StatusCode.SERVER_ERROR, "Database error.");
            }

            Cache.playerAccountCache.put(playerAccount);
            return ProcessResult.ok();
        }
    }

    public record UPDATE_NAME(UUID playerUUID, String playerName) implements ProcessHandler {
        private static final String SQL = String.format(
                "UPDATE %s SET name = ? WHERE balance = ?",
                AccountType.PLAYER_ACCOUNT.tableName
        );

        @Override
        public ProcessResult handle(Process process) throws Exception {
            Connection connection = Database.getConnection(false);

            PlayerAccount playerAccount = Cache.playerAccountCache.get(playerUUID);

            if (playerAccount == null) {
                return ProcessResult.error(StatusCode.NOT_FOUND, "No player account found.");
            }

            playerAccount = playerAccount.updateName(playerName);

            try (connection) {
                try (PreparedStatement statement = connection.prepareStatement(SQL)) {
                    statement.setString(1, playerAccount.playerName());
                    statement.setString(2, playerAccount.playerUUID().toString());
                    statement.executeUpdate();
                }

                Database.insertAuditLog(connection, AuditLog.createAuditLog(
                        AuditType.PLAYER_ACCOUNT_UPDATE_NAME,
                        playerAccount.playerUUID(),
                        playerAccount.playerName()
                ));

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Failed to update player account name.", e);
                return ProcessResult.error(StatusCode.SERVER_ERROR, "Database error.");
            }

            Cache.playerAccountCache.put(playerAccount);
            return ProcessResult.ok();
        }
    }
}
