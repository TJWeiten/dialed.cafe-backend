package cafe.dialed.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    public HealthCheckController() {
    }

    @PostMapping("/healthcheck")
    public ResponseEntity<String> handleClerkWebhook() {
        return ResponseEntity.ok("I'm doing great!");
    }

}