package com.example.PKI.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
@SQLDelete(sql = "UPDATE addresses SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "address")
    private String address;

    @Column(name =  "deleted")
    private boolean deleted = Boolean.FALSE;
    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", address='" + address + '\'' +
                ", deleted=" +deleted +
        '}';
    }

    public Address(Long id, String country, String city, String postalCode, String address, boolean deleted) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
        this.address = address;
        this.deleted = deleted;
    }

    public Address(String country, String city, String postalCode, String address, boolean deleted) {
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
        this.address = address;
        this.deleted = deleted;
    }
}
