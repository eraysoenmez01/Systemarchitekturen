
package at.fhv.sys.eventbus.persistence;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.*;
        import java.time.Instant;

@Entity
@Table(name = "stored_events")
public class StoredEvent extends PanacheEntity {

    public String stream;

    @Lob
    public String payload;

    public String eventType;

    public Instant occurredAt;

    @Column(name = "booking_id")
    public String bookingId;

    @Column(name = "customer_id")
    public String customerId;

    public StoredEvent() {}

    public StoredEvent(String stream, String payload, String eventType, String bookingId, String customerId) {
        this.stream = stream;
        this.payload = payload;
        this.eventType = eventType;
        this.occurredAt = Instant.now();
        this.bookingId = bookingId;
        this.customerId = customerId;
    }

    public StoredEvent(String stream, String payload, String eventType) {
        this(stream, payload, eventType, null, null);
    }

    public Object deserializeEvent() {
        Jsonb jsonb = JsonbBuilder.create();
        return switch (eventType) {
            case "CustomerCreated" -> jsonb.fromJson(payload, at.fhv.sys.hotel.commands.shared.events.CustomerCreated.class);
            case "RoomBooked" -> jsonb.fromJson(payload, at.fhv.sys.hotel.commands.shared.events.RoomBooked.class);
            case "BookingCancelled" -> jsonb.fromJson(payload, at.fhv.sys.hotel.commands.shared.events.BookingCancelled.class);
            case "InvoiceCreated" -> jsonb.fromJson(payload, at.fhv.sys.hotel.commands.shared.events.InvoiceCreated.class);
            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        };
    }
}
