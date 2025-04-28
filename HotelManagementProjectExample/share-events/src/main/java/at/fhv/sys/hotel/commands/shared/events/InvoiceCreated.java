package at.fhv.sys.hotel.commands.shared.events;

import java.time.LocalDate;

public class InvoiceCreated {

    private String invoiceId;
    private String bookingId;
    private double amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private String roomId;

    public InvoiceCreated() {
    }

    public InvoiceCreated(String invoiceId, String bookingId, double amount, String paymentMethod, LocalDate paymentDate, String roomId) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.roomId = roomId;
    }

    public InvoiceCreated(String invoiceId, String bookingId, double amount) {
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "InvoiceCreated{" +
                "invoiceId='" + invoiceId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentDate=" + paymentDate +
                ", roomId='" + roomId + '\'' +
                '}';
    }
}