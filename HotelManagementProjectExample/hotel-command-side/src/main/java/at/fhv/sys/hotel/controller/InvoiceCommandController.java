package at.fhv.sys.hotel.controller;

import at.fhv.sys.hotel.commands.CreateInvoiceCommand;
import at.fhv.sys.hotel.commands.InvoiceAggregate;
import at.fhv.sys.hotel.commands.shared.events.InvoiceCreated;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
public class InvoiceCommandController {

    @Inject
    InvoiceAggregate aggregate;

    @POST
    @Path("/invoice/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createInvoice(@QueryParam("bookingId") String bookingId) {
        CreateInvoiceCommand cmd = new CreateInvoiceCommand(bookingId);

        InvoiceCreated event = aggregate.handle(cmd);

        return Response
                .ok(event)
                .build();
    }
}