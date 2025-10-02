package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import cafe.dialed.entities.*;
import java.util.List;
import java.util.UUID;

public interface BrewRepository extends JpaRepository<Brew, UUID> {
    List<Brew> findByBeanId(UUID beanId);
}