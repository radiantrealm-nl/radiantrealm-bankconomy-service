package nl.radiantrealm.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.BankAccount;
import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.library.utils.dto.DataObject;
import nl.radiantrealm.library.utils.json.JsonUtils;

import java.math.BigDecimal;
import java.util.UUID;

public record FundingsAccount(UUID fundingsUUID, BigDecimal fundingsBalance, String fundingsName) implements DataObject, BankAccount {

    public FundingsAccount(JsonObject object) {
        this(
                JsonUtils.getUUID(object, "fundings_uuid"),
                JsonUtils.getBigDecimal(object, "fundings_balance"),
                JsonUtils.getString(object, "fundings_name")
        );
    }

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("fundiings_uuid", fundingsUUID.toString());
        object.addProperty("fundings_balance", fundingsBalance);
        object.addProperty("fundings_name", fundingsName);
        return object;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.FUNDINGS_ACCOUNT;
    }

    @Override
    public UUID getAccountUUID() {
        return fundingsUUID;
    }

    @Override
    public BigDecimal getAccountBalance() {
        return fundingsBalance;
    }

    @Override
    public String getAccountName() {
        return fundingsName;
    }

    @Override
    public BankAccount updateBalance(BigDecimal accountBalance) {
        return new FundingsAccount(
                fundingsUUID,
                accountBalance,
                fundingsName
        );
    }

    public FundingsAccount updateName(String fundingsName) {
        return new FundingsAccount(
                fundingsUUID,
                fundingsBalance,
                fundingsName
        );
    }
}
