package cafe.dialed.entities;

import cafe.dialed.entities.enums.Process;
import cafe.dialed.entities.enums.RoastLevel;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "beans")
public class Bean {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String roaster;

    @Enumerated(EnumType.STRING)
    private RoastLevel roastLevel;

    private Integer packageWeight;
    private Double currentWeight;
    private Boolean isDecaf;

    @Enumerated(EnumType.STRING)
    private Process process;

    private LocalDate roastDate;
    private String descriptors;

    @Lob
    private String notes;
}
