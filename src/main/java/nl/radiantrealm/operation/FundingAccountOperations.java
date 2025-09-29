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
import nl.radiantrealm.record.FundingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class FundingAccountOperations {
    private static final Logger logger = Logger.getLogger(FundingAccountOperations.class);

    public record CREATE_ACCOUNT(FundingsAccount fundingsAccount) implements ProcessHandler {
        private static final String SQL = String.format(
                "INSERT INTO %s VALUES (?, ?, ?)",
                AccountType.FUNDINGS_ACCOUNT.tableName
        );

        @Override
        public ProcessResult handle(Process process) throws Exception {
            Connection connection = Database.getConnection(false);

            try (connection) {
                try (PreparedStatement statement = connection.prepareStatement(SQL)) {
                    statement.setString(1, fundingsAccount.fundingsUUID().toString());
                    statement.setBigDecimal(2, fundingsAccount.fundingsBalance());
                    statement.setString(3, fundingsAccount.fundingsName());
                    statement.executeUpdate();
                }

                Database.insertAuditLog(connection, AuditLog.createAuditLog(
                        AuditType.FUNDINGS_ACCOUNT_CREATE,
                        fundingsAccount.fundingsUUID(),
                        fundingsAccount.fundingsName()
                ));

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Failed to insert fundings account.", e);
                return ProcessResult.error(StatusCode.SERVER_ERROR, "Database error.");
            }

            Cache.fundingsAccountCache.put(fundingsAccount);
            return ProcessResult.ok();
        }
    }

    public record UPDATE_NAME(UUID fundingsUUID, String fundingsName) implements ProcessHandler {
        private static final String SQL = String.format(
                "UPDATE %s SET fundings_name = ? WHERE fundings_uuid = ?",
                AccountType.FUNDINGS_ACCOUNT.tableName
        );

        @Override
        public ProcessResult handle(Process process) throws Exception {
            Connection connection = Database.getConnection(false);

            FundingsAccount fundingsAccount = Cache.fundingsAccountCache.get(fundingsUUID);

            if (fundingsAccount == null) {
                return ProcessResult.error(StatusCode.NOT_FOUND, "No fundings account found.");
            }

            fundingsAccount = fundingsAccount.updateName(fundingsName);

            try (connection) {
                try (PreparedStatement statement = connection.prepareStatement(SQL)) {
                    statement.setString(1, fundingsAccount.fundingsName());
                    statement.setString(2, fundingsUUID.toString());
                    statement.executeUpdate();
                }

                Database.insertAuditLog(connection, AuditLog.createAuditLog(
                        AuditType.FUNDINGS_ACCOUNT_UPDATE_NAME,
                        fundingsAccount.fundingsUUID(),
                        fundingsAccount.fundingsName()
                ));

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error("Failed to update fundings account name.", e);
                return ProcessResult.error(StatusCode.SERVER_ERROR, "Database error.");
            }

            Cache.fundingsAccountCache.put(fundingsAccount);
            return ProcessResult.ok();
        }
    }
}
