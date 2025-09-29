package nl.radiantrealm.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.BankAccount;
import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.library.utils.dto.DataObject;
import nl.radiantrealm.library.utils.json.JsonUtils;

import java.math.BigDecimal;
import java.util.UUID;

public record SavingsAccount(UUID savingsUUID, UUID ownerUUID, BigDecimal savingsBalance, BigDecimal accumulatedInterest, String savingsName) implements DataObject, BankAccount {

    public SavingsAccount(JsonObject object) {
        this(
                JsonUtils.getUUID(object, "savings_uuid"),
                JsonUtils.getUUID(object, "owner_uuid"),
                JsonUtils.getBigDecimal(object, "savings_balance"),
                JsonUtils.getBigDecimal(object, "accumulated_interest"),
                JsonUtils.getString(object, "savings_name")
        );
    }

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("savings_uuid", savingsUUID.toString());
        object.addProperty("owner_uuid", ownerUUID.toString());
        object.addProperty("savings_balance", savingsBalance);
        object.addProperty("accumulated_interest", accumulatedInterest);
        object.addProperty("savings_name", savingsName);
        return object;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS_ACCOUNT;
    }

    @Override
    public UUID getAccountUUID() {
        return savingsUUID;
    }

    @Override
    public BigDecimal getAccountBalance() {
        return savingsBalance;
    }

    @Override
    public String getAccountName() {
        return savingsName;
    }

    @Override
    public BankAccount updateBalance(BigDecimal accountBalance) {
        return new SavingsAccount(
                savingsUUID,
                ownerUUID,
                accountBalance,
                accumulatedInterest,
                savingsName
        );
    }

    public SavingsAccount updateName(String savingsName) {
        return new SavingsAccount(
                savingsUUID,
                ownerUUID,
                savingsBalance,
                accumulatedInterest,
                savingsName
        );
    }
}
