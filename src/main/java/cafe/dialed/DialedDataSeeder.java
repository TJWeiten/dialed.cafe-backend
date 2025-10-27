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
        User userLocal = userRepository.save(User.builder()
            .oauthId("user_33V6NwRrkOS8mZt7q8L6UhyA7q1")
            .email("tsubaki@kouryuu.me")
            .firstName("Tsubaki")
            .lastName("Kouryuu")
            .build()
        );
        User userTempProd = userRepository.save(User.builder()
            .oauthId("user_33WwVuO2DfkWEe9e6WC7EiZxilV")
            .email("tsubaki@kouryuu.me")
            .firstName("Tsubaki")
            .lastName("Kouryuu")
            .build()
        );

        // --- Create Beans ---
        beanRepository.save(Bean.builder()
            .user(userLocal)
            .name("Ethiopia Guji Natural")
            .roaster("Stumptown")
            .roastLevel(RoastLevel.LIGHT)
            .archived(true)
            .roastDate(LocalDate.now().minusDays(10))
            .build()
        );
        beanRepository.save(Bean.builder()
            .user(userLocal)
            .name("Colombia La Palma")
            .roaster("Heart Roasters")
            .roastLevel(RoastLevel.MEDIUM)
            .archived(true)
            .roastDate(LocalDate.now().minusDays(30))
            .build()
        );
        beanRepository.save(Bean.builder()
            .user(userLocal)
            .name("Kenya AA Nyeri")
            .roaster("Blue Bottle")
            .roastLevel(RoastLevel.LIGHT)
            .archived(false)
            .roastDate(LocalDate.now().minusDays(5))
            .build()
        );

        // --- Create Grinders ---
        grinderRepository.save(Grinder.builder()
            .user(userLocal)
            .name("Mazzer Philos (Stepped)")
            .burrType(BurrType.FLAT)
            .isStepless(false)
            .grindRange("10 - 40")
            .notes("I189D burrs. I found most beans start around 22 at finest.")
            .imageUrl("https://res.cloudinary.com/dialed-cafe/image/upload/v1761498921/development/grinder_93a08ae5-9ba8-4489-86ed-03d02fcc6a7c.jpg")
            .build()
        );
        grinderRepository.save(Grinder.builder()
            .user(userLocal)
            .name("Mazzer Philos (Stepless)")
            .burrType(BurrType.FLAT)
            .isStepless(true)
            .grindRange("10 - 40")
            .notes("Stepless mode for Philos. I189D burrs. I found most beans start around 22 at finest.")
            .imageUrl("https://res.cloudinary.com/dialed-cafe/image/upload/v1761498900/development/grinder_96dbbc90-660c-423e-abbc-eebe85b50091.jpg")
            .build()
        );
        grinderRepository.save(Grinder.builder()
            .user(userLocal)
            .name("Weber EG-1")
            .burrType(BurrType.FLAT)
            .isStepless(true)
            .grindRange("+3.6 – +7.6 (0.1 increments)")
            .notes("The Core (DB-1) burrs are installed.")
            .build()
        );

        // --- Create Sauces with Versions ---
        Sauce caramelSauce = sauceRepository.save(Sauce.builder()
            .user(userLocal)
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