package at.fhv.sys.hotel.store;

import at.fhv.sys.hotel.domain.model.Customer;
import at.fhv.sys.hotel.domain.valueObject.CustomerId;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CustomerStateStore implements InMemoryStore<CustomerId, Customer>{
    private final ConcurrentHashMap<CustomerId, Customer> cache = new ConcurrentHashMap<>();
    @Override
    public ConcurrentHashMap<CustomerId, Customer> data() {
        return cache;
    }
}
