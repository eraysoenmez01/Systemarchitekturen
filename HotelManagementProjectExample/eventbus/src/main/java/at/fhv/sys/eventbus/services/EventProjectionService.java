package at.fhv.sys.eventbus.services;

import at.fhv.sys.eventbus.persistence.RoomEntity;
import at.fhv.sys.eventbus.persistence.RoomRepository;
import at.fhv.sys.eventbus.persistence.StoredEvent;
import at.fhv.sys.eventbus.persistence.StoredEventRepository;
import at.fhv.sys.hotel.commands.shared.events.BookingCancelled;
import at.fhv.sys.hotel.commands.shared.events.CustomerCreated;
import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.commands.shared.events.RoomBooked;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;


@ApplicationScoped
public class EventProjectionService {
    private static final Logger LOGGER = Logger.getLogger(EventProjectionService.class.getName());

    @Inject
    StoredEventRepository eventRepository;

    @Inject
    RoomRepository roomRepository;

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info(" Starte Event-Projektion beim Anwendungsstart...");
        projectAllEvents();
        LOGGER.info(" Event-Projektion abgeschlossen.");
    }

    @Transactional
    public void projectAllEvents() {
        List<StoredEvent> allEvents = eventRepository.listAll()
                .stream()
                .sorted(Comparator.comparing(e -> e.occurredAt))
                .toList();

        LOGGER.info("Projiziere " + allEvents.size() + " Events...");

        for (StoredEvent storedEvent : allEvents) {
            try {
                Object event = storedEvent.deserializeEvent();
                projectEvent(event);
            } catch (Exception e) {
                LOGGER.severe("Fehler beim Projizieren von Event " + storedEvent.id + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void projectEvent(Object event) {
        try {
            if (event instanceof CustomerCreated customerCreated) {
                projectCustomerCreated(customerCreated);
            } else if (event instanceof RoomBooked roomBooked) {
                projectRoomBooked(roomBooked);
            } else if (event instanceof BookingCancelled bookingCancelled) {
                projectBookingCancelled(bookingCancelled);
            } else if (event instanceof InvoiceCreated invoiceCreated) {
                projectInvoiceCreated(invoiceCreated);
            } else {
                LOGGER.warning("Unbekannter Event-Typ: " + event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Projizieren des Events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void projectCustomerCreated(CustomerCreated event) {
        LOGGER.info("Projiziere CustomerCreated: " + event.getCustomerId());

        try {
            LOGGER.info(" Customer-Event zur Projektion an Query-Service weitergeleitet: " + event.getCustomerId());
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Projizieren des Customers: " + e.getMessage());
        }
    }

    private void projectRoomBooked(RoomBooked event) {
        LOGGER.info("Projiziere RoomBooked: " + event.getBookingId());

        try {
             for (String roomIdStr : event.getRoomIds()) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    RoomEntity room = roomRepository.findById(roomId);
                    if (room != null) {
                        room.setIsAvailable(0.0);
                        LOGGER.info("Zimmer " + roomId + " als nicht verfügbar markiert");
                    }
                } catch (Exception e) {
                    LOGGER.warning("Fehler beim Aktualisieren der Zimmerverfügbarkeit: " + e.getMessage());
                }
            }

            LOGGER.info("Booking-Event zur Projektion an Query-Service weitergeleitet: " + event.getBookingId());
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Projizieren der Buchung: " + e.getMessage());
        }
    }

    private void projectBookingCancelled(BookingCancelled event) {
        LOGGER.info("Projiziere BookingCancelled: " + event.getBookingId());

        try {
            for (String roomIdStr : event.getRoomIds()) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    RoomEntity room = roomRepository.findById(roomId);
                    if (room != null) {
                        room.setIsAvailable(1.0);
                        LOGGER.info("Zimmer " + roomId + " wieder als verfügbar markiert");
                    }
                } catch (Exception e) {
                    LOGGER.warning("Fehler beim Aktualisieren der Zimmerverfügbarkeit: " + e.getMessage());
                }
            }

            LOGGER.info(" BookingCancelled-Event zur Projektion an Query-Service weitergeleitet: " + event.getBookingId());
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Projizieren der Buchungsstornierung: " + e.getMessage());
        }
    }

    private void projectInvoiceCreated(InvoiceCreated event) {
        LOGGER.info("Projiziere InvoiceCreated: " + event.getInvoiceId());

        try {
            LOGGER.info(" InvoiceCreated-Event zur Projektion an Query-Service weitergeleitet: " + event.getInvoiceId());
        } catch (Exception e) {
            LOGGER.severe("Fehler beim Projizieren der Rechnung: " + e.getMessage());
        }
    }
}