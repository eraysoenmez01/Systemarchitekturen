package at.fhv.sys.hotel.client;

import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import at.fhv.sys.hotel.domain.model.Room;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey="hotel-eventbus-api-client")
@Path("/api")
public interface EventBusClient {

    @POST
    @Path("/customerCreated")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CustomerCreated processCustomerCreatedEvent(CustomerCreated event);

    @POST
    @Path("/roomBooked")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    RoomBooked processRoomBookedEvent(RoomBooked event);

    @POST
    @Path("/invoiceCreated")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    InvoiceCreated processInvoiceCreatedEvent(InvoiceCreated event);

    @POST
    @Path("/booking/cancel")
    @Consumes(MediaType.APPLICATION_JSON)
    void processCancelBookingEvent(BookingCancelled event);

    @GET
    @Path("/rooms/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    Room getRoomById(@PathParam("roomId") String roomId);

    @GET
    @Path("/deserialized")
    @Produces(MediaType.APPLICATION_JSON)
    List<Object> getDeserializedEvents();
}
