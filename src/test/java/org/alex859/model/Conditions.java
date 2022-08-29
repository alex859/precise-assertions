package org.alex859.model;

import org.assertj.core.api.Condition;
import org.assertj.core.condition.Join;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.condition.VerboseCondition.verboseCondition;

class Conditions {
    static Condition<Customer> firstName(String expected) {
        return verboseCondition(
                it -> expected.equals(it.firstName()),
                "first name: '%s'".formatted(expected),
                it -> "but was: '%s'".formatted(it.firstName())
        );
    }

    static Condition<Customer> lastName(String expected) {
        return verboseCondition(
                it -> expected.equals(it.lastName()),
                "last name: '%s'".formatted(expected),
                it -> "but was: '%s'".formatted(it.lastName())
        );
    }

    static Condition<Customer> dateOfBirth(LocalDate expected) {
        return equals("date of birth", expected, Customer::dateOfBirth);
    }

    static <T, K> Condition<T> equals(String description, K expected, Function<T, K> f) {
        return verboseCondition(
                it -> expected.equals(f.apply(it)),
                "%s: '%s'".formatted(description, expected),
                it -> "but was: '%s'".formatted(f.apply(it))
        );
    }

    static Condition<Customer> addressLine1(String expected) {
        return new Condition<>(it -> expected.equals(it.address().line1()), "address line 1 '%s'".formatted(expected));
    }

    static Condition<Customer> addressLine2(String expected) {
        return new Condition<>(it -> expected.equals(it.address().line2()), "address line 2 '%s'".formatted(expected));
    }

    static Condition<Customer> addressLine3(String expected) {
        return new Condition<>(it -> expected.equals(it.address().line3()), "address line 3 '%s'".formatted(expected));
    }

    static Condition<Customer> addressTown(String expected) {
        return new Condition<>(it -> expected.equals(it.address().town()), "address town '%s'".formatted(expected));
    }

    static Condition<Customer> addressPostcode(Postcode expected) {
        return new Condition<>(it -> expected.equals(it.address().postcode()), "address postcode '%s'".formatted(expected));
    }

    static Condition<Address> line1(String expected) {
        return new Condition<>(it -> expected.equals(it.line1()), "line 1 '%s'".formatted(expected));
    }

    static Condition<Address> line2(String expected) {
        return new Condition<>(it -> expected.equals(it.line2()), "line 2 '%s'".formatted(expected));
    }

    static Condition<Address> line3(String expected) {
        return new Condition<>(it -> expected.equals(it.line3()), "line 3 '%s'".formatted(expected));
    }

    static Condition<Address> town(String expected) {
        return new Condition<>(it -> expected.equals(it.town()), "town '%s'".formatted(expected));
    }

    static Condition<Address> postcode(Postcode expected) {
        return new Condition<>(it -> expected.equals(it.postcode()), "postcode '%s'".formatted(expected));
    }

    @SafeVarargs
    static Condition<Customer> address(Condition<Address>... conditions) {
        List<Condition<Customer>> conditionsOnCustomer =
                Arrays.stream(conditions)
                        .map(Conditions::toConditionOnCustomer)
                        .toList();
        return new CustomizableDescriptionAllOf("address", conditionsOnCustomer);
    }

    static Condition<Customer> toConditionOnCustomer(Condition<Address> condition) {
        return new Condition<>(it ->
                condition.matches(it.address()), condition.description().value()
        );
    }

    @SafeVarargs
    static Condition<Customer> customer(Condition<Customer>... conditions) {
        return allOf(conditions);
    }

    static class CustomizableDescriptionAllOf extends Join<Customer> {

        private final String description;

        @SafeVarargs
        private CustomizableDescriptionAllOf(String string, Condition<Customer>... conditions) {
            this(string, Arrays.stream(conditions).toList());
        }

        private CustomizableDescriptionAllOf(String string, Iterable<Condition<Customer>> conditions) {
            super(conditions);
            this.description = string;
        }

        @Override
        public boolean matches(Customer value) {
            return conditions().stream().allMatch(condition -> condition.matches(value));
        }

        @Override
        public String descriptionPrefix() {
            return description;
        }
    }

    @SafeVarargs
    static Condition<Customer> aCustomerWith(Condition<Customer>... conditions) {
        return customerWith(conditions);
    }

    @SafeVarargs
    static Condition<Customer> customerWith(Condition<Customer>... conditions) {
        return new CustomizableDescriptionAllOf("a customer with", conditions);
    }
}
