package at.fhv.sys.hotel.commands.shared.events;

import java.util.List;

public class BookingCancelled {
    private String bookingId;
    private List<String> roomIds;

    public BookingCancelled() {}

    public BookingCancelled(String bookingId, List<String> roomIds) {
        this.bookingId = bookingId;
        this.roomIds   = roomIds;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public List<String> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(List<String> roomIds) {
        this.roomIds = roomIds;
    }

    @Override
    public String toString() {
        return "BookingCancelled{" +
                "bookingId='" + bookingId + '\'' +
                ", roomIds=" + roomIds +
                '}';
    }
}