package at.fhv.sysarch.lab2.homeautomation.devices.env.external;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import akka.stream.Materializer;
import akka.stream.SystemMaterializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.KillSwitches;
import akka.stream.alpakka.mqtt.*;
import akka.stream.alpakka.mqtt.javadsl.MqttSource;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;
import com.typesafe.config.Config;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.concurrent.CompletionStage;

public class MqttBridge extends AbstractBehavior<MqttBridge.BridgeCommand> {

    public interface BridgeCommand {}
    public static class Disconnect implements BridgeCommand {}

    private final UniqueKillSwitch temperatureKillSwitch;
    private final UniqueKillSwitch weatherKillSwitch;

    public static Behavior<BridgeCommand> create(
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            Config config
    )  {
        return Behaviors.setup(ctx -> {
            Materializer mat = SystemMaterializer.get(ctx.getSystem()).materializer();

            MqttConnectionSettings settings = MqttConnectionSettings.create(
                    config.getString("mqtt.broker-url"),
                    config.getString("mqtt.client-id"),
                    new MemoryPersistence()
            );

            Source<MqttMessage, CompletionStage<Done>> tempSource = MqttSource.atMostOnce(
                    settings.withClientId("temp-subscriber"),
                    MqttSubscriptions.create(config.getString("mqtt.topics.temperature"), MqttQoS.atLeastOnce()),
                    8
            );

            UniqueKillSwitch tempKill = tempSource
                    .viaMat(KillSwitches.single(), Keep.right())
                    .map(msg -> {
                        String json = msg.payload().utf8String();
                        try {
                            JSONObject obj = new JSONObject(json);
                            return new EnvironmentManager.TemperatureUpdate(obj.getDouble("temperature"));
                        } catch (Exception e) {
                            ctx.getLog().error("Fehler beim Parsen der Temperatur-Nachricht: {}", json, e);
                            return null;
                        }
                    })
                    .filter(msg -> msg != null)
                    .toMat(Sink.foreach(envManager::tell), Keep.left())
                    .run(mat);

            Source<MqttMessage, CompletionStage<Done>> weatherSource = MqttSource.atMostOnce(
                    settings.withClientId("weather-subscriber"),
                    MqttSubscriptions.create(config.getString("mqtt.topics.weather"), MqttQoS.atLeastOnce()),
                    8
            );

            UniqueKillSwitch weatherKill = weatherSource
                    .viaMat(KillSwitches.single(), Keep.right())
                    .map(msg -> {
                        String json = msg.payload().utf8String();
                        try {
                            JSONObject obj = new JSONObject(json);
                            return new EnvironmentManager.WeatherUpdate(obj.getString("condition").toLowerCase());
                        } catch (Exception e) {
                            ctx.getLog().error("Fehler beim Parsen der Wetter-Nachricht: {}", json, e);
                            return null;
                        }
                    })
                    .filter(msg -> msg != null)
                    .toMat(Sink.foreach(envManager::tell), Keep.left())
                    .run(mat);

            ctx.getLog().info("MQTT-Bridge gestartet und verbunden mit Topics.");

            return new MqttBridge(ctx, tempKill, weatherKill);
        });
    }

    private MqttBridge(
            ActorContext<BridgeCommand> context,
            UniqueKillSwitch tempKill,
            UniqueKillSwitch weatherKill
    ) {
        super(context);
        this.temperatureKillSwitch = tempKill;
        this.weatherKillSwitch = weatherKill;
    }

    @Override
    public Receive<BridgeCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(Disconnect.class, msg -> onDisconnect())
                .onSignal(PostStop.class, signal -> onStop())
                .build();
    }

    private Behavior<BridgeCommand> onDisconnect() {
        getContext().getLog().info("MQTT-Bridge trennt Streams..");
        temperatureKillSwitch.shutdown();
        weatherKillSwitch.shutdown();
        return Behaviors.stopped();
    }

    private MqttBridge onStop() {
        getContext().getLog().info("MQTT-Bridge PostStop – Streams werden beendet");
        temperatureKillSwitch.shutdown();
        weatherKillSwitch.shutdown();
        return this;
    }
}