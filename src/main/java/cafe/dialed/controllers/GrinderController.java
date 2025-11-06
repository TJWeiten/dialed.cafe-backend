package cafe.dialed.controllers;

import cafe.dialed.entities.Grinder;
import cafe.dialed.entities.User;
import cafe.dialed.repositories.GrinderRepository;
import cafe.dialed.repositories.UserRepository;
import cafe.dialed.services.CloudinaryService;
import cafe.dialed.services.NotificationService;
import com.resend.core.exception.ResendException;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/grinders")
public class GrinderController {

    private static final int MAX_GRINDERS = 2500;

    private final GrinderRepository grinderRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(GrinderController.class);

    @Autowired
    public GrinderController(
            GrinderRepository grinderRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            CloudinaryService cloudinaryService
    ) {
        this.grinderRepository = grinderRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping({"", "/"})
    public List<Grinder> getAllGrinders(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        return grinderRepository.findByUserIdAndDeletedFalse(user.getId());
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Grinder addGrinder(@RequestBody Grinder grinder, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("GrinderController:addGrinder");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        long grinderCount = grinderRepository.countByUserId(localUser.getId());
        // Consider if users should be limited in number of entries they can upload?
        if (grinderCount >= MAX_GRINDERS) {
            String notificationSubject = String.format(
                    "USER HIT %s RESOURCE LIMIT",
                    GrinderController.class.getSimpleName().toUpperCase()
            );
            String notificationBody = String.format(
                    "Warning! User %s hit their more than reasonable resource limit of %d. You might want to investigate.",
                    jwt.getSubject(),
                    MAX_GRINDERS
            );
            try {
                notificationService.sendAdminNotification(notificationSubject, notificationBody);
            } catch (ResendException e) {
                logger.warn("Admin notification for quota limit failed to send.", e);
            }
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "ERROR: You have hit the maximum amount of grinders allowed to prevent abuse");
        }
        grinder.setId(null); // Forces creation of new entity in DB vs. allowing someone to overwrite
        grinder.setUser(localUser);
        return grinderRepository.save(grinder);
    }

    @PutMapping({"", "/"})
    public Grinder saveGrinder(@RequestBody Grinder grinder, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("GrinderController:saveGrinder");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        if (grinder.getId() != null) {
            Grinder existingGrinder = grinderRepository.findById(grinder.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Grinder not found"));
            // Someone else's grinder...?
            if (!existingGrinder.getUser().getId().equals(localUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to edit this grinder.");
            }
        }
        grinder.setUser(localUser);
        return grinderRepository.save(grinder);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGrinder(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("GrinderController:deleteGrinder");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Grinder existingGrinder = grinderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Grinder not found"));
        if (!existingGrinder.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to delete this grinder.");
        }
        cloudinaryService.deleteImage("grinder", existingGrinder.getId());
        existingGrinder.setDeleted(true);
        grinderRepository.save(existingGrinder);
    }

    @DeleteMapping("/delete/img/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGrinderImg(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("GrinderController:deleteGrinderImg");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Grinder existingGrinder = grinderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Grinder not found"));
        if (!existingGrinder.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to delete this grinder.");
        }
        cloudinaryService.deleteImage("grinder", existingGrinder.getId());
        existingGrinder.setImageUrl(null);
        grinderRepository.save(existingGrinder);
    }

}
