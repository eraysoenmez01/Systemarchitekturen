package at.fhv.sysarch.lab2.homeautomation.devices.sensor;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;

public class WeightSensor extends AbstractBehavior<WeightSensor.WeightCommand> {

    public interface WeightCommand {}

    public static final class FetchWeight implements WeightCommand {}

    public static final class ProvideWeight implements WeightCommand {
        public final double weight;

        public ProvideWeight(double weight) {
            this.weight = weight;
        }
    }

    public static Behavior<WeightCommand> create() {
        return Behaviors.setup(WeightSensor::new);
    }

    private WeightSensor(ActorContext<WeightCommand> context) {
        super(context);
        getContext().getLog().info("WeightSensor started");
    }

    @Override
    public Receive<WeightCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(FetchWeight.class, this::onFetchWeight)
                .onMessage(ProvideWeight.class, this::onProvideWeight)
                .build();
    }

    private Behavior<WeightCommand> onFetchWeight(FetchWeight cmd) {
        // Hier könnte z. B. ein Fridge gefragt werden
        return this;
    }

    private Behavior<WeightCommand> onProvideWeight(ProvideWeight msg) {
        getContext().getLog().info("Received provided weight: {}", msg.weight);
        return this;
    }
}