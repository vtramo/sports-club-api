package systems.fervento.sportsclub.mapper;

import org.mapstruct.Mapper;
import systems.fervento.sportsclub.data.SportsReservationReportData;
import systems.fervento.sportsclub.openapi.model.SportsReservationReport;

@Mapper(componentModel = "spring", uses = SportsEnumMapper.class)
public interface SportsReservationReportApiMapper {

    SportsReservationReport mapToSportsReservationReport(SportsReservationReportData sportsReservationReportData);
}
