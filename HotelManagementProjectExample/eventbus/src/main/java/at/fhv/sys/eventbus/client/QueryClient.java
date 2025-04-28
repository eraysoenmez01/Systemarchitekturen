package at.fhv.sys.eventbus.client;

import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey="hotel-query-api-client")
@Path("/api")
public interface QueryClient {

    @POST
    @Path("/customerCreated")
    @Consumes(MediaType.APPLICATION_JSON)
    void forwardCustomerCreatedEvent(CustomerCreated event);

    @POST
    @Path("/roomBooked")
    @Consumes(MediaType.APPLICATION_JSON)
    void forwardRoomBookedEvent(RoomBooked event);

    @POST
    @Path("/invoiceCreated")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    InvoiceCreated processInvoiceCreatedEvent(InvoiceCreated event);

    @POST
    @Path("/bookingCancelled")
    @Consumes(MediaType.APPLICATION_JSON)
    void forwardBookingCancelledEvent(BookingCancelled event);
}
