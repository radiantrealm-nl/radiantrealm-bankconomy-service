package nl.radiantrealm.cache;

import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.library.utils.format.FormatUtils;
import nl.radiantrealm.record.SavingsAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

public class SavingsAccountCache extends AbstractBankAccountCache<SavingsAccount> {

    public SavingsAccountCache() {
        super(
                Duration.ofMinutes(15),
                AccountType.SAVINGS_ACCOUNT
        );
    }

    @Override
    protected SavingsAccount getObject(ResultSet rs, UUID uuid) throws SQLException {
        return new SavingsAccount(
                uuid,
                FormatUtils.formatUUID("owner_uuid"),
                rs.getBigDecimal("balance"),
                rs.getBigDecimal("accumulated_interest"),
                rs.getString("name")
        );
    }
}
