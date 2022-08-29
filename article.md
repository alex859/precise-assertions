# Precise and readable assertions with AssertJ

We all love writing tests! 

In this blog post we are going to explore the tools that `AssertJ` gives us to improve assertions precision and readability.

Let's be more specific and define what we are looking for in a test assertion section:

1. The assertion code should be easy to read
2. The assertion message should be clean, and it should quickly highlight the problem
3. Assertions should be precise, we should only assert on what is relevant for the scenario under test

## Example domain

Let's start defining a simple domain model for `Customers` that we'll be using in the examples:

```java
record Customer(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Address address
) {}

record Address(
        String line1,
        String line2,
        String town,
        String line4,
        Postcode postcode
) {}

record Postcode(String value) {}
```

## First test based on equality

Let's say that we have some logic to retrieve a customer, and we want to check that we got the expected result:

```java
@Test
void testWithIsEqualTo() {
    var customer = retrieveCustomer();

    assertThat(customer).isEqualTo(
            new Customer(
                    "John",
                    "Doe",
                    LocalDate.of(1980, 12, 11),
                    new Address(
                            "12 Chestnut close",
                            "",
                            "London",
                            "",
                            new Postcode("E18 5HT")
                    )
            )
    );
}
```

Classes in our data model usually have a nice `equals` method, manually defined or generated in case of a `record`/`data class`/`lombok`, and
we generally tend to use it in our test assertions (in some cases we actually only use the `equals` method in tests).

Let's evaluate this assertion based on our expectation of a good assertion:

1. The assertion code should be easy to read
   1. It is not easy to read. Is "John" the first name or the last name? We need to check the `Customer`'s constructor to answer this question. Same thing for the address lines. There are ways to improve this, but they are not in the scope of this discussion.
2. The assertion message should be clean, and it should quickly highlight the problem
   1. Let's have a look at the assertion message that we get when a test fails:
```
expected: Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, town=London, postcode=Postcode[value=E18 5HT]]]
but was: Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, town=Manchester, postcode=Postcode[value=M15 5HT]]]
```
those classes have a `toString` method defined, which is good, but it takes a few seconds to visually compare the actual and expected and spot the
difference (oh right, town and postcode are different!). IDE could help, but how about a test failure on our CI server?

And real domain models are usually more complex than this!

3. Assertions should be precise, we should only assert on what is relevant for the scenario under test
   1. OK, maybe for this example we are interested in checking the whole object structure, but many times we are not. And even if we are today in this specific scenario,
   it is likely that we will add a new field to the `Customer` that is not relevant to this test.

