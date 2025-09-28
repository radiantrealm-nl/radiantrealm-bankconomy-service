package nl.radiantrealm.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.BankAccount;
import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.library.utils.dto.DataObject;
import nl.radiantrealm.library.utils.json.JsonUtils;

import java.math.BigDecimal;
import java.util.UUID;

public record PlayerAccount(UUID playerUUID, BigDecimal playerBalance, String playerName) implements DataObject, BankAccount {

    public PlayerAccount(JsonObject object) {
        this(
                JsonUtils.getUUID(object, "player_uuid"),
                JsonUtils.getBigDecimal(object, "player_balance"),
                JsonUtils.getString(object, "player_name")
        );
    }

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("player_uuid", playerUUID.toString());
        object.addProperty("player_balance", playerBalance);
        object.addProperty("player_name", playerName);
        return object;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.PLAYER_ACCOUNT;
    }

    @Override
    public UUID getAccountUUID() {
        return playerUUID;
    }

    @Override
    public BigDecimal getAccountBalance() {
        return playerBalance;
    }

    @Override
    public String getAccountName() {
        return playerName;
    }

    public PlayerAccount addBalance(BigDecimal amount) {
        return new PlayerAccount(
                playerUUID,
                playerBalance.add(amount),
                playerName
        );
    }

    public PlayerAccount subtractBalance(BigDecimal amount) {
        return new PlayerAccount(
                playerUUID,
                playerBalance.subtract(amount),
                playerName
        );
    }

    public PlayerAccount updateName(String playerName) {
        return new PlayerAccount(
                playerUUID,
                playerBalance,
                playerName
        );
    }
}
