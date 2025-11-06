package cafe.dialed.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SauceRequestDTO {
    private UUID id;
    private String name;
    private String recipe;
    private String imageUrl;
}
