package systems.fervento.sportsclub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import systems.fervento.sportsclub.entity.*;
import systems.fervento.sportsclub.repository.NotificationRepository;
import systems.fervento.sportsclub.repository.ReservationRepository;
import systems.fervento.sportsclub.repository.SportsFacilityRepository;
import systems.fervento.sportsclub.repository.UserRepository;

import java.time.ZonedDateTime;
import java.util.List;

@SpringBootApplication(
    scanBasePackages = "systems.fervento.sportsclub"
)
@EnableScheduling
public class SportsClubApp implements ApplicationRunner {
    final SportsFacilityRepository sportsFacilityRepository;
    final NotificationRepository notificationRepository;
    final ReservationRepository reservationRepository;
    final UserRepository userRepository;

    @Value(value = "${INIT_DATABASE:false}")
    private String initDatabase;

    public SportsClubApp(
        SportsFacilityRepository sportsFacilityRepository,
        NotificationRepository notificationRepository,
        ReservationRepository reservationRepository,
        UserRepository userRepository
    ) {
        this.sportsFacilityRepository = sportsFacilityRepository;
        this.notificationRepository = notificationRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SportsClubApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        if (Boolean.parseBoolean(initDatabase)) {
            initDatabase();
        }
    }

    void initDatabase() {
        Address address = new Address();
        address.setStreetNumber("32");
        address.setStreetName("Via Vittorio Bachelet");
        City city = new City();
        city.setCountry("Italia");
        city.setName("Afragola (NA)");
        city.setPostalCode("80021");
        address.setCity(city);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("bonek");
        userEntity.setPassword("ciao");
        userEntity.setFirstName("Vincenzo");
        userEntity.setLastName("Tramo");
        userEntity.setFiscalCode("TRMVCN99C11E791Y");
        userEntity.setEmail("vv.tramo@gmail.com");
        userEntity.setHomeAddress(address);

        CreditCardEntity creditCardEntity = new CreditCardEntity("AAAAAAAAAAAAAAAA", "BBBBB", "CCCC");
        userEntity.addBillingDetails(creditCardEntity);
        creditCardEntity.setOwner(userEntity);

        SportsFacilityEntity sportsFacilityEntity1 = new SportsFacilityEntity("Sports Club 2022", "666");
        sportsFacilityEntity1.setAddress(address);
        userEntity.addSportsFacility(sportsFacilityEntity1);
        sportsFacilityEntity1.setOwner(userEntity);

        SportsFacilityEntity sportsFacilityEntity2 = new SportsFacilityEntity("New Sports Club 2022", "777");
        sportsFacilityEntity2.setAddress(address);
        sportsFacilityEntity2.setOwner(userEntity);

        SportsFieldEntity sportsFieldEntity1 = new SoccerFieldEntity("Eden", SoccerFieldType.ELEVEN_A_SIDE, true);
        SportsFieldEntity sportsFieldEntity2 = new BasketballFieldEntity("Basket Field", false);
        SportsFieldEntity sportsFieldEntity3 = new TennisFieldEntity("Tennis Field", TennisFieldType.CEMENT, false);
        sportsFieldEntity1.setSportsFacility(sportsFacilityEntity1);
        sportsFieldEntity1.setPriceList(new SportsFieldPriceListEntity(0, 0));
        sportsFieldEntity2.setSportsFacility(sportsFacilityEntity2);
        sportsFieldEntity2.setPriceList(new SportsFieldPriceListEntity(0, 0));
        sportsFieldEntity3.setSportsFacility(sportsFacilityEntity2);
        sportsFieldEntity3.setPriceList(new SportsFieldPriceListEntity(0, 0));

        sportsFacilityEntity1.getSportsFields().add(sportsFieldEntity1);
        sportsFacilityEntity2.getSportsFields().add(sportsFieldEntity2);
        sportsFacilityEntity2.getSportsFields().add(sportsFieldEntity3);

        SportsFieldPriceListEntity sportsFieldPriceListEntity = new SportsFieldPriceListEntity(75.0f, 5.0f);
        sportsFieldEntity1.setPriceList(sportsFieldPriceListEntity);

        List<NotificationEntity> notificationsUserEntity1 = List.of(
            new NotificationEntity(null, ZonedDateTime.now(), true, "ciao", userEntity),
            new NotificationEntity(null, ZonedDateTime.now(), false, "ciao2", userEntity),
            new NotificationEntity(null, ZonedDateTime.now(), false, "ciao3", userEntity)
        );

        var dateTimeRange = new DateTimeRange(ZonedDateTime.now(), ZonedDateTime.now().plusDays(1));
        var dateTimeRange2 = new DateTimeRange(ZonedDateTime.now().minusDays(3), ZonedDateTime.now().minusDays(2));
        List<ReservationEntity> reservationsUserEntity1 = List.of(
            new ReservationEntity(dateTimeRange, 10f, userEntity),
            new ReservationEntity(dateTimeRange, 10f, userEntity),
            new ReservationEntity(dateTimeRange, 10f, userEntity),
            new ReservationEntity(dateTimeRange2, 30f, userEntity)
        );
        reservationsUserEntity1.get(0).setSportsField(sportsFieldEntity1);
        reservationsUserEntity1.get(1).setSportsField(sportsFieldEntity1);
        reservationsUserEntity1.get(2).setSportsField(sportsFieldEntity2);
        reservationsUserEntity1.get(3).setReservationStatus(ReservationStatus.ACCEPTED);
        reservationsUserEntity1.get(3).setSportsField(sportsFieldEntity3);

        ReservationRatingEntity reservationRatingEntity1 = new ReservationRatingEntity();
        reservationRatingEntity1.setDescription("Wonderful");
        reservationRatingEntity1.setScore(4);

        ReservationRatingEntity reservationRatingEntity2 = new ReservationRatingEntity();
        reservationRatingEntity2.setDescription("Beautiful");
        reservationRatingEntity2.setScore(5);

        reservationsUserEntity1.get(0).setRating(reservationRatingEntity1);
        reservationsUserEntity1.get(1).setRating(reservationRatingEntity2);

        ReservationRatingEntity reservationRatingEntity3 = new ReservationRatingEntity();
        reservationRatingEntity3.setDescription(" ");
        reservationRatingEntity3.setScore(3);

        reservationsUserEntity1.get(3).setRating(reservationRatingEntity3);

        userRepository.save(userEntity);
        notificationRepository.saveAll(notificationsUserEntity1);
        sportsFacilityRepository.save(sportsFacilityEntity2);
        sportsFacilityRepository.save(sportsFacilityEntity1);
        reservationRepository.saveAll(reservationsUserEntity1);
    }
}
