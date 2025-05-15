package at.fhv.sysarch.lab2.homeautomation.devices.sensor;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;

public class SpaceSensor extends AbstractBehavior<SpaceSensor.SpaceCommand> {

    public interface SpaceCommand {}

    public static final class FetchSpace implements SpaceCommand {}

    public static final class ProvideSpace implements SpaceCommand {
        public final int spaceAvailable;

        public ProvideSpace(int spaceAvailable) {
            this.spaceAvailable = spaceAvailable;
        }
    }

    public static Behavior<SpaceCommand> create() {
        return Behaviors.setup(SpaceSensor::new);
    }

    private SpaceSensor(ActorContext<SpaceCommand> context) {
        super(context);
        getContext().getLog().info("SpaceSensor started");
    }

    @Override
    public Receive<SpaceCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(FetchSpace.class, this::onFetchSpace)
                .onMessage(ProvideSpace.class, this::onProvideSpace)
                .build();
    }

    private Behavior<SpaceCommand> onFetchSpace(FetchSpace cmd) {
        // Logik zur Abfrage von Platz im Fridge z. B.
        return this;
    }

    private Behavior<SpaceCommand> onProvideSpace(ProvideSpace msg) {
        getContext().getLog().info("Received provided space: {}", msg.spaceAvailable);
        return this;
    }
}