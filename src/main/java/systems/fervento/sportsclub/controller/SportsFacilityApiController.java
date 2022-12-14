package systems.fervento.sportsclub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import systems.fervento.sportsclub.data.SportsFacilityData;
import systems.fervento.sportsclub.exception.PreconditionViolationException;
import systems.fervento.sportsclub.mapper.ReservationSummaryApiMapper;
import systems.fervento.sportsclub.mapper.SportsFacilityApiMapper;
import systems.fervento.sportsclub.mapper.SportsFieldApiMapper;
import systems.fervento.sportsclub.openapi.api.SportsFacilitiesApi;
import systems.fervento.sportsclub.openapi.model.ReservationsSummary;
import systems.fervento.sportsclub.openapi.model.SportsFacility;
import systems.fervento.sportsclub.openapi.model.SportsFacilityWithSportsFields;
import systems.fervento.sportsclub.openapi.model.SportsField;
import systems.fervento.sportsclub.service.ReservationSummaryService;
import systems.fervento.sportsclub.service.SportsFacilityService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
public class SportsFacilityApiController implements SportsFacilitiesApi {
    private final SportsFacilityService sportsFacilityService;
    private final ReservationSummaryService reservationSummaryService;
    private final SportsFacilityApiMapper sportsFacilityApiMapper;
    private final SportsFieldApiMapper sportsFieldApiMapper;
    private final ReservationSummaryApiMapper reservationSummaryApiMapper;

    public SportsFacilityApiController(
        SportsFacilityService sportsFacilityService,
        ReservationSummaryService reservationSummaryService,
        SportsFacilityApiMapper sportsFacilityApiMapper,
        SportsFieldApiMapper sportsFieldApiMapper,
        ReservationSummaryApiMapper reservationSummaryApiMapper
    ) {
        this.sportsFacilityService = sportsFacilityService;
        this.reservationSummaryService = reservationSummaryService;
        this.sportsFacilityApiMapper = sportsFacilityApiMapper;
        this.sportsFieldApiMapper = sportsFieldApiMapper;
        this.reservationSummaryApiMapper = reservationSummaryApiMapper;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return SportsFacilitiesApi.super.getRequest();
    }

    @Override
    public ResponseEntity<SportsField> createSportsField(Long sportsFacilityId, SportsField sportsField) {
        var sportsFieldData = sportsFieldApiMapper.mapToSportsFieldData(sportsField);
        var createdSportsFieldData = sportsFacilityService.createSportsField(sportsFacilityId, sportsFieldData);
        return new ResponseEntity<>(
            sportsFieldApiMapper.mapToSportsFieldApi(createdSportsFieldData),
            HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<List<SportsFacility>> getSportsFacilities(
        Long filterByOwnerId,
        Integer totalSportsFieldGt,
        Integer totalSportsFieldLt
    ) {
        final Optional<Long> filterByOwnerIdQueryParam = Optional.ofNullable(filterByOwnerId);
        final int maxTotalSportsFields = (totalSportsFieldLt == null) ? Integer.MAX_VALUE : totalSportsFieldLt;
        final int minTotalSportsFields = (totalSportsFieldGt == null) ? -1 : totalSportsFieldGt;

        if (minTotalSportsFields > maxTotalSportsFields) {
            throw new PreconditionViolationException("totalSportsFieldLt must be greater than or equal to totalSportsFieldGt!");
        }

        final List<SportsFacilityData> sportsFacilities = (filterByOwnerIdQueryParam.isPresent())
            ? sportsFacilityService.getAllByOwnerIdAndTotalNumberSportsFieldBetween(
                minTotalSportsFields,
                maxTotalSportsFields,
                filterByOwnerIdQueryParam.get()
            )
            : sportsFacilityService.getAllByTotalNumberSportsFieldBetween(minTotalSportsFields, maxTotalSportsFields);

        return ResponseEntity.ok(sportsFacilities.stream()
            .map(sportsFacilityApiMapper::map)
            .collect(toList())
        );
    }

    @Override
    public ResponseEntity<SportsFacilityWithSportsFields> getSportsFacilityById(Long sportsFacilityId) {
        return ResponseEntity.ok(
            sportsFacilityApiMapper.mapToSportsFacilityWithSportsFields(
                sportsFacilityService.getById(sportsFacilityId)
            )
        );
    }

    @Override
    public ResponseEntity<ReservationsSummary> generateReservationsSummary(
        Long sportsFacilityId,
        ZonedDateTime startDate,
        ZonedDateTime endDate,
        String sortBy
    ) {
        final var generatedReservationsSummary = reservationSummaryService
            .generateReservationsSummaryForSportsFacility(
                sortBy,
                startDate,
                endDate,
                sportsFacilityId
            );

        return new ResponseEntity<>(
            reservationSummaryApiMapper.mapToReservationsSummaryApi(generatedReservationsSummary),
            HttpStatus.CREATED
        );
    }
}
