package at.fhv.sys.hotel.domain.valueObject;

import java.util.UUID;

public class BookingId {
    private UUID bookingId;
    public BookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }
    public UUID getBookingId() {
        return bookingId;
    }
    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }
}
