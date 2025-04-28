package at.fhv.sys.hotel.projection;

import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.models.CustomerQueryModel;
import at.fhv.sys.hotel.service.CustomerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class CustomerProjection {

    @Inject
    CustomerService customerService;


    public void processCustomerCreatedEvent(CustomerCreated customerCreatedEvent) {
        Logger.getAnonymousLogger().info("Processing event: " + customerCreatedEvent);
        customerService.createCustomer(new CustomerQueryModel(customerCreatedEvent.getCustomerId(), customerCreatedEvent.getEmail()));

    }
}
