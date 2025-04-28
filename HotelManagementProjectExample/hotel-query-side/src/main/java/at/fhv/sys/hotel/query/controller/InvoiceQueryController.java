package at.fhv.sys.hotel.query.controller;

import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import at.fhv.sys.hotel.models.InvoiceQueryModel;
import at.fhv.sys.hotel.projection.InvoiceProjection;
import at.fhv.sys.hotel.service.InvoiceService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvoiceQueryController {

    @Inject
    InvoiceProjection projection;

    @Inject
    InvoiceService service;

    @POST
    @Path("/invoiceCreated")
    public void invoiceCreated(InvoiceCreated evt) {
        projection.processInvoiceCreatedEvent(evt);
    }

    @GET
    @Path("/invoices")
    public List<InvoiceQueryModel> getInvoices(@QueryParam("bookingId") String bookingId) {
        return service.getInvoicesByBooking(bookingId);
    }
}