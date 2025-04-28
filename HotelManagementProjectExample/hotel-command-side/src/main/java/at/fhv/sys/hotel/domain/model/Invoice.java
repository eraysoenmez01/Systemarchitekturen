package at.fhv.sys.hotel.domain.model;

import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.domain.valueObject.*;

import java.time.LocalDate;

public class Invoice {

    private final InvoiceId invoiceId;
    private final BookingId bookingId;
    private final double amount;
    private final String paymentMethod;
    private final LocalDate paymentDate;
    private final RoomId roomId;

    private Invoice(InvoiceId invoiceId, BookingId bookingId, double amount, String paymentMethod, LocalDate paymentDate, RoomId roomId) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.roomId = roomId;
    }

    public static Invoice create(InvoiceId invoiceId, BookingId bookingId, double amount, String paymentMethod, LocalDate paymentDate, RoomId roomId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invoice amount must be positive");
        }

        return new Invoice(invoiceId, bookingId, amount, paymentMethod, paymentDate, roomId);
    }

    public InvoiceCreated toEvent() {
        return new InvoiceCreated(
                invoiceId.getInvoiceId().toString(),
                bookingId.getBookingId().toString(),
                amount,
                paymentMethod,
                paymentDate,
                roomId.getRoomId().toString()
        );
    }


    public InvoiceId getInvoiceId() { return invoiceId; }
    public BookingId getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public RoomId getRoomId() { return roomId; }
}
