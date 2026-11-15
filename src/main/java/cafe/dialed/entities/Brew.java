package cafe.dialed.entities;

import cafe.dialed.entities.enums.BasketType;
import cafe.dialed.entities.enums.DrinkType;
import cafe.dialed.entities.enums.MilkType;
import cafe.dialed.entities.enums.Rating;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brews")
public class Brew {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bean_id", nullable = false)
    private Bean bean;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grinder_id", nullable = false)
    private Grinder grinder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sauce_version_id")
    private SauceVersion sauceVersion;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant brewDate;

    // Core Espresso Stats
    @Column(nullable = false)
    private Double weightIn;
    @Column(nullable = false)
    private Double weightOut;
    @Column(nullable = false)
    private Integer timePulled;
    @Column(nullable = false)
    private String grindSetting;

    // Evaluation
    @Enumerated(EnumType.STRING)
    private Rating rating;
    @Lob
    private String tastingNotes;

    // Optional Advanced Stats
    private Integer waterTemp;
    private Integer preinfusionTime;
    @Lob
    private String puckPrepNotes;
    private String waterType;
    @Enumerated(EnumType.STRING)
    private BasketType basketType;
    private Integer basketSize; // not on frontend?

    // Final Drink Details
    @Enumerated(EnumType.STRING)
    private DrinkType madeAs;
    @Enumerated(EnumType.STRING)
    private MilkType milkType;
    private Integer milkAmount;

    // Barista Co-pilot
    @Column(length = 512)
    private String copilotNotes;
    @Lob
    private String copilotFeedback;
    @Lob
    private String copilotComment;
    @Lob
    private String copilotChainOfThought;

    private boolean deleted = false;

}
