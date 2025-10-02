package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import cafe.dialed.entities.*;
import java.util.List;
import java.util.UUID;

public interface SauceVersionRepository extends JpaRepository<SauceVersion, UUID> {
    List<SauceVersion> findBySauceId(UUID sauceId);
}
