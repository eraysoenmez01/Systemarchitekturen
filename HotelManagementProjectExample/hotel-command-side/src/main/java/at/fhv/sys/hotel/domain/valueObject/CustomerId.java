package at.fhv.sys.hotel.domain.valueObject;

import java.util.UUID;

public class CustomerId {
    private final UUID id;

    public CustomerId(UUID id) {
        this.id = id;
    }

    public String getId() {
        return id.toString();
    }
}