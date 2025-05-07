package at.fhv.sysarch.lab2.homeautomation;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.AirCondition;
import at.fhv.sysarch.lab2.homeautomation.devices.Blinds;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;
import at.fhv.sysarch.lab2.homeautomation.devices.env.internal.TempEnvSimulator;
import at.fhv.sysarch.lab2.homeautomation.devices.env.internal.WeatherEnvSimulator;
import at.fhv.sysarch.lab2.homeautomation.devices.sensor.TemperatureSensor;
import at.fhv.sysarch.lab2.homeautomation.devices.sensor.WeatherSensor;
import at.fhv.sysarch.lab2.homeautomation.ui.UI;

import java.util.UUID;

public class HomeAutomationController extends AbstractBehavior<Void> {

    public static Behavior<Void> create() {
        return Behaviors.setup(HomeAutomationController::new);
    }

    private HomeAutomationController(ActorContext<Void> context) {
        super(context);

        // Devices
        ActorRef<AirCondition.AirConditionCommand> airCondition =
                getContext().spawn(AirCondition.create(UUID.randomUUID().toString()), "AirCondition");

        ActorRef<MediaStation.MediaCommand> mediaStation =
                getContext().spawn(MediaStation.create(), "MediaStation");

        ActorRef<Blinds.BlindsCommand> blinds =
                getContext().spawn(Blinds.create(mediaStation), "Blinds");

        // Environment Manager
        ActorRef<EnvironmentManager.EnvironmentCommand> envManager =
                getContext().spawn(EnvironmentManager.create(), "EnvironmentManager");

        // Sensoren
        ActorRef<TemperatureSensor.TemperatureCommand> tempSensor =
                getContext().spawn(TemperatureSensor.create(envManager, airCondition), "TemperatureSensor");

        ActorRef<WeatherSensor.WeatherCommand> weatherSensor =
                getContext().spawn(WeatherSensor.create(envManager, blinds), "WeatherSensor");

        // Environment Manager informieren (Referenzen setzen)
        envManager.tell(new EnvironmentManager.InitializeSensors(tempSensor, weatherSensor));


        // UI
        ActorRef<Void> ui = getContext().spawn(
                UI.create(envManager, mediaStation), "UI");

        getContext().getLog().info("HomeAutomation Application started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder()
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private HomeAutomationController onPostStop() {
        getContext().getLog().info("HomeAutomation Application stopped");
        return this;
    }
}
