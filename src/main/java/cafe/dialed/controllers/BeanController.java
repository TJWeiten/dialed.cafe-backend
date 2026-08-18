package cafe.dialed.controllers;

import cafe.dialed.entities.Bean;
import cafe.dialed.entities.User;
import cafe.dialed.repositories.BeanRepository;
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
@RequestMapping("/beans")
public class BeanController {

    private static final int MAX_BEANS = 2500;

    private final BeanRepository beanRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(BeanController.class);

    @Autowired
    public BeanController(
            BeanRepository beanRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            CloudinaryService cloudinaryService
    ) {
        this.beanRepository = beanRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping({"", "/"})
    public List<Bean> getAllBeans(@AuthenticationPrincipal Jwt jwt) {
        String oauthId = jwt.getSubject();
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        return beanRepository.findByUserIdAndDeletedFalse(user.getId());
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Bean addBean(@RequestBody Bean bean, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BeanController:addBean");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        long beanCount = beanRepository.countByUserId(localUser.getId());
        if (beanCount >= MAX_BEANS) {
            String notificationSubject = String.format(
                    "USER HIT %s RESOURCE LIMIT",
                    BeanController.class.getSimpleName().toUpperCase()
            );
            String notificationBody = String.format(
                    "Warning! User %s hit their more than reasonable resource limit of %d. You might want to investigate.",
                    jwt.getSubject(),
                    MAX_BEANS
            );
            try {
                notificationService.sendAdminNotification(notificationSubject, notificationBody);
            } catch (ResendException e) {
                logger.warn("Admin notification for quota limit failed to send.", e);
            }
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "ERROR: You have hit the maximum amount of beans allowed to prevent abuse");
        }
        bean.setId(null);
        bean.setUser(localUser);
        return beanRepository.save(bean);
    }

    @PutMapping({"", "/"})
    public Bean saveBean(@RequestBody Bean bean, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BeanController:saveBean");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        if (bean.getId() != null) {
            Bean existingBean = beanRepository.findById(bean.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Bean not found"));
            if (!existingBean.getUser().getId().equals(localUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to edit this bean.");
            }
        }
        bean.setUser(localUser);
        return beanRepository.save(bean);
    }

    @PutMapping("/archive/{id}")
    public Bean archiveBean(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BeanController:archiveBean");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Bean existingBean = beanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Bean not found"));
        if (!existingBean.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to archive this bean.");
        }
        existingBean.setArchived(!existingBean.isArchived());
        return beanRepository.save(existingBean);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBean(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BeanController:deleteBean");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Bean existingBean = beanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Bean not found"));
        if (!existingBean.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to delete this bean.");
        }
        cloudinaryService.deleteImage("bean", existingBean.getId());
        existingBean.setDeleted(true);
        beanRepository.save(existingBean);
    }

    @DeleteMapping("/delete/img/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBeanImg(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("BeanController:deleteBeanImg");
        String oauthId = jwt.getSubject();
        User localUser = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: User record not found"));
        Bean existingBean = beanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERROR: Bean not found"));
        if (!existingBean.getUser().getId().equals(localUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ERROR: You do not have permission to delete this bean.");
        }
        cloudinaryService.deleteImage("bean", existingBean.getId());
        existingBean.setImageUrl(null);
        beanRepository.save(existingBean);
    }

}
