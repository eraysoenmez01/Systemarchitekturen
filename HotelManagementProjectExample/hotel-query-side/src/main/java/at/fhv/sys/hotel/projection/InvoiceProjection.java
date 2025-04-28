package at.fhv.sys.hotel.projection;

import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.models.InvoiceQueryModel;
import at.fhv.sys.hotel.service.InvoiceService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InvoiceProjection {

    @Inject
    InvoiceService service;

    public void processInvoiceCreatedEvent(InvoiceCreated evt) {
        service.createInvoice(new InvoiceQueryModel(
                evt.getInvoiceId(),
                evt.getBookingId(),
                evt.getAmount(),
                evt.getPaymentMethod(),
                evt.getPaymentDate(),
                evt.getRoomId()
        ));
    }
}