package me.clementino.holiday.mapper;

import java.time.LocalDate;
import me.clementino.holiday.domain.dop.FixedHoliday;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.MoveableFromBaseHoliday;
import me.clementino.holiday.domain.dop.MoveableHoliday;
import me.clementino.holiday.domain.dop.ObservedHoliday;
import me.clementino.holiday.dto.CreateHolidayRequestDTO;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting CreateHolidayRequestDTO to Holiday domain objects.
 *
 * <p>This mapper follows DOP principles by providing pure transformation functions that convert
 * between different data representations without side effects.
 *
 * <p>The mapper handles the sealed interface pattern by using pattern matching to determine the
 * correct Holiday implementation to create.
 */
@Mapper(componentModel = "spring")
public interface HolidayCreationMapper {

  /**
   * Convert CreateHolidayRequestDTO to Holiday using pattern matching.
   *
   * <p>This method demonstrates DOP principle of separating operations from data by providing a
   * pure transformation function.
   *
   * @param request the create request (sealed interface)
   * @return the appropriate Holiday implementation
   */
  default Holiday toHoliday(CreateHolidayRequestDTO request) {
    return switch (request) {
      case CreateHolidayRequestDTO.Fixed fixed -> toFixedHoliday(fixed);
      case CreateHolidayRequestDTO.Observed observed -> toObservedHoliday(observed);
      case CreateHolidayRequestDTO.Moveable moveable -> toMoveableHoliday(moveable);
      case CreateHolidayRequestDTO.MoveableFromBase moveableFromBase ->
          toMoveableFromBaseHoliday(moveableFromBase);
    };
  }

  /**
   * Convert Fixed request to FixedHoliday.
   *
   * @param request the fixed holiday request
   * @return FixedHoliday instance
   */
  default FixedHoliday toFixedHoliday(CreateHolidayRequestDTO.Fixed request) {
    int effectiveYear = request.year() != null ? request.year() : LocalDate.now().getYear();
    LocalDate calculatedDate = LocalDate.of(effectiveYear, request.month(), request.day());

    return new FixedHoliday(
        request.name(),
        request.description(),
        calculatedDate,
        request.day(),
        request.month(),
        request.localities(),
        request.holidayType());
  }

  /**
   * Convert Observed request to ObservedHoliday.
   *
   * @param request the observed holiday request
   * @return ObservedHoliday instance
   */
  default ObservedHoliday toObservedHoliday(CreateHolidayRequestDTO.Observed request) {
    return new ObservedHoliday(
        request.name(),
        request.description(),
        request.date(),
        request.localities(),
        request.holidayType(),
        request.observed(),
        request.mondayisation());
  }

  /**
   * Convert Moveable request to MoveableHoliday.
   *
   * @param request the moveable holiday request
   * @return MoveableHoliday instance
   */
  default MoveableHoliday toMoveableHoliday(CreateHolidayRequestDTO.Moveable request) {
    return new MoveableHoliday(
        request.name(),
        request.description(),
        request.date(),
        request.localities(),
        request.holidayType(),
        request.knownHoliday(),
        request.mondayisation());
  }

  /**
   * Convert MoveableFromBase request to MoveableFromBaseHoliday.
   *
   * @param request the moveable from base holiday request
   * @return MoveableFromBaseHoliday instance
   */
  default MoveableFromBaseHoliday toMoveableFromBaseHoliday(
      CreateHolidayRequestDTO.MoveableFromBase request) {
    return new MoveableFromBaseHoliday(
        request.name(),
        request.description(),
        request.date(),
        request.localities(),
        request.holidayType(),
        request.knownHoliday(),
        request.baseHoliday(),
        request.dayOffset(),
        request.mondayisation());
  }
}
