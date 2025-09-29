package nl.radiantrealm.operation;

import nl.radiantrealm.BankAccount;
import nl.radiantrealm.controllers.Cache;
import nl.radiantrealm.controllers.Database;
import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.library.http.enumerator.StatusCode;
import nl.radiantrealm.library.processor.Process;
import nl.radiantrealm.library.processor.ProcessHandler;
import nl.radiantrealm.library.processor.ProcessResult;
import nl.radiantrealm.library.utils.Logger;
import nl.radiantrealm.record.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public record TransactionOperations(Transaction transaction) implements ProcessHandler {
    private static final Logger logger = Logger.getLogger(TransactionOperations.class);

    @Override
    public ProcessResult handle(Process process) throws Exception {
        BankAccount sourceAccount = getBankAccountSource();

        if (sourceAccount == null) {
            return ProcessResult.error(StatusCode.SERVER_ERROR, "Source account not found.");
        }

        BankAccount offsetAccount = getBankAccountOffset();

        if (offsetAccount == null) {
            return ProcessResult.error(StatusCode.NOT_FOUND, "Target account not found.");
        }

        if (!sourceAccount.hasSufficientBalance(transaction.transactionAmount())) {
            return ProcessResult.error(StatusCode.UNPROCESSABLE_ENTITY, "Insufficient balance.");
        }

        sourceAccount = sourceAccount.subtractBalance(transaction.transactionAmount());
        offsetAccount = offsetAccount.addBalance(transaction.transactionAmount());

        Connection connection = Database.getConnection(false);

        try (connection) {
            Database.updateBankAccountBalance(connection, sourceAccount);
            Database.updateBankAccountBalance(connection, offsetAccount);
            Database.insertTransactionLog(connection, transaction.createTransactionLog());

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Failed to execute bank account transaction.", e);
            return ProcessResult.error(StatusCode.SERVER_ERROR, "Database error.");
        }

        updateBankAccountCache(sourceAccount);
        updateBankAccountCache(offsetAccount);
        return ProcessResult.ok();
    }

    public BankAccount getBankAccount(AccountType accountType, UUID accountUUID) throws Exception {
        return switch (accountType) {
            case PLAYER_ACCOUNT -> Cache.playerAccountCache.get(accountUUID);
            case SAVINGS_ACCOUNT -> Cache.savingsAccountCache.get(accountUUID);
            case FUNDINGS_ACCOUNT -> Cache.fundingsAccountCache.get(accountUUID);
        };
    }

    private BankAccount getBankAccountSource() throws Exception {
        return getBankAccount(
                transaction.transactionType().sourceAccount,
                transaction.sourceUUID()
        );
    }

    private BankAccount getBankAccountOffset() throws Exception {
        return getBankAccount(
                transaction.transactionType().offsetAccount,
                transaction.offsetUUID()
        );
    }

    private void updateBankAccountCache(BankAccount bankAccount) {
        switch (bankAccount.getAccountType()) {
            case PLAYER_ACCOUNT -> Cache.playerAccountCache.update(bankAccount.getAccountUUID());
            case SAVINGS_ACCOUNT -> Cache.savingsAccountCache.update(bankAccount.getAccountUUID());
            case FUNDINGS_ACCOUNT -> Cache.fundingsAccountCache.update(bankAccount.getAccountUUID());
        }
    }
}
