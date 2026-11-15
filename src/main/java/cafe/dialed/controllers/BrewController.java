package cafe.dialed.controllers;

import cafe.dialed.entities.Brew;
import cafe.dialed.entities.User;
import cafe.dialed.repositories.BrewRepository;
import cafe.dialed.repositories.UserRepository;
import cafe.dialed.services.CloudinaryService;
import cafe.dialed.services.NotificationService;
import com.resend.core.exception.ResendException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brews")
public class BrewController {

    private static final int MAX_BREWS = 5000;

    private final BrewRepository brewRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(BrewController.class);

    @Autowired
    public BrewController(
            BrewRepository brewRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            CloudinaryService cloudinaryService
    ) {
        this.brewRepository = brewRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping({"", "/"})
    public List<Brew> getAllBrews(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        return brewRepository.findByUserIdAndDeletedFalse(user.getId(), Sort.by(Sort.Direction.DESC, "brewDate"));
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Brew addBrew(@RequestBody Brew brew, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BrewController:addBrew");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        long brewCount = brewRepository.countByUserId(localUser.getId());
        if (brewCount >= MAX_BREWS) {
            String notificationSubject = String.format(
                    "USER HIT %s RESOURCE LIMIT",
                    BrewController.class.getSimpleName().toUpperCase()
            );
            String notificationBody = String.format(
                    "Warning! User %s hit their more than reasonable resource limit of %d. You might want to investigate.",
                    jwt.getSubject(),
                    MAX_BREWS
            );
            try {
                notificationService.sendAdminNotification(notificationSubject, notificationBody);
            } catch (ResendException e) {
                logger.warn("Admin notification for quota limit failed to send.", e);
            }
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "ERROR: You have hit the maximum amount of brews allowed to prevent abuse");
        }
        brew.setId(null);
        brew.setUser(localUser);
        return brewRepository.save(brew);
    }

    @PutMapping({"", "/"})
    public Brew saveBrew(@RequestBody Brew brew, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BrewController:saveBrew");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        if (brew.getId() != null) {
            Brew existingBrew = brewRepository.findById(brew.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Brew not found"));
            if (!existingBrew.getUser().getId().equals(localUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to edit this brew.");
            }
        }
        brew.setUser(localUser);
        return brewRepository.save(brew);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBrew(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BrewController:deleteBrew");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Brew existingBrew = brewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Brew not found"));
        if (!existingBrew.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to delete this brew.");
        }
        existingBrew.setDeleted(true);
        brewRepository.save(existingBrew);
    }

}
