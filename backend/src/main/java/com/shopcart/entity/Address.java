package com.shopcart.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the address table in database
 * Stores shipping addresses for users
 */
@Entity
@Data
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    /**
     * Primary key for the address
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Foreign key referencing the user who owns this address
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Full address line including street number and name
     */
    @Column(name = "address_line", nullable = false, columnDefinition = "TEXT")
    private String addressLine;

    /**
     * City name
     */
    @Column(name = "city", nullable = false, length = 255)
    private String city;

    /**
     * District/County name
     */
    @Column(name = "district", nullable = false, length = 255)
    private String district;

    /**
     * Ward/Commune name
     */
    @Column(name = "ward", nullable = false, length = 255)
    private String ward;

    /**
     * Flag indicating if this is the default address for the user
     */
    @Column(name = "is_default", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDefault;
}