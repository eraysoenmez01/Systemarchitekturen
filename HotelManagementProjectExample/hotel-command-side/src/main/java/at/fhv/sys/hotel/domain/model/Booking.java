package at.fhv.sys.hotel.domain.model;

import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import at.fhv.sys.hotel.domain.valueObject.BookingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Booking {
    private final String bookingId;
    private final String customerId;
    private final List<String> roomIds;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private BookingStatus status;

    private Booking(String bookingId, String customerId, List<String> roomIds, LocalDate startDate, LocalDate endDate) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.roomIds = roomIds;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = BookingStatus.BOOKED;
    }

    public static Booking create(String bookingId, String customerId, List<String> roomIds, LocalDate startDate, LocalDate endDate) {
        Objects.requireNonNull(bookingId, "Booking ID must not be null");
        Objects.requireNonNull(customerId, "Customer ID must not be null");
        Objects.requireNonNull(roomIds, "Room IDs must not be null");
        Objects.requireNonNull(startDate, "Start date must not be null");
        Objects.requireNonNull(endDate, "End date must not be null");

        if (roomIds.isEmpty()) {
            throw new IllegalArgumentException("At least one room must be selected");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        return new Booking(bookingId, customerId, roomIds, startDate, endDate);
    }

    public RoomBooked toEvent(int guests) {
        return new RoomBooked(
                bookingId,
                customerId,
                roomIds,
                startDate.toString(),
                endDate.toString(),
                guests
        );
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
    }

    public BookingStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + bookingId +
                ", room=" + roomIds +
                ", customer=" + customerId+
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }


    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public List<String> getRoomIds() { return roomIds; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}