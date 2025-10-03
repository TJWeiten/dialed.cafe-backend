package cafe.dialed.controllers;



import cafe.dialed.controllers.clerk.ClerkWebhookPayload;
import cafe.dialed.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svix.Webhook;
import com.svix.exceptions.WebhookVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpHeaders;
import java.util.*;

@RestController
public class ClerkWebhookController {

    private final String webhookSecret;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public ClerkWebhookController(
            ObjectMapper objectMapper,
            @Value("${clerk.webhook.secret}") String webhookSecret,
            UserService userService) {
        this.objectMapper = objectMapper;
        this.webhookSecret = webhookSecret;
        this.userService = userService;
    }

    @PostMapping("/webhooks/clerkEvent")
    public ResponseEntity<String> handleClerkWebhook(
            @RequestBody String payload,
            @RequestHeader("svix-id") String svixId,
            @RequestHeader("svix-timestamp") String svixTimestamp,
            @RequestHeader("svix-signature") String svixSignature) {

        System.out.println("WebHook Call Received | Handling...");

        try {
            Webhook webhook = new Webhook(webhookSecret);

            // This was surprisingly difficult to figure out, since the documentation is wrong...
            // The key appears to be Collections.singletonList and not asList()?
            Map<String, List<String>> headersMap = new HashMap<>();
            headersMap.put("svix-id", Collections.singletonList(svixId));
            headersMap.put("svix-timestamp", Collections.singletonList(svixTimestamp));
            headersMap.put("svix-signature", Collections.singletonList(svixSignature));

            HttpHeaders headers = HttpHeaders.of(headersMap, (name, value) -> true);
            webhook.verify(payload, headers);

            // At this point, the payload is verified...
            ClerkWebhookPayload clerkEvent = objectMapper.readValue(payload, ClerkWebhookPayload.class);

            if (clerkEvent.type().equals("user.created")) {
                userService.updateOrCreateUser(clerkEvent.data());
            } else if (clerkEvent.type().equals("user.updated")) {
                userService.updateOrCreateUser(clerkEvent.data());
            } else if (clerkEvent.type().equals("user.deleted")) {
                userService.deleteUser(clerkEvent.data());
            } else if (clerkEvent.type().equals("email.created")) {
                userService.updateOrCreateUser(clerkEvent.data());
            }

            System.out.println("Webhook: " + clerkEvent.type() + " processed!");
            return ResponseEntity.ok("Webhook processed successfully");

        } catch (WebhookVerificationException e) {
            System.out.println("Webhook FAILED: " + e.toString());
            return ResponseEntity.status(400).body("Webhook verification failed");
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}