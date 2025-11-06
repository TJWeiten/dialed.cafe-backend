package cafe.dialed.controllers;

import cafe.dialed.dtos.SauceRequestDTO;
import cafe.dialed.entities.Sauce;
import cafe.dialed.entities.SauceVersion;
import cafe.dialed.entities.User;
import cafe.dialed.repositories.SauceRepository;
import cafe.dialed.repositories.SauceVersionRepository;
import cafe.dialed.repositories.UserRepository;
import cafe.dialed.services.CloudinaryService;
import cafe.dialed.services.NotificationService;
import com.resend.core.exception.ResendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sauces")
public class SauceController {

    private static final int MAX_SAUCES = 2500;

    private final SauceRepository sauceRepository;
    private final SauceVersionRepository sauceVersionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(SauceController.class);

    @Autowired
    public SauceController(
            SauceRepository sauceRepository,
            SauceVersionRepository sauceVersionRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            CloudinaryService cloudinaryService
    ) {
        this.sauceRepository = sauceRepository;
        this.sauceVersionRepository = sauceVersionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping({"", "/"})
    public List<Sauce> getAllSauces(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        return sauceRepository.findByUserIdAndDeletedFalse(user.getId());
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Sauce addSauce(@RequestBody SauceRequestDTO sauce, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("SauceController:addSauce");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        long sauceCount = sauceRepository.countByUserId(localUser.getId());
        // Consider if users should be limited in number of entries they can upload?
        if (sauceCount >= MAX_SAUCES) {
            String notificationSubject = String.format(
                    "USER HIT %s RESOURCE LIMIT",
                    SauceController.class.getSimpleName().toUpperCase()
            );
            String notificationBody = String.format(
                    "Warning! User %s hit their more than reasonable resource limit of %d. You might want to investigate.",
                    jwt.getSubject(),
                    MAX_SAUCES
            );
            try {
                notificationService.sendAdminNotification(notificationSubject, notificationBody);
            } catch (ResendException e) {
                logger.warn("Admin notification for quota limit failed to send.", e);
            }
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "ERROR: You have hit the maximum amount of sauces allowed to prevent abuse");
        }
        // Build new Sauce
        Sauce newSauce = (Sauce.builder()
                .name(sauce.getName())
                .user(localUser)
                .imageUrl(sauce.getImageUrl())
                .build()
        );
        Sauce savedSauce = sauceRepository.save(newSauce); // Commit to DB
        // Build new SauceVersion
        SauceVersion sauceVersion = (SauceVersion.builder()
                .sauce(savedSauce)
                .recipe(sauce.getRecipe())
                .createdAt(LocalDateTime.now())
                .build()
        );
        savedSauce.setLatestVersion(
                sauceVersionRepository.save(sauceVersion) // Commit to DB
        ); // Update new Sauce's reference
        return sauceRepository.save(savedSauce); // Recommit to DB
    }

    @PutMapping({"", "/"})
    public Sauce saveSauce(@RequestBody SauceRequestDTO sauce, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("SauceController:saveSauce");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Sauce existingSauce;
        if (sauce.getId() != null) {
            existingSauce = sauceRepository.findById(sauce.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Sauce not found"));
            // Someone else's sauce...?
            if (!existingSauce.getUser().getId().equals(localUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to edit this sauce.");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Sauce not found");
        }
        // Update name if it has changed
        if (!sauce.getName().equals(existingSauce.getName())) {
            existingSauce.setName(sauce.getName());
        }
        // Image has been added or changed
        if (
                sauce.getImageUrl() == null ||
                !sauce.getImageUrl().equals(existingSauce.getImageUrl())
        ) {
            existingSauce.setImageUrl(sauce.getImageUrl());
        }
        // Recipe has changed, so we create a new SauceVersion
        if (
                existingSauce.getLatestVersion() == null ||
                !sauce.getRecipe().equals(
                        existingSauce.getLatestVersion().getRecipe()
                )
        ) {
            SauceVersion newSauceVersion = (SauceVersion.builder()
                    .sauce(existingSauce)
                    .recipe(sauce.getRecipe())
                    .createdAt(LocalDateTime.now())
                    .build()
            );
            existingSauce.setLatestVersion(sauceVersionRepository.save(newSauceVersion));
        }
        return sauceRepository.save(existingSauce);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSauce(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("SauceController:deleteSauce");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Sauce existingSauce = sauceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Sauce not found"));
        if (!existingSauce.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to delete this sauce.");
        }
        cloudinaryService.deleteImage("sauce", existingSauce.getId());
        existingSauce.setDeleted(true);
        sauceRepository.save(existingSauce);
    }

    @DeleteMapping("/delete/img/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSauceImg(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("SauceController:deleteSauceImg");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Sauce existingSauce = sauceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Sauce not found"));
        if (!existingSauce.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to delete this sauce.");
        }
        cloudinaryService.deleteImage("sauce", existingSauce.getId());
        existingSauce.setImageUrl(null);
        sauceRepository.save(existingSauce);
    }

}
