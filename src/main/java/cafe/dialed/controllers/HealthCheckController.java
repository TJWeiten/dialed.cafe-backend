package cafe.dialed.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    public HealthCheckController() {
    }

    @GetMapping("/healthcheck")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("I'm doing great!");
    }

}