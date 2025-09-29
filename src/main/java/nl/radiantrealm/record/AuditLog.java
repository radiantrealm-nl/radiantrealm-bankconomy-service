package nl.radiantrealm.record;

import com.google.gson.JsonObject;
import nl.radiantrealm.enumerator.AuditType;
import nl.radiantrealm.library.utils.dto.DataObject;
import nl.radiantrealm.library.utils.json.JsonUtils;

import java.util.UUID;

public record AuditLog(int logID, long timestamp, AuditType auditType, UUID relatedUUID, String message) implements DataObject {

    @Override
    public JsonObject toJson() throws IllegalStateException {
        JsonObject object = new JsonObject();
        object.addProperty("log_id", logID);
        object.addProperty("timestamp", timestamp);
        object.addProperty("audit_type", auditType.name());
        object.addProperty("related_uuid", relatedUUID.toString());
        object.addProperty("message", message);
        return object;
    }

    public static AuditLog createAuditLog(AuditType auditType, UUID relatedUUID, String message) {
        return new AuditLog(
                0,
                System.currentTimeMillis(),
                auditType,
                relatedUUID,
                message
        );
    }
}
