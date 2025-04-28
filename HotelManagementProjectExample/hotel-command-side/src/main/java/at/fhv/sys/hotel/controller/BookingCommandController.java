package at.fhv.sys.hotel.controller;

import at.fhv.sys.hotel.commands.BookRoomCommand;
import at.fhv.sys.hotel.commands.CancelBookingCommand;
import at.fhv.sys.hotel.commands.BookingAggregate;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import at.fhv.sys.hotel.store.BookingStateStore;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

@Path("/api/booking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingCommandController {

    @Inject
    BookingStateStore bookingStore;

    @Inject
    BookingAggregate bookingAggregate;

    @POST
    @Path("/create")
    public Response createBooking(
            @QueryParam("roomId") String roomId,
            @QueryParam("customerId") String customerId,
            @QueryParam("fromDate")  String fromDate,
            @QueryParam("toDate")    String toDate,
            @QueryParam("guests") int guests
    ) {
        String bookingId = UUID.randomUUID().toString();

        LocalDate start = LocalDate.parse(fromDate);
        LocalDate end   = LocalDate.parse(toDate);

        BookRoomCommand cmd = new BookRoomCommand(
                bookingId,
                customerId,
                Collections.singletonList(roomId),
                start,
                end,
                guests
        );

        bookingAggregate.handle(cmd);

        RoomBooked event = new RoomBooked(
                bookingId,
                customerId,
                Collections.singletonList(roomId),
                start.toString(),
                end.toString(),
                guests
        );
        return Response.ok(event).build();
    }

    @POST
    @Path("/{bookingId}/cancel")
    public Response cancelBooking(
            @PathParam("bookingId") String bookingId
    ) {
        CancelBookingCommand cmd = new CancelBookingCommand(bookingId);
        bookingAggregate.handle(cmd);
        return Response.ok("Booking " + bookingId + " cancelled").build();
    }


    @GET
    @Path("/debug")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBookings() {
        System.out.println(bookingStore.data().values());
        return Response.ok(bookingStore.data().values()).build();
    }
}