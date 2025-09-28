package nl.radiantrealm;

import com.google.gson.JsonObject;
import nl.radiantrealm.enumerator.AccountType;
import nl.radiantrealm.library.utils.dto.DataObject;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankAccount extends DataObject {
    AccountType getAccountType();
    UUID getAccountUUID();
    BigDecimal getAccountBalance();
    String getAccountName();

    @Override
    default JsonObject toJson() throws IllegalStateException {
        return DataObject.super.toJson();
    }
}
