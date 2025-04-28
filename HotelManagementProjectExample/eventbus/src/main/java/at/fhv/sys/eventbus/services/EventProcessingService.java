package at.fhv.sys.eventbus.services;

import at.fhv.sys.eventbus.client.QueryClient;
import at.fhv.sys.eventbus.persistence.RoomEntity;
import at.fhv.sys.eventbus.persistence.RoomRepository;
import at.fhv.sys.eventbus.persistence.StoredEvent;
import at.fhv.sys.eventbus.persistence.StoredEventRepository;
import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Logger;

@ApplicationScoped
public class EventProcessingService {
    private static final Logger LOGGER = Logger.getLogger(EventProcessingService.class.getName());

    @Inject
    @RestClient
    QueryClient queryClient;

    @Inject
    RoomRepository roomRepository;

    @Inject
    StoredEventRepository repository;

    @Inject
    EventProjectionService projectionService;

    @Transactional
    public void processEvent(String stream, Object eventObject) {
        LOGGER.info("Processing event: " + eventObject.getClass().getSimpleName());

        if (eventObject instanceof RoomBooked roomBooked) {
            validateAndUpdateRoomForBooking(roomBooked);
        }

        if (eventObject instanceof BookingCancelled cancelled) {
            makeRoomsAvailable(cancelled);
        }

        String json = JsonbBuilder.create().toJson(eventObject);
        String eventType = eventObject.getClass().getSimpleName();

        String bookingId = extractBookingId(eventObject);
        String customerId = extractCustomerId(eventObject);

        StoredEvent storedEvent = new StoredEvent(
                stream,
                json,
                eventType,
                bookingId,
                customerId
        );
        repository.persist(storedEvent);
        LOGGER.info("Event stored with ID: " + storedEvent.id);

        forwardToQueryService(eventObject);

        projectionService.projectEvent(eventObject);
    }

    private void validateAndUpdateRoomForBooking(RoomBooked roomBooked) {
        try {
            int roomId = Integer.parseInt(roomBooked.getRoomIds().get(0));
            RoomEntity room = roomRepository.findById(roomId);

            if (room == null) {
                throw new IllegalArgumentException("Room not found: " + roomId);
            }

            int guests = roomBooked.getGuests();
            if (guests > room.getCapacity()) {
                throw new IllegalArgumentException(
                        "Room " + roomId +
                                " capacity is " + room.getCapacity() +
                                ", cannot book for " + guests + " guests"
                );
            }

            if (room.getIsAvailable() <= 0) {
                throw new IllegalArgumentException("Room " + roomId + " is not available");
            }

            // Mark room as unavailable
            room.setIsAvailable(0.0);
            LOGGER.info("Room " + roomId + " marked as unavailable");
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid room ID format in RoomBooked event");
        }
    }

    private void makeRoomsAvailable(BookingCancelled cancelled) {
        for (String idStr : cancelled.getRoomIds()) {
            try {
                int roomId = Integer.parseInt(idStr);
                RoomEntity room = roomRepository.findById(roomId);
                if (room != null) {
                    room.setIsAvailable(1.0);
                    LOGGER.info("Room " + roomId + " marked as available");
                }
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid room ID format in BookingCancelled event: " + idStr);
            }
        }
    }

    private String extractBookingId(Object eventObject) {
        if (eventObject instanceof RoomBooked rb) {
            return rb.getBookingId();
        } else if (eventObject instanceof BookingCancelled bc) {
            return bc.getBookingId();
        } else if (eventObject instanceof InvoiceCreated ic) {
            return ic.getBookingId();
        }
        return null;
    }

    private String extractCustomerId(Object eventObject) {
        if (eventObject instanceof CustomerCreated cc) {
            return cc.getCustomerId();
        } else if (eventObject instanceof RoomBooked rb) {
            return rb.getCustomerId();
        }
        return null;
    }

    private void forwardToQueryService(Object eventObject) {
        try {
            if (eventObject instanceof RoomBooked rb) {
                queryClient.forwardRoomBookedEvent(rb);
                LOGGER.info("RoomBooked event forwarded to Query Service");
            } else if (eventObject instanceof BookingCancelled bc) {
                queryClient.forwardBookingCancelledEvent(bc);
                LOGGER.info("BookingCancelled event forwarded to Query Service");
            } else if (eventObject instanceof CustomerCreated cc) {
                queryClient.forwardCustomerCreatedEvent(cc);
                LOGGER.info("CustomerCreated event forwarded to Query Service");
            } else if (eventObject instanceof InvoiceCreated ic) {
                queryClient.processInvoiceCreatedEvent(ic);
                LOGGER.info("InvoiceCreated event forwarded to Query Service");
            }
        } catch (Exception e) {
            LOGGER.severe("Error forwarding event to Query Service: " + e.getMessage());
        }
    }
}