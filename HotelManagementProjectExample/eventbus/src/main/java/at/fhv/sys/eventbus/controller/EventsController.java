package at.fhv.sys.eventbus.controller;

import at.fhv.sys.eventbus.persistence.RoomEntity;
import at.fhv.sys.eventbus.persistence.RoomRepository;
import at.fhv.sys.eventbus.persistence.StoredEvent;
import at.fhv.sys.eventbus.persistence.StoredEventRepository;
import at.fhv.sys.eventbus.services.EventProcessingService;
import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logmanager.Logger;
import java.util.List;

import java.util.stream.Collectors;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventsController {
    @Inject
    EventProcessingService eventStoreService;

    @Inject
    StoredEventRepository repository;

    @Inject
    RoomRepository roomRepository;

    public EventsController() {
    }

    @POST
    @Path("/customerCreated")
    public Response customerCreated(CustomerCreated event) {
        Logger.getAnonymousLogger().info("Received event: " + event);
        eventStoreService.processEvent("customer-" + event.getCustomerId(), event);
        return Response.ok(event).build();
    }

    @POST
    @Path("/roomBooked")
    public Response roomBooked(RoomBooked event) {
        Logger.getAnonymousLogger().info("📨 Received RoomBooked event: " + event);
        eventStoreService.processEvent("booking-" + event.getBookingId(), event);
        return Response.ok(event).build();
    }

    @POST
    @Path("/invoiceCreated")
    public Response invoiceCreated(InvoiceCreated event) {
        Logger.getAnonymousLogger().info("📨 Received InvoiceCreated event: " + event);
        eventStoreService.processEvent("invoice-" + event.getInvoiceId(), event);
        return Response.ok(event).build();
    }
    @GET
    @Path("/events")
    @Transactional
    public Response getAllEvents() {
        List<Object> events = repository.listAll().stream()
                .map(StoredEvent::deserializeEvent)
                .collect(Collectors.toList());
        return Response.ok(events).build();
    }


    @POST
    @Path("/booking/cancel")
    public Response bookingCancel(BookingCancelled event) {
        Logger.getAnonymousLogger().info("📨 Received BookingCancelled: " + event);
        eventStoreService.processEvent("cancel-" + event.getBookingId(), event);
        return Response.ok(event).build();
    }

    @GET
    @Path("/events/deserialized")
    @Transactional
    public List<Object> getDeserializedEvents(@QueryParam("bookingId") String bookingId) {
        return repository.find("bookingId", bookingId)
                .stream()
                .map(StoredEvent::deserializeEvent)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/rooms/available")
    public List<RoomEntity> availableRooms(@QueryParam("capacity") int capacity) {
        return roomRepository.list(
                "isAvailable = ?1 and capacity >= ?2",
                1, capacity
        );
    }

}