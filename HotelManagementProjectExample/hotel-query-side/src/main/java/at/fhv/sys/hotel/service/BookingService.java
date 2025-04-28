package at.fhv.sys.hotel.service;

import at.fhv.sys.hotel.models.BookingQueryModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class BookingService {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void createBooking(BookingQueryModel booking) {
        em.persist(booking);
    }

    public List<BookingQueryModel> getAllBookings() {
        return em.createQuery("SELECT b FROM BookingQueryModel b", BookingQueryModel.class)
                .getResultList();
    }

    @Transactional
    public void deleteBooking(String bookingId) {
        em.createQuery("DELETE FROM BookingQueryModel b WHERE b.bookingId = :id")
                .setParameter("id", bookingId)
                .executeUpdate();
    }
}