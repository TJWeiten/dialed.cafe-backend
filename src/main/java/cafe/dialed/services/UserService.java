package cafe.dialed.services;

import cafe.dialed.controllers.clerk.ClerkUserData;
import cafe.dialed.entities.User;
import cafe.dialed.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private User createUser(String oauthId, String email, String name, String surname) {
        return (User.builder()
                .oauthId(oauthId)
                .email(email)
                .firstName(name)
                .lastName(surname)
                .build()
        );
    }

    public User findByOauthId(String oauthId) {
        return userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
    }

    public void updateOrCreateUser(ClerkUserData data) {
        User user = userRepository.findByOauthId(data.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        if (user == null) {
            userRepository.save(createUser(
                    data.id(),
                    (String) data.emailAddresses().getFirst().get("email_address"),
                    data.firstName(),
                    data.lastName()
            ));
            return;
        }
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());
        user.setEmail((String) data.emailAddresses().getFirst().get("email_address"));
        userRepository.save(user);
    }

    public void deleteUser(ClerkUserData data) {
        User user = userRepository.findByOauthId(data.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        userRepository.delete(user);
    }

}