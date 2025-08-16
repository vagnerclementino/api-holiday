package me.clementino.holiday.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.clementino.holiday.domain.dop.Locality;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * MongoDB embedded document representing locality information for holidays. This entity supports
 * both traditional flat structure and DOP Locality sealed interface serialization. Can be embedded
 * within Holiday documents or used as a standalone collection.
 */
public class LocalityEntity {

  @NotNull
  @Size(max = 2)
  @Indexed
  private String countryCode; // ISO 3166-1 alpha-2 country code

  @NotNull
  @Size(max = 255)
  @Indexed
  private String countryName;

  @Size(max = 10)
  @Indexed
  private String subdivisionCode; // State/Province code (optional)

  @Size(max = 255)
  @Indexed
  private String subdivisionName; // State/Province name (optional)

  @Size(max = 255)
  @Indexed
  private String cityName; // City name (optional)

  @NotNull @Indexed
  private LocalityType localityType; // Type of locality (COUNTRY, SUBDIVISION, CITY)

  private String dopLocalityData; // Serialized DOP Locality sealed interface data

  // Default constructor for MongoDB
  public LocalityEntity() {}

  // Constructor for country-level locality
  public LocalityEntity(String countryCode, String countryName) {
    this.countryCode = countryCode;
    this.countryName = countryName;
    this.localityType = LocalityType.COUNTRY;
  }

  // Constructor for subdivision-level locality
  public LocalityEntity(
      String countryCode, String countryName, String subdivisionCode, String subdivisionName) {
    this.countryCode = countryCode;
    this.countryName = countryName;
    this.subdivisionCode = subdivisionCode;
    this.subdivisionName = subdivisionName;
    this.localityType = LocalityType.SUBDIVISION;
  }

  // Constructor for city-level locality
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

  // Factory method to create from DOP Locality
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

  // Method to convert to DOP Locality
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

  // Getters and setters
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

  /** Enum representing the type of locality for indexing and querying purposes. */
  public enum LocalityType {
    COUNTRY, // National level
    SUBDIVISION, // State/Province level
    CITY // Municipal level
  }
}
