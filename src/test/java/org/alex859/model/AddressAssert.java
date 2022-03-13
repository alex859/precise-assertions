package org.alex859.model;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

class AddressAssert extends AbstractAssert<AddressAssert, Address> {
    public AddressAssert(Address customer) {
        super(customer, AddressAssert.class);
    }

    AddressAssert line1(String expected) {
        Assertions.assertThat(actual.line1())
                .describedAs("address line 1")
                .isEqualTo(expected);

        return this;
    }

    AddressAssert line2(String expected) {
        Assertions.assertThat(actual.line2())
                .describedAs("address line 2")
                .isEqualTo(expected);

        return this;
    }

    AddressAssert line3(String expected) {
        Assertions.assertThat(actual.line3())
                .describedAs("address line 3")
                .isEqualTo(expected);

        return this;
    }

    AddressAssert town(String expected) {
        Assertions.assertThat(actual.town())
                .describedAs("address town")
                .isEqualTo(expected);

        return this;
    }

    AddressAssert postcode(Postcode expected) {
        Assertions.assertThat(actual.postcode())
                .describedAs("postcode")
                .isEqualTo(expected);

        return this;
    }

    static AddressAssert assertThat(Address customer) {
        return new AddressAssert(customer);
    }
}
