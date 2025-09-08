package me.clementino.holiday.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import me.clementino.holiday.domain.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("LocationInfo Tests")
@Tag("unit")
class LocationInfoTest {

  @Test
  @DisplayName("Should create LocationInfo from Location with all fields")
  void shouldCreateLocationInfoFromLocationWithAllFields() {
    Location location = new Location("Brazil", Optional.of("São Paulo"), Optional.of("São Paulo"));

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("BR");
    assertThat(locationInfo.subdivision()).isEqualTo("São Paulo");
    assertThat(locationInfo.city()).isEqualTo("São Paulo");
    assertThat(locationInfo.pretty()).isEqualTo("São Paulo, São Paulo, Brazil");
  }

  @Test
  @DisplayName("Should create LocationInfo from Location with only country")
  void shouldCreateLocationInfoFromLocationWithOnlyCountry() {
    Location location = new Location("Brazil", Optional.empty(), Optional.empty());

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("BR");
    assertThat(locationInfo.subdivision()).isNull();
    assertThat(locationInfo.city()).isNull();
    assertThat(locationInfo.pretty()).isEqualTo("Brazil");
  }

  @Test
  @DisplayName("Should create LocationInfo from Location with country and subdivision")
  void shouldCreateLocationInfoFromLocationWithCountryAndSubdivision() {
    Location location = new Location("United States", Optional.of("California"), Optional.empty());

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("US");
    assertThat(locationInfo.subdivision()).isEqualTo("California");
    assertThat(locationInfo.city()).isNull();
    assertThat(locationInfo.pretty()).isEqualTo("California, United States");
  }

  @Test
  @DisplayName("Should handle ISO country codes in input")
  void shouldHandleISOCountryCodesInInput() {
    Location location = new Location("BR", Optional.of("SP"), Optional.of("São Paulo"));

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("BR");
    assertThat(locationInfo.subdivision()).isEqualTo("SP");
    assertThat(locationInfo.city()).isEqualTo("São Paulo");
    assertThat(locationInfo.pretty()).isEqualTo("São Paulo, SP, Brazil");
  }

  @Test
  @DisplayName("Should generate correct pretty name for full location")
  void shouldGenerateCorrectPrettyNameForFullLocation() {
    Location location = new Location("Germany", Optional.of("Bavaria"), Optional.of("Munich"));

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("DE");
    assertThat(locationInfo.subdivision()).isEqualTo("Bavaria");
    assertThat(locationInfo.city()).isEqualTo("Munich");
    assertThat(locationInfo.pretty()).isEqualTo("Munich, Bavaria, Germany");
  }

  @Test
  @DisplayName("Should generate correct pretty name for country only")
  void shouldGenerateCorrectPrettyNameForCountryOnly() {
    Location location = new Location("Japan", Optional.empty(), Optional.empty());

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("JP");
    assertThat(locationInfo.subdivision()).isNull();
    assertThat(locationInfo.city()).isNull();
    assertThat(locationInfo.pretty()).isEqualTo("Japan");
  }

  @Test
  @DisplayName("Should generate correct pretty name for country and subdivision")
  void shouldGenerateCorrectPrettyNameForCountryAndSubdivision() {
    Location location = new Location("Canada", Optional.of("Ontario"), Optional.empty());

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("CA");
    assertThat(locationInfo.subdivision()).isEqualTo("Ontario");
    assertThat(locationInfo.city()).isNull();
    assertThat(locationInfo.pretty()).isEqualTo("Ontario, Canada");
  }

  @Test
  @DisplayName("Should handle unknown country names")
  void shouldHandleUnknownCountryNames() {
    Location location = new Location("Unknown Country", Optional.empty(), Optional.empty());

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("Unknown Country");
    assertThat(locationInfo.subdivision()).isNull();
    assertThat(locationInfo.city()).isNull();
    assertThat(locationInfo.pretty()).isEqualTo("Unknown Country");
  }

  @Test
  @DisplayName("Should create simplified LocationInfo structure")
  void shouldCreateSimplifiedLocationInfoStructure() {
    Location location = new Location("France", Optional.of("Île-de-France"), Optional.of("Paris"));

    LocationInfo locationInfo = LocationInfo.from(location);

    assertThat(locationInfo.country()).isEqualTo("FR");
    assertThat(locationInfo.subdivision()).isEqualTo("Île-de-France");
    assertThat(locationInfo.city()).isEqualTo("Paris");
    assertThat(locationInfo.pretty()).isEqualTo("Paris, Île-de-France, France");

    assertThat(locationInfo.toString()).contains("country=FR");
    assertThat(locationInfo.toString()).contains("subdivision=Île-de-France");
    assertThat(locationInfo.toString()).contains("city=Paris");
    assertThat(locationInfo.toString()).contains("pretty=Paris, Île-de-France, France");
  }
}
