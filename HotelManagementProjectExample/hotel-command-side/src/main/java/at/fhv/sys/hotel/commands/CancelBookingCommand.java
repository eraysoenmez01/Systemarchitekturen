package at.fhv.sys.hotel.commands;

public class CancelBookingCommand {
    private String bookingId;

    public CancelBookingCommand() {}

    public CancelBookingCommand(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}