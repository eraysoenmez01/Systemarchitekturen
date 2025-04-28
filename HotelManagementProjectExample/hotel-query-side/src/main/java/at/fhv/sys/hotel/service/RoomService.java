package at.fhv.sys.hotel.service;

import at.fhv.sys.hotel.models.RoomQueryModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class RoomService {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void createRoom(RoomQueryModel room) {
        em.persist(room);
    }

    public List<RoomQueryModel> getAllRooms() {
        return em.createQuery("SELECT r FROM RoomQueryModel r", RoomQueryModel.class)
                .getResultList();
    }

    public RoomQueryModel getRoomById(String roomId) {
        return em.find(RoomQueryModel.class, roomId);
    }
}