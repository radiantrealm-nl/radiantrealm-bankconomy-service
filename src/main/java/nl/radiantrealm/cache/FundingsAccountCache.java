package nl.radiantrealm.cache;

import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.record.FundingsAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

public class FundingsAccountCache extends AbstractBankAccountCache<FundingsAccount> {

    public FundingsAccountCache() {
        super(
                Duration.ofMinutes(15),
                AccountType.FUNDINGS_ACCOUNT
        );
    }

    @Override
    protected FundingsAccount getObject(ResultSet rs, UUID uuid) throws SQLException {
        return new FundingsAccount(
                uuid,
                rs.getBigDecimal("balance"),
                rs.getString("name")
        );
    }
}
