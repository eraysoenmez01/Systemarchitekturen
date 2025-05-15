package at.fhv.sysarch.lab2.homeautomation.devices.env;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.env.external.MqttBridge;
import at.fhv.sysarch.lab2.homeautomation.devices.env.internal.TempEnvSimulator;
import at.fhv.sysarch.lab2.homeautomation.devices.env.internal.WeatherEnvSimulator;
import at.fhv.sysarch.lab2.homeautomation.devices.sensor.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensor.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.utils.FormatUtils;

public class EnvironmentManager extends AbstractBehavior<EnvironmentManager.EnvironmentCommand> {

    public interface EnvironmentCommand {}

    private ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
    private ActorRef<WeatherSensor.WeatherCommand> weatherSensor;
    private ActorRef<TempEnvSimulator.SimCommand> tempSim;
    private ActorRef<WeatherEnvSimulator.SimCommand> weatherSim;
    private ActorRef<MqttBridge.BridgeCommand> mqttBridge;

    private double currentTemperature = 20.0;
    private String currentWeather = "cloudy";

    private boolean internalMode = false;
    private boolean externalMode = false;

    public static class InitializeSensors implements EnvironmentCommand {
        public final ActorRef<TemperatureSensor.TemperatureCommand> tempSensor;
        public final ActorRef<WeatherSensor.WeatherCommand> weatherSensor;

        public InitializeSensors(ActorRef<TemperatureSensor.TemperatureCommand> tempSensor,
                                 ActorRef<WeatherSensor.WeatherCommand> weatherSensor) {
            this.tempSensor = tempSensor;
            this.weatherSensor = weatherSensor;
        }
    }

    public static class TemperatureUpdate implements EnvironmentCommand {
        public final double value;
        public TemperatureUpdate(double value) {
            this.value = value;
        }
    }

    public static class WeatherUpdate implements EnvironmentCommand {
        public final String condition;
        public WeatherUpdate(String condition) {
            this.condition = condition;
        }
    }


    public static class ToggleInternalMode implements EnvironmentCommand {
        public final boolean enabled;
        public ToggleInternalMode(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class ToggleExternalMode implements EnvironmentCommand {
        public final boolean enabled;
        public ToggleExternalMode(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static Behavior<EnvironmentCommand> create() {
        return Behaviors.setup(EnvironmentManager::new);
    }

    private EnvironmentManager(ActorContext<EnvironmentCommand> context) {
        super(context);
        context.getLog().info("EnvironmentManager started");
    }

    @Override
    public Receive<EnvironmentCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(InitializeSensors.class, this::onInit)
                .onMessage(TemperatureUpdate.class, this::onTempUpdate)
                .onMessage(WeatherUpdate.class, this::onWeatherUpdate)
                .onMessage(ToggleInternalMode.class, this::onToggleInternal)
                .onMessage(ToggleExternalMode.class, this::onToggleExternal)
                .build();
    }

    private Behavior<EnvironmentCommand> onInit(InitializeSensors msg) {
        this.tempSensor = msg.tempSensor;
        this.weatherSensor = msg.weatherSensor;
        getContext().getLog().info("Sensors registered");
        return this;
    }

    private Behavior<EnvironmentCommand> onTempUpdate(TemperatureUpdate msg) {
        currentTemperature = msg.value;
        getContext().getLog().info("Temperature updated to {}", FormatUtils.formatTemperature(msg.value));
        if (tempSensor != null) {
            tempSensor.tell(new TemperatureSensor.ProvideTemperature(currentTemperature));
        }
        return this;
    }

    private Behavior<EnvironmentCommand> onWeatherUpdate(WeatherUpdate msg) {
        currentWeather = msg.condition;
        getContext().getLog().info("Weather updated to {}", msg.condition);
        if (weatherSensor != null) {
            weatherSensor.tell(new WeatherSensor.ProvideWeather(currentWeather));
        }
        return this;
    }

    private Behavior<EnvironmentCommand> onToggleInternal(ToggleInternalMode msg) {
        internalMode = msg.enabled;
        if (internalMode) {
            externalMode = false;
            getContext().getLog().info("Internal mode enabled");
            tempSim = getContext().spawn(TempEnvSimulator.create(getContext().getSelf()), "TempEnvSimulator");
            weatherSim = getContext().spawn(WeatherEnvSimulator.create(getContext().getSelf()), "WeatherEnvSimulator");
        } else {
            getContext().getLog().info("Internal mode disabled");
            if (tempSim != null) getContext().stop(tempSim);
            if (weatherSim != null) getContext().stop(weatherSim);
            tempSim = null;
            weatherSim = null;
        }
        return this;
    }

    private Behavior<EnvironmentCommand> onToggleExternal(ToggleExternalMode msg) {
        externalMode = msg.enabled;
        if (externalMode) {
            internalMode = false;
            getContext().getLog().info("External mode enabled");
            if (tempSim != null) getContext().stop(tempSim);
            if (weatherSim != null) getContext().stop(weatherSim);
            tempSim = null;
            weatherSim = null;
            if (mqttBridge == null) {
                mqttBridge = getContext().spawn(
                        MqttBridge.create(getContext().getSelf(), getContext().getSystem().settings().config()),
                        "MqttBridge"
                );
            }
        } else {
            getContext().getLog().info("External mode disabled");
            if (mqttBridge != null) {
                mqttBridge.tell(new MqttBridge.Disconnect());
                mqttBridge = null;
            }
        }
        return this;
    }
}