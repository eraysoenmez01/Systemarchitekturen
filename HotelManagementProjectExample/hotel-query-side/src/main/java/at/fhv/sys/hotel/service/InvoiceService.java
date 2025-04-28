package at.fhv.sys.hotel.service;

import at.fhv.sys.hotel.models.InvoiceQueryModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class InvoiceService {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void createInvoice(InvoiceQueryModel inv) {
        em.persist(inv);
    }

    public List<InvoiceQueryModel> getInvoicesByBooking(String bookingId) {
        return em.createQuery(
                        "SELECT i FROM InvoiceQueryModel i WHERE i.bookingId = :b",
                        InvoiceQueryModel.class)
                .setParameter("b", bookingId)
                .getResultList();
    }
}