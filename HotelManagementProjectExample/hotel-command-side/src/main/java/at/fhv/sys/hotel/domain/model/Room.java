package at.fhv.sys.hotel.domain.model;

import at.fhv.sys.hotel.domain.valueObject.RoomId;
import at.fhv.sys.hotel.domain.valueObject.RoomType;

import java.util.UUID;

public class Room {
    private RoomId roomId;
    private double price;
    private int capacity;
    private double isAvailable;
    private RoomType roomType;

    public Room(RoomId roomId, RoomType roomType, double isAvailable, double price, int capacity) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.isAvailable = isAvailable;
        this.price = price;
        this.capacity = capacity;
    }

    public RoomId getId() {
        return roomId;
    }

    public void setId(RoomId id) {
        this.roomId = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(double isAvailable) {
        this.isAvailable = isAvailable;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
}
