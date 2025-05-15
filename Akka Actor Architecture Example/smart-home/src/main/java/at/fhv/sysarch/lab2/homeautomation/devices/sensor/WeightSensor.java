package at.fhv.sysarch.lab2.homeautomation.devices.sensor;

import akka.actor.typed.Behavior;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.javadsl.AskPattern;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;

import java.time.Duration;

public class WeightSensor extends AbstractBehavior<WeightSensor.Command> {
    public interface Command {}

    private enum Tick implements Command { INSTANCE }

    private static final class InventoryResponse implements Command {
        final Fridge.Inventory inventory;
        InventoryResponse(Fridge.Inventory inventory) { this.inventory = inventory; }
    }

    private final ActorRef<Fridge.FridgeCommand> fridge;
    private final TimerScheduler<Command> timers;

    private WeightSensor(ActorContext<Command> ctx,
                         TimerScheduler<Command> timers,
                         ActorRef<Fridge.FridgeCommand> fridge) {
        super(ctx);
        this.timers = timers;
        this.fridge = fridge;
        timers.startTimerAtFixedRate(Tick.INSTANCE, Duration.ofSeconds(5));
    }

    public static Behavior<Command> create(ActorRef<Fridge.FridgeCommand> fridge) {
        return Behaviors.setup(ctx ->
                Behaviors.withTimers(timers ->
                        new WeightSensor(ctx, timers, fridge)
                )
        );
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals(Tick.INSTANCE, this::onTick)
                .onMessage(InventoryResponse.class, this::onInventory)
                .build();
    }

    private Behavior<Command> onTick() {
        ActorRef<Fridge.Inventory> adapter =
                getContext().messageAdapter(Fridge.Inventory.class, InventoryResponse::new);
        AskPattern.<Fridge.FridgeCommand, Fridge.Inventory>ask(
                fridge,
                replyTo -> new Fridge.GetInventory(replyTo),
                Duration.ofSeconds(3),
                getContext().getSystem().scheduler()
        ).thenAccept(adapter::tell);

        return this;
    }

    private Behavior<Command> onInventory(InventoryResponse msg) {
        int used = msg.inventory.items.values().stream().mapToInt(Integer::intValue).sum();
        fridge.tell(new Fridge.SpaceUpdated(used));
        return this;
    }
}