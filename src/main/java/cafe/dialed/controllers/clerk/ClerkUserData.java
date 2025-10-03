package cafe.dialed.controllers.clerk;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record ClerkUserData(
        @JsonProperty("id") String id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("email_addresses") List<Map<String, Object>> emailAddresses
) {}
