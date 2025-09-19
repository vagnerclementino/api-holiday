package me.clementino.holiday.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.index.Indexed;
import me.clementino.holiday.domain.dop.Locality;

/**
 * MongoDB embedded document representing locality information for holidays.
 * This entity supports
 * both traditional flat structure and DOP Locality sealed interface
 * serialization.
 *
 * <p>
 * This entity can be embedded within Holiday documents or used as a standalone
 * collection for
 * locality management. It provides efficient querying capabilities through
 * proper indexing while
 * maintaining compatibility with DOP Locality sealed interface.
 *
 * <p>
 * <strong>Key Features:</strong>
 *
 * <ul>
 * <li>Hierarchical locality representation (Country → Subdivision → City)
 * <li>DOP Locality sealed interface serialization support
 * <li>Efficient MongoDB indexing for locality-based queries
 * <li>Bidirectional conversion between entity and DOP objects
 * <li>Backward compatibility with flat locality structure
 * </ul>
 *
 * <p>
 * <strong>Locality Hierarchy:</strong>
 *
 * <ul>
 * <li><strong>Country</strong>: National level (e.g., "US", "Brazil")
 * <li><strong>Subdivision</strong>: State/Province level (e.g., "CA", "SP")
 * <li><strong>City</strong>: Municipal level (e.g., "San Francisco", "São
 * Paulo")
 * </ul>
 */
public class LocalityEntity {

  @NotNull
  @Size(max = 2)
  @Indexed
  private String countryCode;

  @NotNull
  @Size(max = 255)
  @Indexed
  private String countryName;

  @Size(max = 10)
  @Indexed
  private String subdivisionCode;

  @Size(max = 255)
  @Indexed
  private String subdivisionName;

  @Size(max = 255)
  @Indexed
  private String cityName;

  @NotNull
  @Indexed
  private LocalityType localityType;

  @Size(max = 2000)
  private String dopLocalityData;

  @Size(max = 10)
  private String timeZone;

  @Size(max = 3)
  private String currencyCode;

  @Size(max = 10)
  private String languageCode;

  public LocalityEntity() {
  }

  public LocalityEntity(String countryCode, String countryName) {
    this.countryCode = countryCode;
    this.countryName = countryName;
    this.localityType = LocalityType.COUNTRY;
  }

  public LocalityEntity(
      String countryCode, String countryName, String subdivisionCode, String subdivisionName) {
    this.countryCode = countryCode;
    this.countryName = countryName;
    this.subdivisionCode = subdivisionCode;
    this.subdivisionName = subdivisionName;
    this.localityType = LocalityType.SUBDIVISION;
  }

  public LocalityEntity(
      String countryCode,
      String countryName,
      String subdivisionCode,
      String subdivisionName,
      String cityName) {
    this.countryCode = countryCode;
    this.countryName = countryName;
    this.subdivisionCode = subdivisionCode;
    this.subdivisionName = subdivisionName;
    this.cityName = cityName;
    this.localityType = LocalityType.CITY;
  }

  /**
   * Factory method to create LocalityEntity from DOP Locality sealed interface.
   *
   * @param locality the DOP Locality object
   * @return corresponding LocalityEntity
   */
  public static LocalityEntity fromDopLocality(Locality locality) {
    LocalityEntity entity = new LocalityEntity();

    switch (locality) {
      case Locality.Country country -> {
        entity.countryCode = country.code();
        entity.countryName = country.name();
        entity.localityType = LocalityType.COUNTRY;
      }
      case Locality.Subdivision subdivision -> {
        entity.countryCode = subdivision.country().code();
        entity.countryName = subdivision.country().name();
        entity.subdivisionCode = subdivision.code();
        entity.subdivisionName = subdivision.name();
        entity.localityType = LocalityType.SUBDIVISION;
      }
      case Locality.City city -> {
        entity.countryCode = city.country().code();
        entity.countryName = city.country().name();
        entity.subdivisionCode = city.subdivision().code();
        entity.subdivisionName = city.subdivision().name();
        entity.cityName = city.name();
        entity.localityType = LocalityType.CITY;
      }
    }

    return entity;
  }