In the [GOOS book](http://www.growing-object-oriented-software.com/) Steve Freeman and Nat Pryce clearly state:

> Testing for equality doesn't scale well as the value being returned becomes more complex. Different test scenarios may make the tested code
> return results that differ only in specific attributes, so comparing the entire result each time is misleading and
> introduces an implicit dependency on the behaviour of the whole tested object.

## Checking individual properties

A first step we can take is to assert on the equality of the single properties.

```java
@Test
void testSeparateAssertions() {
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
```

Let's assess again against our 3 requirements:

1. The assertion code should be easy to read
   1. We now know that "John" is the expected first name and "Doe" is the expected last name, but the noise is pretty high, and I wouldn't say that the assertion is easy to read
2. The assertion message should be clean, and it should quickly highlight the problem
   1. Let's have a look a a failure message:
```
expected: "London"
 but was: "Manchester"
org.opentest4j.AssertionFailedError: 
expected: "London"
 but was: "Manchester"
```
Easier to read, but there are 2 issues here:
1. We can probably infer that we have one issue with the `town` field, but it would be better to have it explicitly written in the assertion error 
2. We only see one error, the postcode is probably still not matching

3. Assertions should be precise, we should only assert on what is relevant for the scenario under test
   1. There is an improvement here, we are free to assert only on the properties related to this test scenario.

Let's see what we can do to improve on the issues found on requirement `2.`.

### Describe object under test

`AssertJ` lets us describe the fields we are asserting on, so we can use:

```java
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
```

and the error message becomes:
```
[town] 
expected: "London"
 but was: "Manchester"
```

Which is better, but the test has grown in size.

### Use SoftAssertions

We can use `SoftAssertions` to also get the error message about the wrong postcode:

```java
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
```
(or we can use the `assertSoftly` method)

which gives us this error:
```
Multiple Failures (2 failures)
-- failure 1 --
[town] 
expected: "London"
 but was: "Manchester"
-- failure 2 --
[postcode] 
expected: Postcode[value=E18 5HT]
 but was: Postcode[value=M15 5HT]
```

Definitely an improvement on the assertion message.

Let's now see what we can do to improve the readability of the test.

## Custom AssertJ assertions

As we've seen the test has grown in size. Also, the descriptions we've added are reusable in other tests. 

We can refactor them to a custom assertion:

```java
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

    static CustomerAssert assertThat(Customer customer) {
        return new CustomerAssert(customer);
    }
}
```

and our test becomes:

```java
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
```

We can use the custom assertion as a soft assertion by adding:

```java
class MySoftAssertions extends SoftAssertions {
    CustomerAssert assertThat(Customer actual) {
        return proxy(CustomerAssert.class, Customer.class, actual);
    }
}
```

to get for our test:

```java
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
```

the assertion is still the same as before:

```
Multiple Failures (2 failures)
-- failure 1 --
[address town] 
expected: "London"
 but was: "Manchester"
at CustomerAssert.hasAddressTown(CustomerAssert.java:64)
-- failure 2 --
[postcode] 
expected: Postcode[value=E18 5HT]
 but was: Postcode[value=M15 5HT]
```

but we have greatly improved the test readability.

One thing to note is that we have lost a view of the full actual object when asserting on the single field. There is probably a way of inproving on this.

Another thing to notice is that we have flattened the address assertions. If we wanted to have a
nested assertion (maybe we want to reuse the address assertion elsewhere), it is also doable:

We can have an `AddressAssert`:

```java
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
```

and add a method in `CustomerAssert` that uses it:

```java
CustomerAssert address(Consumer<AddressAssert> addressAssertions) {
    addressAssertions.accept(AddressAssert.assertThat(actual.address()));
    return this;
}
```

We can now use it in our test:

```java
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
```

One issue with this approach is that it is not `SoftAssertions` friendly. We will have a look at a way of solving the issue
in another blog post.

## AssertJ Conditions

`AssertJ`'s `AbstractAssert` provides the `is` and `has` method that can take `Condition` as parameters. Conditions
are basically `Predicate`s with a description. We can create a condition to check our customer's first name as

```java
 Condition<Customer> firstName(String expected) {
     return new Condition<>(it -> expected.equals(it.firstName()), "first name %s".formatted(expected));
 }
```

and use it as

```java
@Test
void separateAssertionsWithConditions() {
    var customer = retrieveCustomer();

    assertThat(customer).has(firstName("Mike"));
}
```

which gives us a failure message like:

```
Expecting actual:
  Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=Postcode[value=M15 5HT]]]
to have first name 'Mike'
```

The failure message is only showing the expected first name, but it would be useful to see the actual value we got, as we do 
when we use custom asserts. This is doable by using a `VerboseCondition`:

```java
static Condition<Customer> firstName(String expected) {
    return VerboseCondition.verboseCondition(
            it -> expected.equals(it.firstName()),
            "first name: '%s'".formatted(expected),
            it -> "but was: '%s'".formatted(it.firstName())
    );
}
```

Which yields:

```
Expecting actual:
  Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=Postcode[value=M15 5HT]]]
to have first name: 'Mike'but was: 'John'
```

We can add verbose conditions for other fields:

```java
static Condition<Customer> lastName(String expected) {
    return verboseCondition(
            it -> expected.equals(it.lastName()),
            "last name: '%s'".formatted(expected),
            it -> "but was: '%s'".formatted(it.lastName())
    );
}

static Condition<Customer> dateOfBirth(LocalDate expected) {
    return verboseCondition(
            it -> expected.equals(it.dateOfBirth()),
            "date of birth: '%s'".formatted(expected),
            it -> "but was: '%s'".formatted(it.dateOfBirth())
    );
}
```

We easily notice some duplication here: the only thing that varies is the way we get the actual value and the field's description.
We can make ourselves a factory method:

```java
static <T, K> Condition<T> equals(String description, K expected, Function<T, K> f) {
    return verboseCondition(
            it -> expected.equals(f.apply(it)),
            "%s: '%s'".formatted(description, expected),
            it -> " but was: '%s'".formatted(f.apply(it))
    );
}
```

Which leads to:

```java
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
```

We can then use the `AllOf` condition to join multiple assertions on our Customer:

```java
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
```

This approach satisfies our requirement `1.` and `3.`

Let's have a look at the test failure message:

```
Expecting actual:
  Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=M15 5HT]]
to have:
[✗] all of:[
   [✓] first name: 'John',
   [✓] last name: 'Doe',
   [✓] date of birth: '1980-12-11',
   [✓] address line 1: '12 Chestnut close',
   [✓] address line 2: '',
   [✓] address line 3: 'South Woodford',
   [✗] address town: 'London' but was: 'Manchester',
   [✗] address postcode: 'E18 5HT' but was: 'M15 5HT'
]
```

I would say is the most clear message so far! We also have the actual object printed on top.

### Nested conditions

What if we wanted the address conditions to be reusable, i.e. expressed as `Condition<Address>`?

```java
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
```

`AssertJ` has the concept of `NestableCondition` that we can use::

```java
@SafeVarargs
static Condition<Customer> customerWith(Condition<Customer>... conditions) {
    return nestable("customer", conditions);
}

@SafeVarargs
static Condition<Customer> address(Condition<Address>... conditions) {
    return nestable("address", Customer::address, conditions);
}
```

And we can use it in our test as:

```java
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
```

with the following error message:

```
Expecting actual:
  Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=M15 5HT]]
to be:
[✗] customer:[
   [✓] first name: 'John',
   [✗] last name: 'Tilbury' but was: 'Doe',
   [✓] date of birth: '1980-12-11',
   [✗] address:[
      [✓] line 1: '12 Chestnut close',
      [✓] line 2: '',
      [✓] line 3: 'South Woodford',
      [✗] town: 'London' but was: 'Manchester',
      [✗] postcode: 'E18 5HT' but was: 'M15 5HT'
   ]
]
```

This satisfies all of our 3 initial requirements!

## Custom assertions VS Conditions

I can see two advantages of the `Condition` approach versus the custom assertions:

1. Assertion message is cleaner
2. If we have a project with multiple modules, and a condition is only required in one module, we can define it only where it is used. With custom assertions all assertions will have to be part of the same class. This is even more important 
if we use Kotlin, where we might want to add an extension property/function in a different module, and define the assertion in the same module.

There is yet another advantage: conditions can also be used when asserting on collections.

## Asserting on collections

When we assert on a collection using `AssertJ`, we face the same issues that we've seen for a single item.

We can use one of the `containsExactly*` methods available:

```java
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
```

which gives us as error message:

```
Expecting actual:
  [Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=Postcode[value=M15 5HT]]],
    Customer[firstName=Mike, lastName=Bellview, dateOfBirth=1985-04-10, address=Address[line1=5 Holy street, line2=, line3=, town=London, postcode=Postcode[value=E18 4AB]]]]
to contain exactly in any order:
  [Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=Postcode[value=M15 5HT]]],
    Customer[firstName=Mike, lastName=Bellview, dateOfBirth=1985-04-10, address=Address[line1=5 Holy street, line2=, line3=, town=Glasgow, postcode=Postcode[value=G52 4AB]]]]
elements not found:
  [Customer[firstName=Mike, lastName=Bellview, dateOfBirth=1985-04-10, address=Address[line1=5 Holy street, line2=, line3=, town=Glasgow, postcode=Postcode[value=G52 4AB]]]]
and elements not expected:
  [Customer[firstName=Mike, lastName=Bellview, dateOfBirth=1985-04-10, address=Address[line1=5 Holy street, line2=, line3=, town=London, postcode=Postcode[value=E18 4AB]]]]
```

This approach has the same issues that we have discussed about assertion using the `equals` method on a single object.

Another approach is to use `Condition`s and `SoftAssertions`:

```java
@Test
void assertOnCollectionWithConditions() {
        var customers = retrieveCustomers();

        assertSoftly(softly ->
            softly.assertThat(customers)
                .haveExactly(1,
                        customerWith(
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
                    customerWith(
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
```

which gives us

```
Multiple Failures (2 failures)
-- failure 1 --
Expecting elements:
  [Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=Postcode[value=M15 5HT]]],
    Customer[firstName=Mike, lastName=Bellview, dateOfBirth=1985-04-10, address=Address[line1=5 Holy street, line2=, line3=, town=London, postcode=Postcode[value=E18 4AB]]]]
to have exactly 1 times a customer with:[
   first name 'John',
   last name 'Doe',
   date of birth '1980-12-11',
   address:[
      line 1 '12 Chestnut close',
      line 2 '',
      line 3 'South Woodford',
      town 'London',
      postcode 'Postcode[value=E18 5HT]'
   ]
]
at ModelTest.lambda$assertOnCollectionWithConditions$0(ModelTest.java:238)
-- failure 2 --
Expecting elements:
  [Customer[firstName=John, lastName=Doe, dateOfBirth=1980-12-11, address=Address[line1=12 Chestnut close, line2=, line3=South Woodford, town=Manchester, postcode=Postcode[value=M15 5HT]]],
    Customer[firstName=Mike, lastName=Bellview, dateOfBirth=1985-04-10, address=Address[line1=5 Holy street, line2=, line3=, town=London, postcode=Postcode[value=E18 4AB]]]]
to have exactly 1 times a customer with:[
   first name 'Mike',
   last name 'Bellview',
   date of birth '1985-04-10',
   address:[
      line 1 '5 Holy Street',
      line 2 '',
      line 3 '',
      town 'Glasgow',
      postcode 'Postcode[value=G52 4AB]'
   ]
]
```

This again is more readable than the message we got using the `contains` approach.


## Summary

In this blog post we've discussed test assertions and how we can make them better using `AssertJ`. 

Assertions are one of the key parts of a test. Having the clean, readable and free from noise helps us to focus on what the main objective of the test is, 
and to quickly understand the behaviour of the system.

Assertion messages are very important to quickly diagnose test failures. The last thing we want to do
is spend a good few minutes to inspect the assertion message to find what was actually wrong. 

Test should be precise to make our system and data model easier to change and evolve.

`AssertJ` gives us different tools to help with this, custom assertions and conditions.

We've compared the two approaches and found that using condition is more flexible and gives us clearer failure messages.

_Enjoy your testing!!_
