package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.springframework.validation.FieldError;

@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "location")
    private Garden garden;
    private String streetAddress;
    private String suburb;
    private String city;
    private String postcode;
    private String country;

    public Location(String streetAddress, String suburb, String city, String postcode, String country) {
        this.streetAddress = streetAddress;
        this.suburb = suburb;
        this.city = city;
        this.postcode = postcode;
        this.country = country;
    }

    public Location() {} //Required


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Garden getGarden() {
        return garden;
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    //AI generated from Copilot
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (streetAddress != null && !streetAddress.isEmpty()) {
            sb.append(streetAddress);
        }

        if (suburb != null && !suburb.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(suburb);
        }

        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(city);
        }

        if (country != null && !country.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(country);
        }

        return sb.toString();
    }
}
