package me.clementino.holiday.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import me.clementino.holiday.domain.dop.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("LocationInfoDTO Tests")
@Tag("unit")
class LocationInfoDTOTest {

  @Test
  @DisplayName("Should create LocationInfoDTO from Location with all fields")
  void shouldCreateLocationInfoFromLocationWithAllFields() {
    Location location = new Location("Brazil", Optional.of("São Paulo"), Optional.of("São Paulo"));

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("BR");
    assertThat(locationInfoDTO.subdivision()).isEqualTo("São Paulo");
    assertThat(locationInfoDTO.city()).isEqualTo("São Paulo");
    assertThat(locationInfoDTO.pretty()).isEqualTo("São Paulo, São Paulo, Brazil");
  }

  @Test
  @DisplayName("Should create LocationInfoDTO from Location with only country")
  void shouldCreateLocationInfoFromLocationWithOnlyCountry() {
    Location location = new Location("Brazil", Optional.empty(), Optional.empty());

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("BR");
    assertThat(locationInfoDTO.subdivision()).isNull();
    assertThat(locationInfoDTO.city()).isNull();
    assertThat(locationInfoDTO.pretty()).isEqualTo("Brazil");
  }

  @Test
  @DisplayName("Should create LocationInfoDTO from Location with country and subdivision")
  void shouldCreateLocationInfoFromLocationWithCountryAndSubdivision() {
    Location location = new Location("United States", Optional.of("California"), Optional.empty());

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("US");
    assertThat(locationInfoDTO.subdivision()).isEqualTo("California");
    assertThat(locationInfoDTO.city()).isNull();
    assertThat(locationInfoDTO.pretty()).isEqualTo("California, United States");
  }

  @Test
  @DisplayName("Should handle ISO country codes in input")
  void shouldHandleISOCountryCodesInInput() {
    Location location = new Location("BR", Optional.of("SP"), Optional.of("São Paulo"));

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("BR");
    assertThat(locationInfoDTO.subdivision()).isEqualTo("SP");
    assertThat(locationInfoDTO.city()).isEqualTo("São Paulo");
    assertThat(locationInfoDTO.pretty()).isEqualTo("São Paulo, SP, Brazil");
  }

  @Test
  @DisplayName("Should generate correct pretty name for full location")
  void shouldGenerateCorrectPrettyNameForFullLocation() {
    Location location = new Location("Germany", Optional.of("Bavaria"), Optional.of("Munich"));

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("DE");
    assertThat(locationInfoDTO.subdivision()).isEqualTo("Bavaria");
    assertThat(locationInfoDTO.city()).isEqualTo("Munich");
    assertThat(locationInfoDTO.pretty()).isEqualTo("Munich, Bavaria, Germany");
  }

  @Test
  @DisplayName("Should generate correct pretty name for country only")
  void shouldGenerateCorrectPrettyNameForCountryOnly() {
    Location location = new Location("Japan", Optional.empty(), Optional.empty());

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("JP");
    assertThat(locationInfoDTO.subdivision()).isNull();
    assertThat(locationInfoDTO.city()).isNull();
    assertThat(locationInfoDTO.pretty()).isEqualTo("Japan");
  }

  @Test
  @DisplayName("Should generate correct pretty name for country and subdivision")
  void shouldGenerateCorrectPrettyNameForCountryAndSubdivision() {
    Location location = new Location("Canada", Optional.of("Ontario"), Optional.empty());

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("CA");
    assertThat(locationInfoDTO.subdivision()).isEqualTo("Ontario");
    assertThat(locationInfoDTO.city()).isNull();
    assertThat(locationInfoDTO.pretty()).isEqualTo("Ontario, Canada");
  }

  @Test
  @DisplayName("Should handle unknown country names")
  void shouldHandleUnknownCountryNames() {
    Location location = new Location("Unknown Country", Optional.empty(), Optional.empty());

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("Unknown Country");
    assertThat(locationInfoDTO.subdivision()).isNull();
    assertThat(locationInfoDTO.city()).isNull();
    assertThat(locationInfoDTO.pretty()).isEqualTo("Unknown Country");
  }

  @Test
  @DisplayName("Should create simplified LocationInfoDTO structure")
  void shouldCreateSimplifiedLocationInfoStructure() {
    Location location = new Location("France", Optional.of("Île-de-France"), Optional.of("Paris"));

    LocationInfoDTO locationInfoDTO = LocationInfoDTO.from(location);

    assertThat(locationInfoDTO.country()).isEqualTo("FR");
    assertThat(locationInfoDTO.subdivision()).isEqualTo("Île-de-France");
    assertThat(locationInfoDTO.city()).isEqualTo("Paris");
    assertThat(locationInfoDTO.pretty()).isEqualTo("Paris, Île-de-France, France");

    assertThat(locationInfoDTO.toString()).contains("country=FR");
    assertThat(locationInfoDTO.toString()).contains("subdivision=Île-de-France");
    assertThat(locationInfoDTO.toString()).contains("city=Paris");
    assertThat(locationInfoDTO.toString()).contains("pretty=Paris, Île-de-France, France");
  }
}
