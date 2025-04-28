package at.fhv.sys.eventbus.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoomRepository implements PanacheRepository<RoomEntity> {
    public RoomEntity findById(int id) {
        return find("roomId", id).firstResult();
    }
}