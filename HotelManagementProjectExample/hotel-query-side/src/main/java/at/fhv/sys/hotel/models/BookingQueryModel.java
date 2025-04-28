package at.fhv.sys.hotel.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BookingQueryModel {

    @Id
    private String bookingId;
    private String customerId;
    private String roomIds;
    private String startDate;
    private String endDate;

    public BookingQueryModel() {}

    public BookingQueryModel(String bookingId, String customerId, String roomIds, String startDate, String endDate) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.roomIds = roomIds;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(String roomIds) {
        this.roomIds = roomIds;
    }
}