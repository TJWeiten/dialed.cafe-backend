package cafe.dialed.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class UserController {

    @GetMapping("/protected")
    public Map<String, String> getProtectedData(@AuthenticationPrincipal Jwt jwt) {
        String oauthProviderId = jwt.getSubject();
        String fullName = jwt.getClaimAsString("name");
        System.out.println("oauthProviderId = " + oauthProviderId + " | Access to /protected granted!");
        return Map.of("message", String.format("Hi %s! I'm Spring Boot. Warm welcome to User ID: %s!", fullName, oauthProviderId));
    }

}
