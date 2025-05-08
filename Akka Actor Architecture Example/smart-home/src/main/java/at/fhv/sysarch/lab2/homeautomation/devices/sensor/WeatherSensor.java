package at.fhv.sysarch.lab2.homeautomation.devices.sensor;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;
import at.fhv.sysarch.lab2.homeautomation.devices.Blinds;


public class WeatherSensor extends AbstractBehavior<WeatherSensor.WeatherCommand> {

    public interface WeatherCommand {}

    public static final class FetchWeather implements WeatherCommand {}

    private final ActorRef<EnvironmentManager.EnvironmentCommand> envManager;
    private final ActorRef<Blinds.BlindsCommand> blinds;

    public static Behavior<WeatherCommand> create(
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            ActorRef<Blinds.BlindsCommand> blinds
    ) {
        return Behaviors.setup(context -> new WeatherSensor(context, envManager, blinds));
    }

    private WeatherSensor(
            ActorContext<WeatherCommand> context,
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            ActorRef<Blinds.BlindsCommand> blinds
    ) {
        super(context);
        this.envManager = envManager;
        this.blinds = blinds;

        getContext().getLog().info("WeatherSensor started");
    }

    @Override
    public Receive<WeatherCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(FetchWeather.class, this::onFetchWeather)
                .onMessage(ProvideWeather.class, this::onProvideWeather)
                .build();
    }

    private Behavior<WeatherCommand> onFetchWeather(FetchWeather cmd) {
        envManager.tell(new EnvironmentManager.RequestWeather(getContext().getSelf()));
        return this;
    }

    public static final class ProvideWeather implements WeatherCommand {
        public final String condition;
        public ProvideWeather(String condition) {
            this.condition = condition;
        }
    }

    private Behavior<WeatherCommand> onProvideWeather(ProvideWeather msg) {
        getContext().getLog().info("Received provided weather: {}", msg.condition);
        blinds.tell(new Blinds.WeatherInfo(msg.condition));
        return this;
    }
}