package cafe.dialed.controllers.copilot;

import cafe.dialed.entities.Bean;
import cafe.dialed.entities.Brew;
import cafe.dialed.entities.Grinder;
import cafe.dialed.repositories.BrewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * Builds the barista co-pilot prompt from a Brew, streams the LLM response
 * to an SseEmitter, accumulates the full text, and persists it to the Brew
 * row when the stream completes.
 */
@Service
public class CopilotService {

    private static final Logger logger = LoggerFactory.getLogger(CopilotService.class);

    public record StreamChunk(String content) {}

    private final OpenRouterClient openRouterClient;
    private final BrewRepository brewRepository;

    public CopilotService(OpenRouterClient openRouterClient, BrewRepository brewRepository) {
        this.openRouterClient = openRouterClient;
        this.brewRepository = brewRepository;
    }

    /**
     * Streams copilot feedback for a brew into the emitter.
     * If cached feedback exists and regenerate is false, emits it immediately.
     */
    public void streamFeedback(Brew brew, boolean regenerate, SseEmitter emitter) {
        // Serve cached feedback unless a regeneration was requested
        if (!regenerate && brew.getCopilotFeedback() != null && !brew.getCopilotFeedback().isBlank()) {
            try {
                emitter.send(SseEmitter.event()
                        .name("chunk")
                        .data(new StreamChunk(brew.getCopilotFeedback()), MediaType.APPLICATION_JSON));
                emitter.send(SseEmitter.event().name("done"));
                emitter.complete();
            } catch (IOException e) {
                logger.warn("Failed to send cached copilot feedback", e);
                emitter.completeWithError(e);
            }
            return;
        }

        String systemPrompt = buildSystemPrompt();
        String userPrompt = buildUserPrompt(brew);
        StringBuilder accumulator = new StringBuilder();

        try {
            openRouterClient.streamCompletion(systemPrompt, userPrompt, chunk -> {
                accumulator.append(chunk);
                try {
                    emitter.send(SseEmitter.event()
                            .name("chunk")
                            .data(new StreamChunk(chunk), MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    logger.warn("Failed to send SSE chunk", e);
                    throw new RuntimeException(e);
                }
            });

            // Persist the full accumulated feedback
            String fullFeedback = accumulator.toString();
            brew.setCopilotFeedback(fullFeedback);
            brewRepository.save(brew);

            emitter.send(SseEmitter.event().name("done"));
            emitter.complete();
        } catch (Exception e) {
            logger.error("Copilot stream failed for brew {}", brew.getId(), e);
            try {
                String errorMessage = e.getMessage() != null ? e.getMessage() : "Stream interrupted";
                emitter.send(SseEmitter.event().name("error").data(errorMessage));
            } catch (IOException ioEx) {
                logger.debug("Client disconnected before error event could be sent", ioEx);
            }
            emitter.completeWithError(e);
        }
    }

    private String buildSystemPrompt() {
        return """
                You are a barista co-pilot for a specialty coffee app called DIALED.
                You receive the details of a single espresso brew (dose, yield, time, grind setting,
                bean, grinder, rating, tasting notes, and optional parameters) and provide concise,
                actionable feedback to help the user dial in their next shot.
                Focus on extraction quality, ratio, timing, and grind adjustments.
                Keep the response under 50 words and address the user directly! THAT IS BE BRIEF!
                You may be a little quirky—you're a chatty barista after all. Do not overuse emojis, however.
                """;
    }

    private String buildUserPrompt(Brew brew) {
        Bean bean = brew.getBean();
        Grinder grinder = brew.getGrinder();
        StringBuilder sb = new StringBuilder();
        sb.append("Here is my most recent brew:\n");
        if (bean != null) {
            sb.append("- Bean: ").append(bean.getRoaster()).append(" ").append(bean.getName()).append("\n");
        }
        if (grinder != null) {
            sb.append("- Grinder: ").append(grinder.getName()).append("\n");
        }
        sb.append("- Grind setting: ").append(brew.getGrindSetting()).append("\n");
        sb.append("- Dose in: ").append(brew.getWeightIn()).append("g\n");
        sb.append("- Yield out: ").append(brew.getWeightOut()).append("g\n");
        sb.append("- Time: ").append(brew.getTimePulled()).append("s\n");
        if (brew.getWaterTemp() != null) {
            sb.append("- Water temp: ").append(brew.getWaterTemp()).append("C\n");
        }
        if (brew.getPreinfusionTime() != null) {
            sb.append("- Pre-infusion: ").append(brew.getPreinfusionTime()).append("s\n");
        }
        if (brew.getRating() != null) {
            sb.append("- My rating: ").append(brew.getRating()).append("\n");
        }
        if (brew.getTastingNotes() != null && !brew.getTastingNotes().isBlank()) {
            sb.append("- My tasting notes: ").append(brew.getTastingNotes()).append("\n");
        }
        if (brew.getCopilotNotes() != null && !brew.getCopilotNotes().isBlank()) {
            sb.append("- My notes for you: ").append(brew.getCopilotNotes()).append("\n");
        }
        /* TODO: ADD ALL BREW FIELDS + HISTORY TO REQUEST */
        return sb.toString();
    }
}
