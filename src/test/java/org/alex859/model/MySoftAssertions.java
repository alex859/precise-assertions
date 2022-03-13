package org.alex859.model;

import org.assertj.core.api.SoftAssertions;

class MySoftAssertions extends SoftAssertions {
    CustomerAssert assertThat(Customer actual) {
        return proxy(CustomerAssert.class, Customer.class, actual);
    }

    AddressAssert assertThat(Address actual) {
        return proxy(AddressAssert.class, Address.class, actual);
    }
}