  /**
   * Converts this LocalityEntity to DOP Locality sealed interface.
   *
   * @return corresponding DOP Locality object
   */
  public Locality toDopLocality() {
    return switch (localityType) {
      case COUNTRY -> new Locality.Country(countryCode, countryName);
      case SUBDIVISION ->
        new Locality.Subdivision(
            new Locality.Country(countryCode, countryName), subdivisionCode, subdivisionName);
      case CITY ->
        new Locality.City(
            cityName,
            new Locality.Subdivision(
                new Locality.Country(countryCode, countryName), subdivisionCode, subdivisionName),
            new Locality.Country(countryCode, countryName));
    };
  }

  /**
   * Checks if this locality is at the country level.
   *
   * @return true if this is a country-level locality
   */
  public boolean isCountryLevel() {
    return localityType == LocalityType.COUNTRY;
  }

  /**
   * Checks if this locality is at the subdivision level.
   *
   * @return true if this is a subdivision-level locality
   */
  public boolean isSubdivisionLevel() {
    return localityType == LocalityType.SUBDIVISION;
  }

  /**
   * Checks if this locality is at the city level.
   *
   * @return true if this is a city-level locality
   */
  public boolean isCityLevel() {
    return localityType == LocalityType.CITY;
  }

  /**
   * Gets the full hierarchical name of this locality.
   *
   * @return formatted locality name (e.g., "São Paulo, SP, Brazil")
   */
  public String getFullName() {
    return switch (localityType) {
      case COUNTRY -> countryName;
      case SUBDIVISION -> subdivisionName + ", " + countryName;
      case CITY -> cityName + ", " + subdivisionName + ", " + countryName;
    };
  }

  /**
   * Checks if this locality contains or matches another locality. A country
   * contains subdivisions
   * and cities within it. A subdivision contains cities within it. A city only
   * matches itself.
   *
   * @param other the other locality to check
   * @return true if this locality contains the other locality
   */
  public boolean contains(LocalityEntity other) {
    if (other == null) {
      return false;
    }

    if (!countryCode.equals(other.countryCode)) {
      return false;
    }

    return switch (this.localityType) {
      case COUNTRY -> true;
      case SUBDIVISION -> {
        if (other.localityType == LocalityType.COUNTRY) {
          yield false;
        }
        yield subdivisionCode != null && subdivisionCode.equals(other.subdivisionCode);
      }
      case CITY -> {
        yield this.equals(other);
      }
    };
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public String getSubdivisionCode() {
    return subdivisionCode;
  }

  public void setSubdivisionCode(String subdivisionCode) {
    this.subdivisionCode = subdivisionCode;
  }

  public String getSubdivisionName() {
    return subdivisionName;
  }

  public void setSubdivisionName(String subdivisionName) {
    this.subdivisionName = subdivisionName;
  }

  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public LocalityType getLocalityType() {
    return localityType;
  }

  public void setLocalityType(LocalityType localityType) {
    this.localityType = localityType;
  }

  public String getDopLocalityData() {
    return dopLocalityData;
  }

  public void setDopLocalityData(String dopLocalityData) {
    this.dopLocalityData = dopLocalityData;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    LocalityEntity that = (LocalityEntity) o;

    if (!countryCode.equals(that.countryCode))
      return false;
    if (subdivisionCode != null
        ? !subdivisionCode.equals(that.subdivisionCode)
        : that.subdivisionCode != null)
      return false;
    if (cityName != null ? !cityName.equals(that.cityName) : that.cityName != null)
      return false;
    return localityType == that.localityType;
  }

  @Override
  public int hashCode() {
    int result = countryCode.hashCode();
    result = 31 * result + (subdivisionCode != null ? subdivisionCode.hashCode() : 0);
    result = 31 * result + (cityName != null ? cityName.hashCode() : 0);
    result = 31 * result + localityType.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "LocalityEntity{"
        + "countryCode='"
        + countryCode
        + '\''
        + ", countryName='"
        + countryName
        + '\''
        + ", subdivisionCode='"
        + subdivisionCode
        + '\''
        + ", subdivisionName='"
        + subdivisionName
        + '\''
        + ", cityName='"
        + cityName
        + '\''
        + ", localityType="
        + localityType
        + '}';
  }

  /**
   * Enum representing the type of locality for indexing and querying purposes.
   */
  public enum LocalityType {
    COUNTRY,
    SUBDIVISION,
    CITY
  }
}
