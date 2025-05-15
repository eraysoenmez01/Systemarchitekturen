package at.fhv.sysarch.lab2.homeautomation.devices.sensor;


import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;
import at.fhv.sysarch.lab2.homeautomation.utils.FormatUtils;


public class TemperatureSensor extends AbstractBehavior<TemperatureSensor.TemperatureCommand> {

    public interface TemperatureCommand {}

    private final ActorRef<EnvironmentManager.EnvironmentCommand> envManager;
    private final ActorRef<AirCondition.AirConditionCommand> airCondition;

    public static Behavior<TemperatureCommand> create(
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            ActorRef<AirCondition.AirConditionCommand> airCondition
    ) {
        return Behaviors.setup(context -> new TemperatureSensor(context, envManager, airCondition));
    }

    private TemperatureSensor(
            ActorContext<TemperatureCommand> context,
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            ActorRef<AirCondition.AirConditionCommand> airCondition
    ) {
        super(context);
        this.envManager = envManager;
        this.airCondition = airCondition;

        getContext().getLog().info("TemperatureSensor started");
    }

    @Override
    public Receive<TemperatureCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(ProvideTemperature.class, this::onProvideTemperature)
                .build();
    }

    public static final class ProvideTemperature implements TemperatureCommand {
        public final double value;
        public ProvideTemperature(double value) {
            this.value = value;
        }
    }

    private Behavior<TemperatureCommand> onProvideTemperature(ProvideTemperature msg) {
        getContext().getLog().info("Received provided temperature: {}", FormatUtils.formatTemperature(msg.value));
        airCondition.tell(new AirCondition.EnrichedTemperature(msg.value, "Celsius"));
        return this;
    }
}