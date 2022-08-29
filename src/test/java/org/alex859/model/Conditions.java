package org.alex859.model;

import org.assertj.core.api.Condition;
import org.assertj.core.condition.Join;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.alex859.model.NestableCondition.nestable;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.condition.VerboseCondition.verboseCondition;

class Conditions {
    static Condition<Customer> firstName(String expected) {
        return equals("first name", expected, Customer::firstName);
    }

    static Condition<Customer> lastName(String expected) {
        return equals("last name", expected, Customer::lastName);
    }

    static Condition<Customer> dateOfBirth(LocalDate expected) {
        return equals("date of birth", expected, Customer::dateOfBirth);
    }

    static Condition<Customer> addressLine1(String expected) {
        return equals("address line 1", expected, it -> it.address().line1());
    }

    static Condition<Customer> addressLine2(String expected) {
        return equals("address line 2", expected, it -> it.address().line2());
    }

    static Condition<Customer> addressLine3(String expected) {
        return equals("address line 3", expected, it -> it.address().line3());
    }

    static Condition<Customer> addressTown(String expected) {
        return equals("address town", expected, it -> it.address().town());
    }

    static Condition<Customer> addressPostcode(Postcode expected) {
        return equals("address postcode", expected, it -> it.address().postcode());
    }

    static Condition<Address> line1(String expected) {
        return equals("line 1", expected, Address::line1);
    }

    static Condition<Address> line2(String expected) {
        return equals("line 2", expected, Address::line2);
    }

    static Condition<Address> line3(String expected) {
        return equals("line 3", expected, Address::line3);
    }

    static Condition<Address> town(String expected) {
        return equals("town", expected, Address::town);
    }

    static Condition<Address> postcode(Postcode expected) {
        return equals("postcode", expected, Address::postcode);
    }

    @SafeVarargs
    static Condition<Customer> address(Condition<Address>... conditions) {
        return nestable("address", Customer::address, conditions);
    }

    static <T, K> Condition<T> equals(String description, K expected, Function<T, K> f) {
        return verboseCondition(
                it -> expected.equals(f.apply(it)),
                "%s: '%s'".formatted(description, expected),
                it -> " but was: '%s'".formatted(f.apply(it))
        );
    }

    @SafeVarargs
    static Condition<Customer> customer(Condition<Customer>... conditions) {
        return nestable("customer", conditions);
    }
}
