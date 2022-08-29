package org.alex859.model;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.alex859.model.Conditions.address;
import static org.alex859.model.Conditions.addressLine1;
import static org.alex859.model.Conditions.addressLine2;
import static org.alex859.model.Conditions.addressLine3;
import static org.alex859.model.Conditions.addressPostcode;
import static org.alex859.model.Conditions.addressTown;
import static org.alex859.model.Conditions.customer;
import static org.alex859.model.Conditions.dateOfBirth;
import static org.alex859.model.Conditions.firstName;
import static org.alex859.model.Conditions.lastName;
import static org.alex859.model.Conditions.line1;
import static org.alex859.model.Conditions.line2;
import static org.alex859.model.Conditions.line3;
import static org.alex859.model.Conditions.postcode;
import static org.alex859.model.Conditions.town;
import static org.alex859.model.CustomerAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class ModelTest {
    @Test
    void isEqualTo() {
        var customer = retrieveCustomer();

        Assertions.assertThat(customer).isEqualTo(
                new Customer(
                        "John",
                        "Doe",
                        LocalDate.of(1980, 12, 11),
                        new Address(
                                "12 Chestnut close",
                                "",
                                "South Woodford",
                                "London",
                                new Postcode("E18 5HT")
                        )
                )
        );
    }

    @Test
    void separateAssertions() {
        var customer = retrieveCustomer();

        assertThat(customer.firstName()).isEqualTo("John");
        assertThat(customer.lastName()).isEqualTo("Doe");
        assertThat(customer.dateOfBirth()).isEqualTo(LocalDate.of(1980, 12, 11));
        assertThat(customer.address().line1()).isEqualTo("12 Chestnut close");
        assertThat(customer.address().line2()).isEqualTo("");
        assertThat(customer.address().line3()).isEqualTo("South Woodford");
        assertThat(customer.address().town()).isEqualTo("London");
        assertThat(customer.address().postcode()).isEqualTo(new Postcode("E18 5HT"));
    }

    @Test
    void separateAssertionsWithDescribe() {
        var customer = retrieveCustomer();

        assertThat(customer.firstName())
                .describedAs("first name")
                .isEqualTo("John");
        assertThat(customer.lastName())
                .describedAs("last name")
                .isEqualTo("Doe");
        assertThat(customer.dateOfBirth())
                .describedAs("date of birth")
                .isEqualTo(LocalDate.of(1980, 12, 11));
        assertThat(customer.address().line1())
                .describedAs("address line 1")
                .isEqualTo("12 Chestnut close");
        assertThat(customer.address().line2())
                .describedAs("address line 2")
                .isEqualTo("");
        assertThat(customer.address().line3())
                .describedAs("address line 3")
                .isEqualTo("South Woodford");
        assertThat(customer.address().town())
                .describedAs("town")
                .isEqualTo("London");
        assertThat(customer.address().postcode())
                .describedAs("postcode")
                .isEqualTo(new Postcode("E18 5HT"));
    }

    @Test
    void separateAssertionsWithCustomAssert() {
        var customer = retrieveCustomer();

        assertThat(customer)
                .hasFirstName("John")
                .hasLastName("Doe")
                .hasDateOfBirth(LocalDate.of(1980, 12, 11))
                .hasAddressLine1("12 Chestnut close")
                .hasAddressLine2("")
                .hasAddressLine3("South Woodford")
                .hasAddressTown("London")
                .hasAddressPostcode(new Postcode("E18 5HT"));
    }

    @Test
    void separateAssertionsWithCustomAssertNested() {
        var customer = retrieveCustomer();

        assertThat(customer)
                .hasFirstName("John")
                .hasLastName("Doe")
                .hasDateOfBirth(LocalDate.of(1980, 12, 11))
                .address(it ->
                        it.line1("12 Chestnut close")
                                .line2("")
                                .line3("South Woodford")
                                .town("London")
                                .postcode(new Postcode("E18 5HT"))
                );
    }

    @Test
    void separateAssertionsWithDescribeAndSoftAssertions() {
        var customer = retrieveCustomer();
        var softly = new SoftAssertions();
        softly.assertThat(customer.firstName())
                .describedAs("first name")
                .isEqualTo("John");
        softly.assertThat(customer.lastName())
                .describedAs("last name")
                .isEqualTo("Doe");
        softly.assertThat(customer.dateOfBirth())
                .describedAs("date of birth")
                .isEqualTo(LocalDate.of(1980, 12, 11));
        softly.assertThat(customer.address().line1())
                .describedAs("address line 1")
                .isEqualTo("12 Chestnut close");
        softly.assertThat(customer.address().line2())
                .describedAs("address line 2")
                .isEqualTo("");
        softly.assertThat(customer.address().line3())
                .describedAs("address line 3")
                .isEqualTo("South Woodford");
        softly.assertThat(customer.address().town())
                .describedAs("town")
                .isEqualTo("London");
        softly.assertThat(customer.address().postcode())
                .describedAs("postcode")
                .isEqualTo(new Postcode("E18 5HT"));
        softly.assertAll();
    }

    @Test
    void separateAssertionsWithCustomSoftAssert() {
        var softly = new MySoftAssertions();
        var customer = retrieveCustomer();

        softly.assertThat(customer)
                .hasFirstName("John")
                .hasLastName("Doe")
                .hasDateOfBirth(LocalDate.of(1980, 12, 11))
                .hasAddressLine1("12 Chestnut close")
                .hasAddressLine2("")
                .hasAddressLine3("South Woodford")
                .hasAddressTown("London")
                .hasAddressPostcode(new Postcode("E18 5HT"));

        softly.assertAll();
    }

    @Test
    void verboseAssertion() {
        var customer = retrieveCustomer();
        assertThat(customer).has(firstName("Mike"));
    }


    @Test
    void separateAssertionsWithConditions() {
        var customer = retrieveCustomer();

        assertThat(customer).has(
                customer(
                        firstName("John"),
                        lastName("Doe"),
                        dateOfBirth(LocalDate.of(1980, 12, 11)),
                        addressLine1("12 Chestnut close"),
                        addressLine2(""),
                        addressLine3("South Woodford"),
                        addressTown("London"),
                        addressPostcode(new Postcode("E18 5HT"))
                )
        );
    }

    @Test
    void separateAssertionsWithConditionsNestedAddress() {
        var customer = retrieveCustomer();

        assertThat(customer).is(
                customer(
                        firstName("John"),
                        lastName("Tilbury"),
                        dateOfBirth(LocalDate.of(1980, 12, 11)),
                        address(
                                line1("12 Chestnut close"),
                                line2(""),
                                line3("South Woodford"),
                                town("London"),
                                postcode(new Postcode("E18 5HT"))
                        )

                )
        );
    }

    @Test
    void assertOnCollection() {
        var customers = retrieveCustomers();

        assertThat(customers).containsExactlyInAnyOrder(
                new Customer(
                        "John",
                        "Doe",
                        LocalDate.of(1980, 12, 11),
                        new Address(
                                "12 Chestnut close",
                                "",
                                "South Woodford",
                                "Manchester",
                                new Postcode("M15 5HT")
                        )
                ),
                new Customer(
                        "Mike",
                        "Bellview",
                        LocalDate.of(1985, 4, 10),
                        new Address(
                                "5 Holy street",
                                "",
                                "",
                                "Glasgow",
                                new Postcode("G52 4AB")
                        )
                )
        );
    }

    @Test
    void assertOnCollectionWithConditions() {
        var customers = retrieveCustomers();

        assertSoftly(softly ->
                softly.assertThat(customers)
                        .haveExactly(1,
                                customer(
                                        firstName("John"),
                                        lastName("Doe"),
                                        dateOfBirth(LocalDate.of(1980, 12, 11)),
                                        address(
                                                line1("12 Chestnut close"),
                                                line2(""),
                                                line3("South Woodford"),
                                                town("London"),
                                                postcode(new Postcode("E18 5HT"))
                                        )
                                )
                        )
                        .haveExactly(1,
                                customer(
                                        firstName("Mike"),
                                        lastName("Bellview"),
                                        dateOfBirth(LocalDate.of(1985, 4, 10)),
                                        address(
                                                line1("5 Holy Street"),
                                                line2(""),
                                                line3(""),
                                                town("Glasgow"),
                                                postcode(new Postcode("G52 4AB"))
                                        )
                                )
                        )
        );
    }

    private Collection<Customer> retrieveCustomers() {
        return List.of(
                new Customer(
                        "John",
                        "Doe",
                        LocalDate.of(1980, 12, 11),
                        new Address(
                                "12 Chestnut close",
                                "",
                                "South Woodford",
                                "Manchester",
                                new Postcode("M15 5HT")
                        )
                ),
                new Customer(
                        "Mike",
                        "Bellview",
                        LocalDate.of(1985, 4, 10),
                        new Address(
                                "5 Holy street",
                                "",
                                "",
                                "London",
                                new Postcode("E18 4AB")
                        )
                )
        );
    }

    private Customer retrieveCustomer() {
        return new Customer(
                "John",
                "Doe",
                LocalDate.of(1980, 12, 11),
                new Address(
                        "12 Chestnut close",
                        "",
                        "South Woodford",
                        "Manchester",
                        new Postcode("M15 5HT")
                )
        );
    }
}

