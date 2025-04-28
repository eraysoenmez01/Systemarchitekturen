package at.fhv.sys.hotel.domain.valueObject;

import java.util.UUID;

public class RoomId {
    private UUID roomId;

    public RoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }
}
