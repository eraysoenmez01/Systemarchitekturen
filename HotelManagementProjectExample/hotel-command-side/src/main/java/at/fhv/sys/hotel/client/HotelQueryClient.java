package at.fhv.sys.hotel.client;

import at.fhv.sys.hotel.domain.model.Booking;
import at.fhv.sys.hotel.domain.model.Room;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@RegisterRestClient(configKey="hotel-query-api-client")
@Path("/api")
public interface HotelQueryClient {

    @GET
    @Path("/rooms/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    Room getRoomById(@PathParam("roomId") String roomId);

    @GET
    @Path("/free-rooms")
    @Produces(MediaType.APPLICATION_JSON)
    List<Room> getFreeRooms(
            @QueryParam("from")     String from,
            @QueryParam("to")       String to,
            @QueryParam("capacity") int capacity
    );

    @GET
    @Path("/bookings")
    @Produces(MediaType.APPLICATION_JSON)
    List<Booking> getBookings(
            @QueryParam("from") String from,
            @QueryParam("to")   String to
    );
}