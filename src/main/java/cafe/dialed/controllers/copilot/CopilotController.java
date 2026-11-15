package cafe.dialed.controllers.copilot;

import cafe.dialed.entities.Brew;
import cafe.dialed.entities.User;
import cafe.dialed.repositories.BrewRepository;
import cafe.dialed.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * Exposes GET /brews/{id}/copilot/stream which streams barista co-pilot
 * feedback over Server-Sent Events.
 */
@RestController
@RequestMapping("/brews")
public class CopilotController {

    private static final long SSE_TIMEOUT = 120_000L; // 2 minutes

    private final BrewRepository brewRepository;
    private final UserRepository userRepository;
    private final CopilotService copilotService;

    public CopilotController(BrewRepository brewRepository, UserRepository userRepository,
                             CopilotService copilotService) {
        this.brewRepository = brewRepository;
        this.userRepository = userRepository;
        this.copilotService = copilotService;
    }

    @GetMapping(value = "/{id}/copilot/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamCopilotFeedback(@PathVariable UUID id,
                                            @RequestParam(defaultValue = "false") boolean regenerate,
                                            @AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));

        Brew brew = brewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Brew not found"));

        if (!brew.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to view this brew.");
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        emitter.onTimeout(emitter::complete);
        emitter.onError(e -> emitter.completeWithError(e));

        // Run the stream on a separate thread so the HTTP request returns immediately
        Thread.startVirtualThread(() -> copilotService.streamFeedback(brew, regenerate, emitter));

        return emitter;
    }
}