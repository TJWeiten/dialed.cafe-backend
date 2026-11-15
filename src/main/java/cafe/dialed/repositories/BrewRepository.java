package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import cafe.dialed.entities.*;
import java.util.List;
import java.util.UUID;

public interface BrewRepository extends JpaRepository<Brew, UUID> {
    List<Brew> findByUserIdAndDeletedFalse(UUID userId, Sort sort);
    long countByUserId(UUID userId);
}
