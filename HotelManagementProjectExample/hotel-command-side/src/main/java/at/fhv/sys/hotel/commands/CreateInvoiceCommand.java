package at.fhv.sys.hotel.commands;

public class CreateInvoiceCommand {
    private final String bookingId;

    public CreateInvoiceCommand(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return bookingId;
    }
}