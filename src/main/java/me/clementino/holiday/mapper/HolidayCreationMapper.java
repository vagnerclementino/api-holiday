package me.clementino.holiday.mapper;

import me.clementino.holiday.domain.dop.FixedHoliday;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.MoveableFromBaseHoliday;
import me.clementino.holiday.domain.dop.MoveableHoliday;
import me.clementino.holiday.domain.dop.ObservedHoliday;
import me.clementino.holiday.dto.CreateHolidayRequest;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting CreateHolidayRequest to Holiday domain objects.
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
   * Convert CreateHolidayRequest to Holiday using pattern matching.
   *
   * <p>This method demonstrates DOP principle of separating operations from data by providing a
   * pure transformation function.
   *
   * @param request the create request (sealed interface)
   * @return the appropriate Holiday implementation
   */
  default Holiday toHoliday(CreateHolidayRequest request) {
    return switch (request) {
      case CreateHolidayRequest.Fixed fixed -> toFixedHoliday(fixed);
      case CreateHolidayRequest.Observed observed -> toObservedHoliday(observed);
      case CreateHolidayRequest.Moveable moveable -> toMoveableHoliday(moveable);
      case CreateHolidayRequest.MoveableFromBase moveableFromBase ->
          toMoveableFromBaseHoliday(moveableFromBase);
    };
  }

  /**
   * Convert Fixed request to FixedHoliday.
   *
   * @param request the fixed holiday request
   * @return FixedHoliday instance
   */
  default FixedHoliday toFixedHoliday(CreateHolidayRequest.Fixed request) {
    return new FixedHoliday(
        request.name(),
        request.description(),
        request.date(),
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
  default ObservedHoliday toObservedHoliday(CreateHolidayRequest.Observed request) {
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
  default MoveableHoliday toMoveableHoliday(CreateHolidayRequest.Moveable request) {
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
      CreateHolidayRequest.MoveableFromBase request) {
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
