package cafe.dialed.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import cafe.dialed.entities.*;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByOauthId(String oauthId);
}
