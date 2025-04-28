package at.fhv.sys.hotel.store;

import at.fhv.sys.hotel.domain.model.Booking;
import at.fhv.sys.hotel.domain.valueObject.BookingId;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class BookingStateStore implements InMemoryStore<BookingId, Booking> {
    private final ConcurrentHashMap<BookingId, Booking> cache = new ConcurrentHashMap<>();
    @Override
    public ConcurrentHashMap<BookingId, Booking> data() {
        return cache;
    }

}
