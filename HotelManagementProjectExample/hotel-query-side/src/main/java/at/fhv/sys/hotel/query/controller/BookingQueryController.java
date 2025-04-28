package at.fhv.sys.hotel.query.controller;

import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import at.fhv.sys.hotel.models.BookingQueryModel;
import at.fhv.sys.hotel.models.RoomQueryModel;
import at.fhv.sys.hotel.projection.BookingProjection;
import at.fhv.sys.hotel.service.BookingService;
import at.fhv.sys.hotel.service.RoomService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logmanager.Logger;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingQueryController {

    @Inject
    BookingProjection bookingProjection;

    @Inject
    BookingService bookingService;

    @Inject
    RoomService roomService;

    @POST
    @Path("/roomBooked")
    public Response roomBooked(RoomBooked event) {
        Logger.getAnonymousLogger().info(" Processing RoomBooked: " + event);
        bookingProjection.processRoomBookedEvent(event);
        return Response.ok().build();
    }

    @POST
    @Path("/bookingCancelled")
    public Response bookingCancelled(BookingCancelled event) {
        Logger.getAnonymousLogger().info(" Processing BookingCancelled: " + event);
        bookingProjection.processBookingCancelledEvent(event);
        return Response.ok().build();
    }

    @GET
    @Path("/bookings")
    public List<BookingQueryModel> getBookings(
            @QueryParam("from") String from,
            @QueryParam("to")   String to
    ) {
        LocalDate start = LocalDate.parse(from);
        LocalDate end   = LocalDate.parse(to);

        return bookingService.getAllBookings().stream()
                .filter(b -> {
                    LocalDate s = LocalDate.parse(b.getStartDate());
                    LocalDate e = LocalDate.parse(b.getEndDate());
                    return !s.isAfter(end) && !e.isBefore(start);
                })
                .collect(Collectors.toList());
    }

    @GET
    @Path("/free-rooms")
    public List<RoomQueryModel> getFreeRooms(
            @QueryParam("from")     String from,
            @QueryParam("to")       String to,
            @QueryParam("capacity") int capacity
    ) {
        LocalDate start = LocalDate.parse(from);
        LocalDate end   = LocalDate.parse(to);

        List<RoomQueryModel> allRooms = roomService.getAllRooms();

        List<String> bookedIds = bookingService.getAllBookings().stream()
                .filter(b -> {
                    LocalDate s = LocalDate.parse(b.getStartDate());
                    LocalDate e = LocalDate.parse(b.getEndDate());
                    return !s.isAfter(end) && !e.isBefore(start);
                })
                .flatMap(b -> {
                    String raw = b.getRoomIds().trim();
                    if (raw.startsWith("[") && raw.endsWith("]")) {
                        raw = raw.substring(1, raw.length() - 1);
                    }
                    if (raw.isEmpty()) {
                        return Stream.<String>empty();
                    }
                    return Arrays.stream(raw.split(","))
                            .map(String::trim);
                })
                .collect(Collectors.toList());

        return allRooms.stream()
                .filter(r -> !bookedIds.contains(r.getRoomId()))
                .filter(r -> r.getCapacity() >= capacity)
                .collect(Collectors.toList());
    }
}