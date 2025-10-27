package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import cafe.dialed.entities.*;
import java.util.List;
import java.util.UUID;

public interface GrinderRepository extends JpaRepository<Grinder, UUID> {
    List<Grinder> findByUserId(UUID userId);
    List<Grinder> findByUserIdAndDeletedFalse(UUID userId);
}