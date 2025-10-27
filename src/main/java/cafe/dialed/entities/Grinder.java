package cafe.dialed.entities;

import cafe.dialed.entities.enums.BurrType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grinders")
public class Grinder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private BurrType burrType;

    private boolean isStepless;
    private String grindRange;

    @Lob
    private String notes;

    @Column(name = "image_url")
    private String imageUrl;

    private boolean deleted = false;
}
