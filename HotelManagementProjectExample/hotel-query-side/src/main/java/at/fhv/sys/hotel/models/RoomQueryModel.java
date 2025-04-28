package at.fhv.sys.hotel.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class RoomQueryModel {

    @Id
    @Column(name = "room_id")
    private String roomId;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "is_available")
    private double isAvailable;

    private double price;
    private int capacity;

    public RoomQueryModel() {}

    public RoomQueryModel(String roomId, String roomType, double isAvailable, double price, int capacity) {
        this.roomId      = roomId;
        this.roomType    = roomType;
        this.isAvailable = isAvailable;
        this.price       = price;
        this.capacity    = capacity;
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public double getIsAvailable() { return isAvailable; }
    public void setIsAvailable(double isAvailable) { this.isAvailable = isAvailable; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}