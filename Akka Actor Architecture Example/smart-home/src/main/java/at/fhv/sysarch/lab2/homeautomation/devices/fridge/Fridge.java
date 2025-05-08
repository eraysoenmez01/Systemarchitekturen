package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

import akka.actor.typed.Behavior;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Behaviors;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Fridge {

    public interface FridgeCommand {}

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

    public static final class ConsumeProduct implements FridgeCommand {
        public final String product;

        public ConsumeProduct(String product) {
            this.product = product;
        }
    }

    public static final class GetInventory implements FridgeCommand {
        public final ActorRef<String> replyTo;

        public GetInventory(ActorRef<String> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static final class GetOrderHistory implements FridgeCommand {
        public final ActorRef<String> replyTo;

        public GetOrderHistory(ActorRef<String> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static Behavior<FridgeCommand> create() {
        return Behaviors.setup(ctx -> {

            Map<String, Integer> inventory = new HashMap<>();
            List<String> orderHistory = new ArrayList<>();
            AtomicReference<Double> currentWeight = new AtomicReference<>(0.0);
            final double maxWeight = 50.0;
            final int maxProducts = 20;

            return Behaviors.receive(FridgeCommand.class)
                    .onMessage(OrderProduct.class, msg -> {
                        int totalItems = inventory.values().stream().mapToInt(i -> i).sum();
                        if (totalItems + msg.amount > maxProducts) {
                            msg.replyTo.tell(new Receipt("Nicht genug Platz", 0, 0.0));
                            return Behaviors.same();
                        }

                        // Simulierter Preis statt gRPC-Antwort
                        double unitPrice = 1.5;
                        double totalPrice = unitPrice * msg.amount;

                        inventory.merge(msg.product, msg.amount, Integer::sum);
                        currentWeight.set(currentWeight.get() + totalPrice);
                        orderHistory.add("Bestellt: " + msg.product + " x" + msg.amount);
                        msg.replyTo.tell(new Receipt(msg.product, msg.amount, totalPrice));

                        return Behaviors.same();
                    })

                    .onMessage(ConsumeProduct.class, msg -> {
                        inventory.computeIfPresent(msg.product, (p, v) -> v > 1 ? v - 1 : null);
                        return Behaviors.same();
                    })

                    .onMessage(GetInventory.class, msg -> {
                        msg.replyTo.tell(inventory.toString());
                        return Behaviors.same();
                    })

                    .onMessage(GetOrderHistory.class, msg -> {
                        msg.replyTo.tell(String.join("\n", orderHistory));
                        return Behaviors.same();
                    })

                    .build();
        });
    }
}