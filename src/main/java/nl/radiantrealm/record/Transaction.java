package nl.radiantrealm.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.enumerator.TransactionType;
import nl.radiantrealm.library.utils.dto.DataObject;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction(TransactionType transactionType, BigDecimal transactionAmount, UUID sourceUUID, UUID offsetUUID, String message) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("transaction_type", transactionType.name());
        object.addProperty("transaction_amount", transactionAmount);
        object.addProperty("source_uuid", sourceUUID.toString());
        object.addProperty("offset_uuid", offsetUUID.toString());
        object.addProperty("transaction_message", message);
        return object;
    }

    public TransactionLog createTransactionLog() {
        return new TransactionLog(
                0,
                System.currentTimeMillis(),
                transactionType,
                transactionAmount,
                sourceUUID,
                offsetUUID,
                message
        );
    }
}
