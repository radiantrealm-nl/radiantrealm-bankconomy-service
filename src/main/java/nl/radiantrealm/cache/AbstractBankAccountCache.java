package nl.radiantrealm.cache;

import nl.radiantrealm.BankAccount;
import nl.radiantrealm.controllers.Database;
import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.utils.format.FormatUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

public abstract class AbstractBankAccountCache<V extends BankAccount> extends CacheRegistry<UUID, V> {
    private final AccountType accountType;
    private final String SQL;

    public AbstractBankAccountCache(Duration expiryDuration, AccountType accountType) {
        super(expiryDuration);

        this.accountType = accountType;
        this.SQL = buildSQL(accountType);
    }

    protected String buildSQL(AccountType accountType) {
        return String.format(
                "SELECT * FROM %s WHERE uuid = ?",
                accountType.tableName
        );
    }

    protected String buildPluralSQL(int size) {
        return String.format(
                "SELECT * FROM %s WHERE uuid IN (%s)",
                accountType.tableName,
                String.join(", ", Collections.nCopies(size, "?"))
        );
    }

    protected abstract V getObject(ResultSet rs, UUID uuid) throws SQLException;

    @Override
    protected V load(UUID uuid) throws Exception {
        try (Connection connection = Database.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SQL)) {
                statement.setString(1, uuid.toString());

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return getObject(rs, uuid);
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected Map<UUID, V> load(Collection<UUID> keys) throws Exception {
        Map<UUID, V> map = new HashMap<>(keys.size());

        try (Connection connection = Database.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(buildPluralSQL(keys.size()))) {
                int index = 0;
                for (UUID uuid : keys) {
                    statement.setString(++index, uuid.toString());
                }

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        UUID uuid = FormatUtils.formatUUID(rs, "uuid");
                        map.put(uuid, getObject(rs, uuid));
                    }
                }
            }
        }

        return map;
    }
}
