package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.ordermanager.OrderReply;
import at.fhv.sysarch.lab2.ordermanager.OrderRequest;
import at.fhv.sysarch.lab2.ordermanager.OrderService;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {

    public interface FridgeCommand {}

    private final OrderService orderService;

    public static final class ConsumeProduct implements FridgeCommand {
        public final String product;
        public final int amount;

        public ConsumeProduct(String product, int amount) {
            this.product = product;
            this.amount = amount;
        }
    }

    public static final class OrderProduct implements FridgeCommand {
        public final String product;
        public final int amount;
        public final ActorRef<Receipt> replyTo;

        public OrderProduct(String product, int amount, ActorRef<Receipt> replyTo) {
            this.product = product;
            this.amount = amount;
            this.replyTo = replyTo;
        }
    }

    public static Behavior<FridgeCommand> create(OrderService orderService) {
        return Behaviors.setup(ctx -> new Fridge(ctx, orderService));
    }

    private Fridge(ActorContext<FridgeCommand> context, OrderService orderService) {
        super(context);
        this.orderService = orderService;
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(OrderProduct.class, this::onOrderProduct)
                .onMessage(ConsumeProduct.class, this::onConsumeProduct)
                .build();
    }

    private Behavior<FridgeCommand> onOrderProduct(OrderProduct msg) {
        OrderRequest request = OrderRequest.newBuilder()
                .setName(msg.product)
                .setAmount(msg.amount)
                .build();

        orderService.placeOrder(request)
                .thenAccept(reply -> {
                    Receipt receipt = new Receipt(
                            reply.getName(),
                            reply.getAmount(),
                            reply.getUnitPrice(),
                            reply.getTotalPrice()
                    );
                    msg.replyTo.tell(receipt);
                })
                .exceptionally(ex -> {
                    msg.replyTo.tell(new Receipt("Fehler: " + ex.getMessage(), 0, 0.0, 0.0));
                    return null;
                });

        return this;
    }

    private Behavior<FridgeCommand> onConsumeProduct(ConsumeProduct msg) {
        return this;
    }
}