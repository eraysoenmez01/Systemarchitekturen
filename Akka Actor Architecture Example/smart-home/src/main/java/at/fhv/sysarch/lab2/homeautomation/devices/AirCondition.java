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

    public static final class EnrichedTemperature implements AirConditionCommand {
        Double value;
        String unit;

        public EnrichedTemperature(Double value, String unit) {
            this.value = value;
            this.unit = unit;
        }
    }

    private final String identifier;

    public AirCondition(ActorContext<AirConditionCommand> context, String identifier) {
        super(context);
        this.identifier = identifier;
        getContext().getLog().info("AirCondition started");
    }

    public static Behavior<AirConditionCommand> create(String identifier) {
        return Behaviors.setup(context -> new AirCondition(context, identifier));
    }

    @Override
    public Receive<AirConditionCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(EnrichedTemperature.class, this::onReadTemperature)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<AirConditionCommand> onReadTemperature(EnrichedTemperature r) {
        getContext().getLog().info("Aircondition reading {}", FormatUtils.formatTemperature(r.value));

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
