package at.fhv.sys.hotel.domain.valueObject;

import java.util.UUID;

public class InvoiceId {
    private UUID invoiceId;
    public InvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }
    public UUID getInvoiceId() {
        return invoiceId;
    }
    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }
}
