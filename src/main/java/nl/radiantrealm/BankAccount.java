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

    default boolean hasSufficientBalance(BigDecimal amount) {
        return (getAccountBalance().compareTo(amount) >= 0);
    }

    BankAccount updateBalance(BigDecimal accountBalance);

    default BankAccount addBalance(BigDecimal amount) {
        return updateBalance(getAccountBalance().add(amount));
    }

    default BankAccount subtractBalance(BigDecimal amount) {
        return updateBalance(getAccountBalance().subtract(amount));
    }
}
