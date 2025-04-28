package at.fhv.sys.hotel.store;

import at.fhv.sys.hotel.domain.model.Room;
import at.fhv.sys.hotel.domain.valueObject.RoomId;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class RoomStateStore implements InMemoryStore<RoomId,Room> {
    private final ConcurrentHashMap<RoomId, Room> cache = new ConcurrentHashMap<>();

    @Override
    public ConcurrentHashMap<RoomId, Room> data() {
        return cache;
    }
}