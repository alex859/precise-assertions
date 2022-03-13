package org.alex859.model;

import java.time.LocalDate;

record Customer(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Address address
) {}

record Address(
        String line1,
        String line2,
        String line3,
        String town,
        Postcode postcode
) {}

record Postcode(String value) {}