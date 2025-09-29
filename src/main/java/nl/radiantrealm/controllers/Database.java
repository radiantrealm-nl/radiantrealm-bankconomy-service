package nl.radiantrealm.controllers;

import nl.radiantrealm.BankAccount;
import nl.radiantrealm.library.sql.HikariDatabase;
import nl.radiantrealm.record.AuditLog;
import nl.radiantrealm.record.TransactionLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database extends HikariDatabase {

    @Override
    protected String databaseURL() {
        return System.getenv("DB_URL");
    }

    @Override
    protected String databaseUsername() {
        return System.getenv("DB_USERNAME");
    }

    @Override
    protected String databasePassword() {
        return System.getenv("DB_PASSWORD");
    }

    public static void insertAuditLog(Connection connection, AuditLog auditLog) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO bankconomy_audits (timestamp, audit_type, related_uuid, message) VALUES (?, ?, ?, ?)"
        )) {
            statement.setLong(1, auditLog.timestamp());
            statement.setString(2, auditLog.auditType().name());
            statement.setString(3, auditLog.relatedUUID().toString());
            statement.setString(4, auditLog.message());
            statement.executeUpdate();
        }
    }

    public static void insertTransactionLog(Connection connectionon, TransactionLog transactionLog) throws SQLException {
        try (PreparedStatement statement = connectionon.prepareStatement(
                "INSERT INTO bankconomy_transactions (timestamp, transaction_type, transaction_amount, source_uuid, offset_uuid, message) VALUES (?, ?, ?, ?, ?)"
        )) {
            statement.setLong(1, transactionLog.timestamp());
            statement.setString(2, transactionLog.transactionType().name());
            statement.setString(3, transactionLog.sourceUUID().toString());
            statement.setString(4, transactionLog.offsetUUID().toString());
            statement.setString(5, transactionLog.message());
            statement.executeUpdate();
        }
    }

    public static void updateBankAccountBalance(Connection connection, BankAccount bankAccount) throws SQLException {
        String SQL = String.format(
                "UPDATE %s SET balance = ? WHERE uuid = ?",
                bankAccount.getAccountType().tableName
        );

        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            statement.setBigDecimal(1, bankAccount.getAccountBalance());
            statement.setString(2, bankAccount.getAccountUUID().toString());
            statement.executeUpdate();
        }
    }
}
