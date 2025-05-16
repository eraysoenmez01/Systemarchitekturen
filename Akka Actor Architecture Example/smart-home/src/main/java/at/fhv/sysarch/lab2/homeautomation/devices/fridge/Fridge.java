package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.sensor.SpaceSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensor.WeightSensor;
import at.fhv.sysarch.lab2.ordermanager.OrderRequest;
import at.fhv.sysarch.lab2.ordermanager.OrderService;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Fridge extends AbstractBehavior<Fridge.FridgeCommand> {

    public interface FridgeCommand {}
    private static final int   MAX_SLOTS  = 100;
    private static final double MAX_WEIGHT = 1000.0;

    // gewicht in kg
    private static final Map<String, Double> PRODUCT_WEIGHTS = Map.of(
            "schokolade", 0.2,
            "eier",        0.07,
            "kaffee",      0.25,
            "tee",         0.05,
            "salz",        0.5,
            "nudeln",      0.3,
            "reis",        2.0,
            "öl",          1.0
    );

    //initial Kühlschrankinhalt
    private static final List<String> INITIAL_PRODUCTS = List.of(
            "schokolade", "eier", "kaffee", "tee",
            "salz", "nudeln", "reis", "öl"
    );


    private final OrderService orderService;
    private final Map<String,Integer> inventory = new HashMap<>();
    private final List<Receipt> receipts = new ArrayList<>();
    private double lastKnownWeight = 0.0;
    private int    lastKnownSlots  = 0;
    private final ActorRef<Receipt> receiptAdapter;


    public static final class ConsumeProduct implements FridgeCommand {
        public final String product;
        public final int amount;
        public ConsumeProduct(String product, int amount) {
            this.product = product;
            this.amount  = amount;
        }
    }

    public static final class OrderProduct implements FridgeCommand {
        public final String product;
        public final int amount;
        public final ActorRef<Receipt> replyTo;
        public OrderProduct(String product, int amount, ActorRef<Receipt> replyTo) {
            this.product = product;
            this.amount  = amount;
            this.replyTo = replyTo;
        }
    }

    public static final class WeightUpdated implements FridgeCommand {
        public final double totalWeight;
        public WeightUpdated(double totalWeight) { this.totalWeight = totalWeight; }
    }

    public static final class SpaceUpdated implements FridgeCommand {
        public final int usedSlots;
        public SpaceUpdated(int usedSlots) { this.usedSlots = usedSlots; }
    }

    public static final class GetInventory implements FridgeCommand {
        public final ActorRef<Inventory> replyTo;
        public GetInventory(ActorRef<Inventory> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class Inventory {
        public final Map<String,Integer> items;
        public Inventory(Map<String,Integer> items) { this.items = items; }
    }

    public static final class ReceiptResponse implements FridgeCommand {
        public final Receipt receipt;
        public ReceiptResponse(Receipt receipt) { this.receipt = receipt; }
    }

    public static final class RequestWeight implements FridgeCommand {
        public final ActorRef<WeightUpdated> replyTo;
        public RequestWeight(ActorRef<WeightUpdated> replyTo) {
            this.replyTo = replyTo;
        }
    }
    public static final class RequestSpace implements FridgeCommand {
        public final ActorRef<SpaceUpdated> replyTo;
        public RequestSpace(ActorRef<SpaceUpdated> replyTo) {
            this.replyTo = replyTo;
        }
    }


    public static Behavior<FridgeCommand> create(OrderService orderService) {
        return Behaviors.setup(ctx -> {
            Fridge fridge = new Fridge(ctx, orderService);

            ctx.spawn(WeightSensor.create(ctx.getSelf()), "weightSensor");
            ctx.spawn(SpaceSensor.create(ctx.getSelf()),  "spaceSensor");

            INITIAL_PRODUCTS.forEach(p -> fridge.inventory.put(p, 10));

            return fridge;
        });
    }

    private Fridge(ActorContext<FridgeCommand> context, OrderService orderService) {
        super(context);
        this.orderService   = orderService;
        this.receiptAdapter = context.messageAdapter(
                Receipt.class,
                ReceiptResponse::new
        );
    }

    @Override
    public Receive<FridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(OrderProduct.class,    this::onOrderProduct)
                .onMessage(ConsumeProduct.class,  this::onConsumeProduct)
                .onMessage(WeightUpdated.class,   this::onWeightUpdated)
                .onMessage(SpaceUpdated.class,    this::onSpaceUpdated)
                .onMessage(GetInventory.class,    this::onGetInventory)
                .onMessage(ReceiptResponse.class, this::onReceiptResponse)
                .onMessage(RequestWeight.class,    this::onRequestWeight)
                .onMessage(RequestSpace.class,     this::onRequestSpace)
                .build();
    }

    private Behavior<FridgeCommand> onOrderProduct(OrderProduct msg) {
        int usedSlots = inventory.values().stream().mapToInt(i -> i).sum();
        double usedWeight = lastKnownWeight;
        double productWeight = PRODUCT_WEIGHTS.getOrDefault(msg.product, 1.0);

        if (usedSlots + msg.amount > MAX_SLOTS) {
            msg.replyTo.tell(new Receipt("Not enough space", 0, 0.0, 0.0));
            return this;
        }
        if (usedWeight + msg.amount * productWeight > MAX_WEIGHT) {
            msg.replyTo.tell(new Receipt("Weight limit exceeded", 0, 0.0, 0.0));
            return this;
        }

        OrderRequest request = OrderRequest.newBuilder()
                .setName(msg.product)
                .setAmount(msg.amount)
                .build();

        orderService.placeOrder(request)
                .thenAccept(reply -> {
                    Receipt receipt = new Receipt(
                            reply.getName(), reply.getAmount(),
                            reply.getUnitPrice(), reply.getTotalPrice()
                    );
                    receipts.add(receipt);
                    inventory.merge(msg.product, msg.amount, Integer::sum);
                    msg.replyTo.tell(receipt);
                })
                .exceptionally(ex -> {
                    msg.replyTo.tell(new Receipt("Error: " + ex.getMessage(), 0, 0.0, 0.0));
                    return null;
                });

        return this;
    }

    private Behavior<FridgeCommand> onConsumeProduct(ConsumeProduct msg) {
        inventory.computeIfPresent(msg.product, (k, v) -> v - msg.amount);
        inventory.remove(msg.product, 0);
        if (!inventory.containsKey(msg.product)) {
            getContext().getSelf().tell(
                    new OrderProduct(msg.product, msg.amount, receiptAdapter)
            );
        }
        return this;
    }

    private Behavior<FridgeCommand> onWeightUpdated(WeightUpdated msg) {
        this.lastKnownWeight = msg.totalWeight;
        return this;
    }

    private Behavior<FridgeCommand> onSpaceUpdated(SpaceUpdated msg) {
        this.lastKnownSlots = msg.usedSlots;
        return this;
    }

    private Behavior<FridgeCommand> onGetInventory(GetInventory msg) {
        msg.replyTo.tell(new Inventory(new HashMap<>(inventory)));
        return this;
    }

    private Behavior<FridgeCommand> onReceiptResponse(ReceiptResponse msg) {
        receipts.add(msg.receipt);
        return this;
    }

    private Behavior<FridgeCommand> onRequestWeight(RequestWeight msg) {
        double total = inventory.entrySet().stream()
                .mapToDouble(e ->
                        e.getValue()
                                * PRODUCT_WEIGHTS.getOrDefault(e.getKey(), 1.0)
                )
                .sum();
        msg.replyTo.tell(new WeightUpdated(total));
        return this;
    }

    private Behavior<FridgeCommand> onRequestSpace(RequestSpace msg) {
        int used = inventory.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        msg.replyTo.tell(new SpaceUpdated(used));
        return this;
    }

}