package at.fhv.sys.hotel.commands;

import java.time.LocalDate;
import java.util.List;


public class BookRoomCommand {
    private String bookingId;
    private String customerId;
    private List<String> roomIds;
    private LocalDate startDate;
    private LocalDate endDate;
    private int guests;

    public BookRoomCommand() {}

    public BookRoomCommand(String bookingId,
                           String customerId,
                           List<String> roomIds,
                           LocalDate startDate,
                           LocalDate endDate,
                           int guests) {
        this.bookingId  = bookingId;
        this.customerId = customerId;
        this.roomIds    = roomIds;
        this.startDate  = startDate;
        this.endDate    = endDate;
        this.guests     = guests;
    }

    public int getGuests() {
        return guests;
    }
    public void setGuests(int guests) {
        this.guests = guests;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<String> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(List<String> roomIds) {
        this.roomIds = roomIds;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}