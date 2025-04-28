package at.fhv.sys.hotel.projection;

import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import at.fhv.sys.hotel.models.BookingQueryModel;
import at.fhv.sys.hotel.service.BookingService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BookingProjection {

    @Inject
    BookingService bookingService;

    public void processRoomBookedEvent(RoomBooked event) {
        BookingQueryModel booking = new BookingQueryModel(
                event.getBookingId(),
                event.getCustomerId(),
                event.getRoomIds().toString(),
                event.getStartDate(),
                event.getEndDate()
        );
        bookingService.createBooking(booking);
    }

    public void processBookingCancelledEvent(BookingCancelled event) {
        bookingService.deleteBooking(event.getBookingId());
    }
}