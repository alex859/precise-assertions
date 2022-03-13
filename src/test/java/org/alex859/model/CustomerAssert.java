package org.alex859.model;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.util.function.Consumer;

class CustomerAssert extends AbstractAssert<CustomerAssert, Customer> {
    public CustomerAssert(Customer customer) {
        super(customer, CustomerAssert.class);
    }

    CustomerAssert hasFirstName(String expected) {
        Assertions.assertThat(actual.firstName())
                .describedAs("first name")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert hasLastName(String expected) {
        Assertions.assertThat(actual.lastName())
                .describedAs("last name")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert hasDateOfBirth(LocalDate expected) {
        Assertions.assertThat(actual.dateOfBirth())
                .describedAs("date of birth")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert hasAddressLine1(String expected) {
        Assertions.assertThat(actual.address().line1())
                .describedAs("address line 1")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert hasAddressLine2(String expected) {
        Assertions.assertThat(actual.address().line2())
                .describedAs("address line 2")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert hasAddressLine3(String expected) {
        Assertions.assertThat(actual.address().line3())
                .describedAs("address line 3")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert hasAddressTown(String expected) {
        Assertions.assertThat(actual.address().town())
                .describedAs("address town")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert hasAddressPostcode(Postcode expected) {
        Assertions.assertThat(actual.address().postcode())
                .describedAs("postcode")
                .isEqualTo(expected);

        return this;
    }

    CustomerAssert address(Consumer<AddressAssert> addressAssertions) {
        addressAssertions.accept(AddressAssert.assertThat(actual.address()));
        return this;
    }

    static CustomerAssert assertThat(Customer customer) {
        return new CustomerAssert(customer);
    }
}

