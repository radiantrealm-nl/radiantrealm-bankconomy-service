package nl.radiantrealm.cache;

import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.record.PlayerAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

public class PlayerAccountCache extends AbstractBankAccountCache<PlayerAccount> {

    public PlayerAccountCache() {
        super(
                Duration.ofMinutes(15),
                AccountType.PLAYER_ACCOUNT
        );
    }

    @Override
    protected PlayerAccount getObject(ResultSet rs, UUID uuid) throws SQLException {
        return new PlayerAccount(
                uuid,
                rs.getBigDecimal("balance"),
                rs.getString("name")
        );
    }
}
