package at.fhv.sys.hotel.commands.shared.events;

import java.util.List;

public class RoomBooked {
    private String bookingId;
    private String customerId;
    private List<String> roomIds;
    private String startDate;
    private String endDate;
    private int guests;

    public RoomBooked() {}

    public RoomBooked(String bookingId, String customerId,
                      List<String> roomIds,
                      String startDate, String endDate,
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

    public String getCustomerId() {
        return customerId;
    }

    public List<String> getRoomIds() {
        return roomIds;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setRoomIds(List<String> roomIds) {
        this.roomIds = roomIds;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "RoomBooked{" +
                "bookingId='" + bookingId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", roomIds=" + roomIds +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", guests=" + guests +
                '}';
    }
}