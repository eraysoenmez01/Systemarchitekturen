package at.fhv.sys.hotel.commands;

import at.fhv.sys.hotel.client.EventBusClient;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.domain.model.Booking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import at.fhv.sys.hotel.store.BookingStateStore;
import at.fhv.sys.hotel.domain.valueObject.BookingId;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ApplicationScoped
public class BookingAggregate {


    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @Inject
    @RestClient
    EventBusClient eventClient;

    public String handle(BookRoomCommand command) {
        Logger logger = Logger.getAnonymousLogger();

        Booking booking = Booking.create(
                command.getBookingId(),
                command.getCustomerId(),
                command.getRoomIds(),
                command.getStartDate(),
                command.getEndDate()
        );
        bookings.put(booking.getBookingId(), booking);

        RoomBooked event = booking.toEvent(command.getGuests());
        eventClient.processRoomBookedEvent(event);
        logger.info(" Sending RoomBooked event: " + event);

        return booking.getBookingId();
    }

    public String handle(CancelBookingCommand command) {
        Logger logger = Logger.getAnonymousLogger();

        Booking booking = bookings.get(command.getBookingId());
        if (booking == null) {
            throw new IllegalArgumentException("Keine Buchung mit ID " + command.getBookingId());
        }
        booking.cancel();

        BookingCancelled event = new BookingCancelled(
                command.getBookingId(),
                booking.getRoomIds()
        );
        eventClient.processCancelBookingEvent(event);
        logger.info(" Sending BookingCancelled event: " + event);

        return command.getBookingId();
    }
}