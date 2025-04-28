package at.fhv.sys.hotel.commands;

import at.fhv.sys.hotel.client.EventBusClient;
import at.fhv.sys.hotel.client.HotelQueryClient;
import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.domain.model.Booking;
import at.fhv.sys.hotel.domain.model.Invoice;
import at.fhv.sys.hotel.domain.model.Room;
import at.fhv.sys.hotel.domain.valueObject.BookingId;
import at.fhv.sys.hotel.domain.valueObject.InvoiceId;
import at.fhv.sys.hotel.domain.valueObject.RoomId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class InvoiceAggregate {


    @Inject
    @RestClient
    EventBusClient eventClient;

    @Inject
    @RestClient
    HotelQueryClient hotelQueryClient;

    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public InvoiceCreated handle(CreateInvoiceCommand cmd) {
        Booking booking = bookings.get(cmd.getBookingId());
        if (booking == null) {
            throw new IllegalArgumentException("Keine Buchung mit ID " + cmd.getBookingId());
        }

        String roomId = booking.getRoomIds().get(0);
        long nights = booking.getEndDate().toEpochDay() - booking.getStartDate().toEpochDay();

        Room room = hotelQueryClient.getRoomById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Zimmer nicht gefunden: " + roomId);
        }
        double pricePerNight = room.getPrice();
        System.out.println(pricePerNight);
        double amount = pricePerNight * nights;
        System.out.println(amount);
        String paymentMethod = "CARD";
        LocalDate paymentDate = LocalDate.now();
        System.out.println(paymentDate);
        String invoiceId = UUID.randomUUID().toString();
        System.out.println(paymentDate);
        Invoice invoice = Invoice.create(
                new InvoiceId(UUID.fromString(invoiceId)),
                new BookingId(UUID.fromString(cmd.getBookingId())),
                amount,
                paymentMethod,
                paymentDate,
                new RoomId(UUID.fromString(roomId))
        );
        InvoiceCreated event = invoice.toEvent();
System.out.println(event);
        eventClient.processInvoiceCreatedEvent(event);

        return event;
    }
}