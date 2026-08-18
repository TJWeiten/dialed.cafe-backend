package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import cafe.dialed.entities.*;
import java.util.List;
import java.util.UUID;

public interface BeanRepository extends JpaRepository<Bean, UUID> {
    List<Bean> findByUserIdAndDeletedFalse(UUID userId, Sort sort);
    long countByUserId(UUID userId);
}
