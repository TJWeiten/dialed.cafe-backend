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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@Profile({"local", "production"})
@RequiredArgsConstructor
public class DialedDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BeanRepository beanRepository;
    private final GrinderRepository grinderRepository;
    private final SauceRepository sauceRepository;
    private final SauceVersionRepository sauceVersionRepository;
    private final BrewRepository brewRepository;

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
            .name("Gamut")
            .roaster("Chromatic Coffee Co.")
            .roastLevel(RoastLevel.MEDIUM)
            .archived(false)
            .roastDate(LocalDate.now().minusDays(3))
            .currentWeight(Double.valueOf(1000))
            .packageWeight(Double.valueOf(1000))
            .imageUrl("https://res.cloudinary.com/dialed-cafe/image/upload/v1787095806/development/bean_b5eaedd4-c9c2-4c52-b8ec-88df7d1bb96f.jpg")
            .build()
        );
        beanRepository.save(Bean.builder()
            .user(userLocal)
            .name("Ethiopia Guji Natural")
            .roaster("Stumptown")
            .roastLevel(RoastLevel.LIGHT)
            .archived(true)
            .roastDate(LocalDate.now().minusDays(10))
            .currentWeight(Double.valueOf(0))
            .packageWeight(Double.valueOf(1000))
            .build()
        );
        beanRepository.save(Bean.builder()
            .user(userLocal)
            .name("Colombia La Palma")
            .roaster("Heart Roasters")
            .roastLevel(RoastLevel.MEDIUM)
            .archived(true)
            .roastDate(LocalDate.now().minusDays(30))
            .currentWeight(Double.valueOf(0))
            .packageWeight(Double.valueOf(1000))
            .build()
        );
        beanRepository.save(Bean.builder()
            .user(userLocal)
            .name("Kenya AA Nyeri")
            .roaster("Blue Bottle")
            .roastLevel(RoastLevel.LIGHT)
            .archived(false)
            .roastDate(LocalDate.now().minusDays(5))
            .currentWeight(Double.valueOf(340))
            .packageWeight(Double.valueOf(1000))
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
        grinderRepository.save(Grinder.builder()
            .user(userLocal)
            .name("BOOKOO Motto80")
            .burrType(BurrType.FLAT)
            .isStepless(true)
            .grindRange("25 - 1500μm")
            .notes("Using SweetCore80 Burrs, designed for espresso (as opposed to Omni80 burrs for filter and espresso). Adjustment is per μm. Variable from 300-1500 RPM. Can also adjust bean feed from approx. 0.5-2g/s.")
            .imageUrl("https://res.cloudinary.com/dialed-cafe/image/upload/v1787108950/development/grinder_8d1fde53-a670-41d7-8c3e-2ae2c3141d7e.jpg")
            .build()
        );

        // --- Create Sauces with Versions ---
        Sauce caramelSauce = sauceRepository.save(Sauce.builder()
            .user(userLocal)
            .name("Caramel Drizzle")
            .imageUrl("https://res.cloudinary.com/dialed-cafe/image/upload/v1762382589/development/sauce_9a933f99-06b6-4165-b3ec-8bbbc30df9b2.jpg")
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

        Sauce mochaSauce = sauceRepository.save(Sauce.builder()
                .user(userLocal)
                .name("Mocha Sauce")
                .build()
        );
        SauceVersion mochaVersion = (SauceVersion.builder()
                .sauce(mochaSauce)
                .recipe("1 cup sugar, 1/2 cup water.\nA boat load of...\n\nEXPENSIVE\n\nchocolate!")
                .build()
        );

        sauceVersionRepository.saveAll(List.of(mochaVersion));

        caramelSauce.setLatestVersion(caramelV2);
        mochaSauce.setLatestVersion(mochaVersion);
        sauceRepository.save(caramelSauce);
        sauceRepository.save(mochaSauce);

        // --- Create Brews ---
        Bean gamut = beanRepository.findAll().stream()
            .filter(b -> b.getName().equals("Gamut"))
            .findFirst().orElseThrow();
        Bean ethiopiaGuji = beanRepository.findAll().stream()
            .filter(b -> b.getName().equals("Ethiopia Guji Natural"))
            .findFirst().orElseThrow();
        Bean colombiaPalma = beanRepository.findAll().stream()
            .filter(b -> b.getName().equals("Colombia La Palma"))
            .findFirst().orElseThrow();
        Bean kenyaAA = beanRepository.findAll().stream()
            .filter(b -> b.getName().equals("Kenya AA Nyeri"))
            .findFirst().orElseThrow();

        Grinder mazzerStepped = grinderRepository.findAll().stream()
            .filter(g -> g.getName().equals("Mazzer Philos (Stepped)"))
            .findFirst().orElseThrow();
        Grinder mazzerStepless = grinderRepository.findAll().stream()
            .filter(g -> g.getName().equals("Mazzer Philos (Stepless)"))
            .findFirst().orElseThrow();
        Grinder weberEG1 = grinderRepository.findAll().stream()
            .filter(g -> g.getName().equals("Weber EG-1"))
            .findFirst().orElseThrow();
        Grinder bookooMotto = grinderRepository.findAll().stream()
            .filter(g -> g.getName().equals("BOOKOO Motto80"))
            .findFirst().orElseThrow();

        brewRepository.save(Brew.builder()
            .user(userLocal).bean(gamut).grinder(mazzerStepped)
            .brewDate(Instant.now().minusSeconds(86400).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(36.5).timePulled(28).grindSetting("22")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Bright, floral, stone fruit finish")
            .waterTemp(93).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.ESPRESSO)
            .copilotComment("Great extraction! Consider extending the shot by 2 seconds next time to pull out more sweetness.")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(gamut).grinder(mazzerStepped)
            .brewDate(Instant.now().minusSeconds(86400 + 3600).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.2).weightOut(40.0).timePulled(31).grindSetting("21")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Chocolate, caramel, great with oat milk")
            .waterTemp(94).preinfusionTime(5).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.LATTE).milkType(MilkType.OAT).milkAmount(180)
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(ethiopiaGuji).grinder(mazzerStepped)
            .brewDate(Instant.now().minusSeconds(2 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(17.5).weightOut(35.0).timePulled(26).grindSetting("23")
            .rating(Rating.NEUTRAL)
            .tastingNotes("A bit sour, under-extracted")
            .waterTemp(92).basketType(BasketType.STANDARD_DOUBLE)
            .madeAs(DrinkType.ESPRESSO)
            .copilotComment("Try grinding finer or extending the extraction time. The shot ran 2 seconds short.")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(ethiopiaGuji).grinder(mazzerStepped)
            .brewDate(Instant.now().minusSeconds(3 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(38.0).timePulled(29).grindSetting("22")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Blueberry, jammy, sweet")
            .waterTemp(93).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.ESPRESSO)
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(colombiaPalma).grinder(mazzerStepless)
            .brewDate(Instant.now().minusSeconds(4 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.5).weightOut(37.0).timePulled(30).grindSetting("24.5")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Nutty, smooth, balanced")
            .waterTemp(93).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.MILK_DRINK).milkType(MilkType.WHOLE).milkAmount(120)
            .sauceVersion(caramelV2)
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(colombiaPalma).grinder(mazzerStepless)
            .brewDate(Instant.now().minusSeconds(5 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(36.0).timePulled(27).grindSetting("25")
            .rating(Rating.THUMBS_DOWN)
            .tastingNotes("Bitter, over-extracted")
            .waterTemp(95).basketType(BasketType.STANDARD_DOUBLE)
            .madeAs(DrinkType.ESPRESSO)
            .copilotComment("Grind coarser — the shot pulled too fast at 27s with a 1:2 ratio. Aim for 30s.")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(kenyaAA).grinder(weberEG1)
            .brewDate(Instant.now().minusSeconds(6 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(42.0).timePulled(33).grindSetting("+5.2")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Blackcurrant, tomato, bright acidity")
            .waterTemp(94).basketType(BasketType.PRECISION_TRIPLE)
            .madeAs(DrinkType.ESPRESSO)
            .copilotComment("Excellent ratio for this bean. The extended yield brought out the fruitiness.")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(kenyaAA).grinder(weberEG1)
            .brewDate(Instant.now().minusSeconds(7 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(36.0).timePulled(30).grindSetting("+5.4")
            .rating(Rating.NEUTRAL)
            .tastingNotes("Grapefruit, slightly harsh")
            .waterTemp(93).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.AMERICANO)
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(gamut).grinder(bookooMotto)
            .brewDate(Instant.now().minusSeconds(8 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(36.0).timePulled(28).grindSetting("450")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Sweet, caramel, clean")
            .waterTemp(93).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.ESPRESSO)
            .puckPrepNotes("WDT with 0.35mm needles, 30s bloom")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(gamut).grinder(bookooMotto)
            .brewDate(Instant.now().minusSeconds(9 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(40.0).timePulled(32).grindSetting("430")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Toffee, brown sugar, long finish")
            .waterTemp(94).preinfusionTime(8).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.LATTE).milkType(MilkType.OAT).milkAmount(200)
            .sauceVersion(mochaVersion)
            .copilotComment("The extended pre-infusion really helped with even extraction on this one.")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(colombiaPalma).grinder(mazzerStepped)
            .brewDate(Instant.now().minusSeconds(10 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(17.0).weightOut(34.0).timePulled(25).grindSetting("24")
            .rating(Rating.THUMBS_DOWN)
            .tastingNotes("Sour, thin, under-extracted")
            .waterTemp(91).basketType(BasketType.STANDARD_SINGLE)
            .madeAs(DrinkType.ESPRESSO)
            .copilotComment("Grind finer — 25s is too fast for a 1:2 ratio. Try setting 22.")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(ethiopiaGuji).grinder(weberEG1)
            .brewDate(Instant.now().minusSeconds(11 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.5).weightOut(37.0).timePulled(30).grindSetting("+5.0")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Peach, jasmine, tea-like")
            .waterTemp(92).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.MILK_DRINK).milkType(MilkType.WHOLE).milkAmount(130)
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(kenyaAA).grinder(bookooMotto)
            .brewDate(Instant.now().minusSeconds(12 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(36.5).timePulled(29).grindSetting("460")
            .rating(Rating.NEUTRAL)
            .tastingNotes("Citrus, a little sharp")
            .waterTemp(93).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.ESPRESSO)
            .waterType("Third Wave Water")
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(gamut).grinder(mazzerStepless)
            .brewDate(Instant.now().minusSeconds(13 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(36.0).timePulled(28).grindSetting("23.8")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Classic, well-balanced shot")
            .waterTemp(93).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.MILK_DRINK).milkType(MilkType.TWO_PERCENT).milkAmount(60)
            .build()
        );
        brewRepository.save(Brew.builder()
            .user(userLocal).bean(colombiaPalma).grinder(weberEG1)
            .brewDate(Instant.now().minusSeconds(14 * 86400L).truncatedTo(java.time.temporal.ChronoUnit.MINUTES))
            .weightIn(18.0).weightOut(38.5).timePulled(31).grindSetting("+5.6")
            .rating(Rating.THUMBS_UP)
            .tastingNotes("Milk chocolate, almond, smooth")
            .waterTemp(94).basketType(BasketType.PRECISION_DOUBLE)
            .madeAs(DrinkType.MILK_DRINK).milkType(MilkType.OAT).milkAmount(140)
            .sauceVersion(caramelV1)
            .copilotComment("Note: this used the older caramel recipe. The updated version with brown sugar would pair even better.")
            .build()
        );

        System.out.println("--- Database seeding complete. ---");

    }

}
