package at.fhv.sys.hotel.controller;

import at.fhv.sys.hotel.client.EventBusClient;
import at.fhv.sys.hotel.commands.BookRoomCommand;
import at.fhv.sys.hotel.commands.BookingAggregate;
import at.fhv.sys.hotel.commands.CreateCustomerCommand;
import at.fhv.sys.hotel.commands.CustomerAggregate;
import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.store.CustomerStateStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Path("/api/customer")
public class CustomerCommandController {
    @Inject
    BookingAggregate bookingAggregate;

    @Inject
    CustomerAggregate customerAggregate;

    @Inject
    @RestClient
    EventBusClient eventBusClient;
    @Inject
    CustomerStateStore customerStateStore;

    @POST
    @Path("/create")
    public Response createCustomer(
            @QueryParam("name") String name,
            @QueryParam("email") String email
    ) {
        String customerId = UUID.randomUUID().toString();

        CreateCustomerCommand cmd = new CreateCustomerCommand(customerId, name, email);
        CustomerCreated event = customerAggregate.handle(cmd);

        return Response.ok(event).build();
    }


    @POST
    @Path("/{customerId}/update")
    public String updateCustomer(@PathParam("customerId") String customerId,
                                 @QueryParam("email") String email) {
        return "Customer updated";
    }

    @POST
    @Path("/{customerId}/delete")
    public String deleteCustomer(@PathParam("customerId") String customerId) {
        return "Customer deleted";
    }

    @GET
    @Path("/debug")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings() {
        System.out.println(customerStateStore.data().values());
        return Response.ok(customerStateStore.data().values()).build();
    }
}