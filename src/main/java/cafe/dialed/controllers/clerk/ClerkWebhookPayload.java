package cafe.dialed.controllers.clerk;

import java.time.Instant;

public record ClerkWebhookPayload(
        ClerkUserData data,
        String instanceId,
        String object,
        Instant timestamp,
        String type
) {}
