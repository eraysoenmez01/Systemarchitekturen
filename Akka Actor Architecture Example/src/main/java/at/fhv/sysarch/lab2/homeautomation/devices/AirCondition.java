package at.fhv.sysarch.lab2.homeautomation.devices;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import at.fhv.sysarch.lab2.homeautomation.utils.FormatUtils;

public class AirCondition extends AbstractBehavior<AirCondition.AirConditionCommand> {
    public interface AirConditionCommand {}

    public static final class PowerAirCondition implements AirConditionCommand {
        final Boolean value;

        public PowerAirCondition(Boolean value) {
            this.value = value;
        }
    }

    public static final class EnrichedTemperature implements AirConditionCommand {
        Double value;
        String unit;

        public EnrichedTemperature(Double value, String unit) {
            this.value = value;
            this.unit = unit;
        }
    }

    // ralph
    public static final class PowerMessage implements AirConditionCommand {
        boolean powerOn;

        public PowerMessage(boolean powerOn) {
            this.powerOn = powerOn;
        }
    }

    private final String identifier;

    // ralph
    private boolean powerOn = false;

    public AirCondition(ActorContext<AirConditionCommand> context, String identifier) {
        super(context);
        this.identifier = identifier;
        getContext().getLog().info("AirCondition started");
    }

    public static Behavior<AirConditionCommand> create(String identifier) {
        return Behaviors.setup(context -> new AirCondition(context, identifier));
    }

    // ralph
    private Behavior<AirConditionCommand> processPower(PowerMessage powerMessage) {
        getContext().getLog().info("Aircondition was powered on {}", powerMessage.powerOn);
        this.powerOn = powerMessage.powerOn;
        if (!powerOn) {
            return Behaviors.receive(AirConditionCommand.class)
                    .onMessage(PowerMessage.class, this::powerOn)
                    .onSignal(PostStop.class, signal -> onPostStop())
                    .build();
        }
        return Behaviors.same();
    }

    // ralph
    private Behavior<AirConditionCommand> powerOn(PowerMessage powerMessage) {
        this.powerOn = powerMessage.powerOn;
        if (powerOn) {
            return newReceiveBuilder()
                    .onMessage(EnrichedTemperature.class, this::onReadTemperature)
                    .onMessage(PowerMessage.class, this::processPower)
                    .onSignal(PostStop.class, signal -> onPostStop())
                    .build();
        }
        return Behaviors.same();
    }

    @Override
    public Receive<AirConditionCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(EnrichedTemperature.class, this::onReadTemperature)
                .onMessage(PowerMessage.class, this::processPower)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<AirConditionCommand> onReadTemperature(EnrichedTemperature r) {
        getContext().getLog().info("Aircondition reading {}", FormatUtils.formatTemperature(r.value));
        // TODO: process temperature

        if (r.value > 25) {
            getContext().getLog().info("Aircondition activated {}", FormatUtils.formatTemperature(r.value));
        } else {
            getContext().getLog().info("Aircondition deactivated {}", FormatUtils.formatTemperature(r.value));
        }
        return Behaviors.same();
    }

    private AirCondition onPostStop() {
        getContext().getLog().info("AirCondition actor {}-{} stopped", identifier);
        return this;
    }
}
