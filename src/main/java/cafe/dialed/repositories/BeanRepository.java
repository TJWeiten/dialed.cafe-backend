package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import cafe.dialed.entities.*;
import java.util.List;
import java.util.UUID;

public interface BeanRepository extends JpaRepository<Bean, UUID> {
    List<Bean> findByUserId(UUID userId);
    long countByUserId(UUID userId);
}
