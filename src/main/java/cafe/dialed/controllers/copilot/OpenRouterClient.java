package cafe.dialed.controllers.copilot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Calls OpenRouter's chat completion endpoint with streaming enabled and
 * forwards each delta.content chunk to the provided consumer.
 */
@Service
public class OpenRouterClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenRouterClient.class);
    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.model}")
    private String model;

    /**
     * Streams a chat completion from OpenRouter.
     *
     * @param systemPrompt The system prompt describing the assistant's role
     * @param userPrompt   The user message containing brew data
     * @param chunkConsumer Receives each text delta as it arrives
     * @throws IOException on connection or read failure
     */
    public void streamCompletion(String systemPrompt, String userPrompt, Consumer<String> chunkConsumer)
            throws IOException {
        URL url = URI.create(OPENROUTER_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(8192);

        String body = mapper.createObjectNode()
                .put("model", model)
                .put("stream", true)
                .set("messages", mapper.createArrayNode()
                        .add(mapper.createObjectNode().put("role", "system").put("content", systemPrompt))
                        .add(mapper.createObjectNode().put("role", "user").put("content", userPrompt)))
                .toString();

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            String err = readAll(conn.getErrorStream());
            logger.error("OpenRouter returned {}: {}", status, err);
            throw new IOException("OpenRouter HTTP " + status + ": " + err);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data: ")) continue;
                String payload = line.substring(6).trim();
                if ("[DONE]".equals(payload)) break;
                try {
                    JsonNode node = mapper.readTree(payload);
                    JsonNode choices = node.path("choices");
                    if (choices.isArray() && !choices.isEmpty()) {
                        String content = choices.get(0).path("delta").path("content").asText("");
                        if (!content.isEmpty()) {
                            chunkConsumer.accept(content);
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Skipping unparseable SSE line: {}", payload);
                }
            }
        } finally {
            conn.disconnect();
        }
    }

    private String readAll(java.io.InputStream is) throws IOException {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}