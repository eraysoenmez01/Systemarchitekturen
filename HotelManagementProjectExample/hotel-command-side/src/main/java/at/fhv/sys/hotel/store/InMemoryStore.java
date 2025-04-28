package at.fhv.sys.hotel.store;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public interface InMemoryStore<ID, T> {
    ConcurrentHashMap<ID, T> data();
    default Optional<T> find(ID id) {
        return Optional.ofNullable(data().get(id));
    }
    default void put(ID id, T value) {
        data().put(id, value);
    }
    default void delete(ID id) {
        data().remove(id);
    }
    default void clear() {
        data().clear();
    }
}