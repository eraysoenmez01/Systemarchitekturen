package at.fhv.sysarch.lab2.ordermanager.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.ordermanager.*;
import at.fhv.sysarch.lab2.ordermanager.catalog.ProductCatalog;

import java.util.concurrent.CompletableFuture;

public class OrderProcessingActor extends AbstractBehavior<OrderProcessingActor.PlaceOrder> {

    public interface Command {}

    public static final class PlaceOrder implements Command {
        public final OrderRequest request;
        public final CompletableFuture<OrderReply> replyTo;

        public PlaceOrder(OrderRequest request, CompletableFuture<OrderReply> replyTo) {
            this.request = request;
            this.replyTo = replyTo;
        }
    }

    private final ProductCatalog catalog;
    private final CompletableFuture<OrderReply> future;

    public static Behavior<PlaceOrder> create(ProductCatalog catalog, CompletableFuture<OrderReply> future) {
        return Behaviors.setup(ctx -> new OrderProcessingActor(ctx, catalog, future));
    }

    private OrderProcessingActor(ActorContext<PlaceOrder> context, ProductCatalog catalog, CompletableFuture<OrderReply> future) {
        super(context);
        this.catalog = catalog;
        this.future = future;
    }

    @Override
    public Receive<PlaceOrder> createReceive() {
        return newReceiveBuilder()
                .onMessage(PlaceOrder.class, this::onPlaceOrder)
                .build();
    }

    private Behavior<PlaceOrder> onPlaceOrder(PlaceOrder msg) {
        String name = msg.request.getName();
        int amount = msg.request.getAmount();
        double unitPrice = catalog.getUnitPrice(name);
        //berechnung
        double total = unitPrice * amount;

        OrderReply reply = OrderReply.newBuilder()
                .setName(name)
                .setAmount(amount)
                .setUnitPrice(unitPrice)
                .setTotalPrice(total)
                .build();

        future.complete(reply);
        return Behaviors.stopped();
    }
}