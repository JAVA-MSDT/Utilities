/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt.masking.service;

import com.javamsdt.masking.domain.Address;
import com.javamsdt.masking.domain.Gender;
import com.javamsdt.masking.domain.GeoLocation;
import com.javamsdt.masking.domain.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    public User findUserById(final Long id) {
        return findUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> findUsers() {
        return List.of(
                new User(1L, "Ahmed Samy", "one@mail.com", "123456", "01000000000", findAddresses().get(0), LocalDate.of(1985, 1, 25), Gender.MALE, new BigDecimal("20.0"), Instant.now()),
                new User(2L, "Maria Samy", "two@mail.com", "123456", "01000000000", findAddresses().get(1), LocalDate.of(2014, 2, 8), Gender.FEMALE, new BigDecimal("40.20"), Instant.now()),
                new User(3L, "Male One", "three@mail.com", "123456", "01000000000", findAddresses().get(2), LocalDate.of(2000, 3, 15), Gender.MALE, new BigDecimal("55.98"), Instant.now()),
                new User(4L, "Female One", "four@mail.com", "123456", "01000000000", findAddresses().get(3), LocalDate.of(1995, 4, 20), Gender.FEMALE, new BigDecimal("100.46"), Instant.now())
        );
    }


    private List<Address> findAddresses() {
        return List.of(
                new Address(1L, "first Street", "Building one", "City One", "State One", "Zip One", "Country One", findGeoLocations().get(0)),
                new Address(2L, "second Street", "Building Two", "City Two", "State Two", "Zip Two", "Country Two", findGeoLocations().get(1)),
                new Address(3L, "Third Street", "Building Three", "City Three", "State Three", "Zip Three", "Country Three", findGeoLocations().get(2)),
                new Address(4L, "Forth Street", "Building one", "City Four", "State Four", "Zip Four", "Country Four", findGeoLocations().get(3)));
    }

    private List<GeoLocation> findGeoLocations() {
        return List.of(
                new GeoLocation(UUID.fromString("ad3ee468-4216-418f-8e12-518272ead5d6"), 27.8109, 33.9211),
                new GeoLocation(UUID.fromString("ad3ee468-4216-418f-8e12-518272ead111"), 25.3200, 34.8450),
                new GeoLocation(UUID.fromString("ad3ee468-4216-418f-8e12-518272ead222"), 26.3425, 34.8444),
                new GeoLocation(UUID.fromString("ad3ee468-4216-418f-8e12-518272ead333"), 24.5747, 35.1526));
    }
}
