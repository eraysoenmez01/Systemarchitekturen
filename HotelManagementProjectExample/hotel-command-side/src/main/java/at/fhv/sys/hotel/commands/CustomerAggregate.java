package at.fhv.sys.hotel.commands;

import at.fhv.sys.hotel.client.EventBusClient;
import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.domain.model.Customer;
import at.fhv.sys.hotel.domain.valueObject.CustomerId;
import at.fhv.sys.hotel.store.CustomerStateStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class CustomerAggregate {

    @Inject
    @RestClient
    EventBusClient eventClient;

    @Inject
    CustomerStateStore customerStore;

    public CustomerCreated handle(CreateCustomerCommand command) {
        Customer customer = Customer.create(
                new CustomerId(UUID.fromString(command.customerId())),
                command.name(),
                command.email()
        );

        //da es probleme macht, ist es auskommentiert. die neubeladung der daten geht nicht.
        //customerStore.put(customer.getCustomerId(), customer);
        //System.out.println("Customer created: " + customer.getCustomerId() + " " + customer);

        CustomerCreated event = customer.toEvent();
        Logger.getAnonymousLogger().info("📨 Sending CustomerCreated: " + event);
        eventClient.processCustomerCreatedEvent(event);
        return event;
    }
}