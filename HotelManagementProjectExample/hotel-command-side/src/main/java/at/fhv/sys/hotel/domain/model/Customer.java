package at.fhv.sys.hotel.domain.model;

import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.domain.valueObject.CustomerId;

public class Customer {
    private CustomerId customerId;
    private String name;
    private String email;

    private Customer(CustomerId customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }

    public static Customer create(CustomerId customerId, String name, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email must be valid.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty.");
        }
        return new Customer(customerId, name, email);
    }

    public CustomerCreated toEvent() {
        return new CustomerCreated(
                customerId.getId(),
                name,
                email
        );
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + customerId.getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}