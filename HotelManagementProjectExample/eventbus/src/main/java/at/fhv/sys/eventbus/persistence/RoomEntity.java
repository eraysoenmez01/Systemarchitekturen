package at.fhv.sys.eventbus.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class RoomEntity {
    @Id
    @Column(name = "room_id")
    private int roomId;

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(name = "is_available", nullable = false)
    private double isAvailable;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int capacity;

    public RoomEntity() {}

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public double getIsAvailable() { return isAvailable; }
    public void setIsAvailable(double isAvailable) { this.isAvailable = isAvailable; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}