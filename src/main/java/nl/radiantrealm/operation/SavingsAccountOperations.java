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
import nl.radiantrealm.record.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SavingsAccountOperations {
    private static final Logger logger = Logger.getLogger(SavingsAccountOperations.class);

    public record CREATE_ACCOUNT(SavingsAccount savingsAccount) implements ProcessHandler {
        private static final String SQL = String.format(
                "INSERT INTO %s VALUES (?, ?, ?, ?, ?)",
                AccountType.SAVINGS_ACCOUNT.tableName
        );

        @Override
        public ProcessResult handle(Process process) throws Exception {
            Connection connection = Database.getConnection(false);

            try (connection) {
                try (PreparedStatement statement = connection.prepareStatement(SQL)) {
                    statement.setString(1, savingsAccount.savingsUUID().toString());
                    statement.setString(2, savingsAccount.ownerUUID().toString());
                    statement.setBigDecimal(3, savingsAccount.savingsBalance());
                    statement.setBigDecimal(4, savingsAccount.accumulatedInterest());
                    statement.setString(5, savingsAccount.savingsName());
                    statement.executeUpdate();
                }

                Database.insertAuditLog(connection, AuditLog.createAuditLog(
                        AuditType.SAVINGS_ACCOUNT_CREATE,
                        savingsAccount.ownerUUID(),
                        savingsAccount.savingsName()
                ));

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Failed to insert savings account.", e);
                return ProcessResult.error(StatusCode.SERVER_ERROR, "Database error.");
            }

            Cache.savingsAccountCache.put(savingsAccount);
            return ProcessResult.ok();
        }
    }

    public record UPDATE_NAME(UUID savingsUUID, String savingsName) implements ProcessHandler {
        private static final String SQL = String.format(
                "UPDATE %S SET name = ? WHERE balance = ?",
                AccountType.SAVINGS_ACCOUNT.tableName
        );

        @Override
        public ProcessResult handle(Process process) throws Exception {
            Connection connection = Database.getConnection(false);

            SavingsAccount savingsAccount = Cache.savingsAccountCache.get(savingsUUID);

            if (savingsAccount == null) {
                return ProcessResult.error(StatusCode.NOT_FOUND, "No savings account found.");
            }

            savingsAccount = savingsAccount.updateName(savingsName);

            try (connection) {
                try (PreparedStatement statement = connection.prepareStatement(SQL)) {
                    statement.setString(1, savingsAccount.savingsName());
                    statement.setString(2, savingsAccount.savingsUUID().toString());
                    statement.executeUpdate();
                }

                Database.insertAuditLog(connection, AuditLog.createAuditLog(
                        AuditType.SAVINGS_ACCOUNT_UPDATE_NAME,
                        savingsAccount.savingsUUID(),
                        savingsAccount.savingsName()
                ));

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Failed to update savings account name.", e);
                return ProcessResult.error(StatusCode.SERVER_ERROR, "Database error.");
            }

            Cache.savingsAccountCache.put(savingsAccount);
            return ProcessResult.ok();
        }
    }
}
