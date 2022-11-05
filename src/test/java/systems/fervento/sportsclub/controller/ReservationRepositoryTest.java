package systems.fervento.sportsclub.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import systems.fervento.sportsclub.entity.ReservationStatus;

import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;


@SpringBootTest
@DisplayName("Reservation Repository")
public class ReservationRepositoryTest extends SpringDataJpaTest {

    private final static PageRequest pagable = PageRequest.of(0, 10);

    @Test
    @DisplayName("when findAll with status pending and sport soccer as filtering parameters then return two reservations")
    void testGetAllReservationsFilteredByStatusAndSport() {
        var reservations =
            reservationRepository.findAll(
                pagable, ReservationStatus.PENDING, null, "soccer",
                null, null, null,
                null, null
            );
        assertThat(reservations.getContent(), hasSize(2));
    }

    @Test
    @DisplayName("when findAll with price 10.0 as filtering parameter then return three reservations")
    void testGetAllReservationsFilteredByPrice() {
        var reservations =
            reservationRepository.findAll(
                pagable, null, 10.0f, null,
                null, null, null,
                null, null
        );
        assertThat(reservations.getContent(), hasSize(3));
    }

    @Test
    @DisplayName("when findAll with ZonedDateTime.now() start date filtering parameter then return three reservations")
    void testGetAllReservationsFilteredByStartDate() {
        var reservations =
            reservationRepository.findAll(
                pagable, null, null, null, null,
                ZonedDateTime.now().minusSeconds(1), null, null,
                null
            );
        assertThat(reservations.getContent(), hasSize(3));
    }

    @Test
    @DisplayName("when findAll with status accepted as filtering parameter then return one reservation")
    void testGetAllReservationsFilteredByStatus() {
        var reservations =
            reservationRepository.findAll(
            pagable, ReservationStatus.ACCEPTED, null, null,
            null, null, null, null, null
            );
        assertThat(reservations.getContent(), hasSize(1));
    }

    @Test
    @DisplayName("when findAll between two dates then return one reservation")
    void testGetAllReservationsBetweenTwoDates() {
        var reservations =
            reservationRepository.findAll(
                pagable, null, null, null, null,
                ZonedDateTime.now().minusDays(3).minusMinutes(1), ZonedDateTime.now().minusDays(2), null, null
            );
        assertThat(reservations.getContent(), hasSize(1));
    }

    @Test
    @DisplayName("when findAll by sports facility id then return two reservation")
    void testGetAllReservationBySportsFacility() {
        var reservations =
            reservationRepository.findAll(
                pagable, null,null, null, null,
        null, null, null, sportsFacilityEntity1.getId()
            );
        assertThat(reservations.getContent(), hasSize(2));
    }

    @Test
    @DisplayName("when findAll asc sorted by createdAt property then return sorted reservations")
    void testGetAllReservationsSortedByCreatedAt() {

    }
}
