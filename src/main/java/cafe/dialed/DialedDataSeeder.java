package cafe.dialed;

import cafe.dialed.entities.*;
import cafe.dialed.entities.enums.*;
import cafe.dialed.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Component
@Profile({"local", "production"})
@RequiredArgsConstructor
public class DialedDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BeanRepository beanRepository;
    private final GrinderRepository grinderRepository;
    private final SauceRepository sauceRepository;
    private final SauceVersionRepository sauceVersionRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Development Profile Active: Seeding database with CommandLineRunner... ---");

        // --- Create Users ---
        User user1 = userRepository.save(User.builder()
            .oauthId("user_33V6NwRrkOS8mZt7q8L6UhyA7q1")
            .email("tsubaki@kouryuu.me")
            .firstName("Tsubaki")
            .lastName("Kouryuu")
            .build()
        );

        User user2 = userRepository.save(User.builder()
            .oauthId("987654321")
            .email("hello@tjweiten.com")
            .firstName("T.J.")
            .lastName("Weiten")
            .build()
        );

        User user3 = userRepository.save(User.builder()
            .oauthId("sasd_987654321231")
            .email("emptyemptyphil@empty.com")
            .firstName("Empty")
            .lastName("Phil")
            .build()
        );

        // --- Create Beans ---
        beanRepository.save(Bean.builder()
            .user(user1)
            .name("Ethiopia Guji Natural")
            .roaster("Stumptown")
            .roastLevel(RoastLevel.LIGHT)
            .isArchived(false)
            .roastDate(LocalDate.now().minusDays(10))
            .build()
        );
        beanRepository.save(Bean.builder()
            .user(user1)
            .name("Colombia La Palma")
            .roaster("Heart Roasters")
            .roastLevel(RoastLevel.MEDIUM)
            .isArchived(true)
            .roastDate(LocalDate.now().minusDays(30))
            .build()
        );
        beanRepository.save(Bean.builder()
            .user(user2)
            .name("Kenya AA Nyeri")
            .roaster("Blue Bottle")
            .roastLevel(RoastLevel.LIGHT)
            .isArchived(false)
            .roastDate(LocalDate.now().minusDays(5))
            .build()
        );

        // --- Create Grinders ---
        grinderRepository.save(Grinder.builder()
            .user(user1)
            .name("Mazzer Philos")
            .burrType(BurrType.FLAT)
            .isStepless(true)
            .notes("Primary espresso grinder.")
            .build()
        );
        grinderRepository.save(Grinder.builder()
            .user(user1)
            .name("Comandante C40")
            .burrType(BurrType.CONICAL)
            .isStepless(false)
            .notes("For pour-overs and travel.")
            .build()
        );
        grinderRepository.save(Grinder.builder()
            .user(user2)
            .name("Baratza Encore")
            .burrType(BurrType.CONICAL)
            .isStepless(false)
            .notes("Daily workhorse grinder.")
            .build()
        );

        // --- Create Sauces with Versions ---
        Sauce caramelSauce = sauceRepository.save(Sauce.builder()
            .user(user1)
            .name("Caramel Drizzle")
            .build()
        );

        SauceVersion caramelV1 = (SauceVersion.builder()
            .sauce(caramelSauce)
            .recipe("Original recipe: 1 cup sugar, 1/2 cup water.")
            .build()
        );
        SauceVersion caramelV2 = (SauceVersion.builder()
            .sauce(caramelSauce)
            .recipe("Updated: 1 cup brown sugar, pinch of sea salt.")
            .build()
        );

        sauceVersionRepository.saveAll(List.of(caramelV1, caramelV2));

        caramelSauce.setLatestVersion(caramelV2);
        sauceRepository.save(caramelSauce);

        System.out.println("--- Database seeding complete. ---");

    }

}