package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import cafe.dialed.entities.*;
import java.util.List;
import java.util.UUID;

public interface SauceRepository extends JpaRepository<Sauce, UUID> {
    List<Sauce> findByUserId(UUID userId);
}
