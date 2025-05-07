package at.fhv.sysarch.lab2.homeautomation.devices;


import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;

public class Blinds extends AbstractBehavior<Blinds.BlindsCommand> {

    // --- Commands ---
    public interface BlindsCommand {}

    public static final class WeatherInfo implements BlindsCommand {
        public final String weather;
        public WeatherInfo(String weather) {
            this.weather = weather;
        }
    }

    public static final class WeatherUpdate implements BlindsCommand {
        public final String condition;

        public WeatherUpdate(String condition) {
            this.condition = condition;
        }
    }

    public static final class SetMediaStation implements BlindsCommand {
        public final ActorRef<MediaStation.MediaCommand> mediaStation;
        public SetMediaStation(ActorRef<MediaStation.MediaCommand> mediaStation) {
            this.mediaStation = mediaStation;
        }
    }

    public static final class OverrideBlinds implements BlindsCommand {
        public final boolean forceClosed;
        public OverrideBlinds(boolean forceClosed) {
            this.forceClosed = forceClosed;
        }
    }

    // --- State ---
    private boolean isClosed = false;
    private boolean forcedByFilm = false;
    private ActorRef<MediaStation.MediaCommand> mediaStation = null;

    public static Behavior<BlindsCommand> create(ActorRef<MediaStation.MediaCommand> mediaStation) {
        return Behaviors.setup(context -> new Blinds(context, mediaStation));
    }

    public Blinds(ActorContext<BlindsCommand> context, ActorRef<MediaStation.MediaCommand> mediaStation) {
        super(context);
        this.mediaStation = mediaStation;
    }

    @Override
    public Receive<BlindsCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(WeatherInfo.class, this::onWeatherInfo)
                .onMessage(SetMediaStation.class, this::onSetMediaStation)
                .onMessage(OverrideBlinds.class, this::onOverrideBlinds)
                .build();
    }

    private Behavior<BlindsCommand> onSetMediaStation(SetMediaStation msg) {
        this.mediaStation = msg.mediaStation;
        return this;
    }

    private Behavior<BlindsCommand> onOverrideBlinds(OverrideBlinds msg) {
        forcedByFilm = msg.forceClosed;
        updateBlindsState(forcedByFilm ? true : null); // force close or re-evaluate
        return this;
    }

    private Behavior<BlindsCommand> onWeatherInfo(WeatherInfo msg) {
        if (!forcedByFilm) {
            boolean shouldClose = msg.weather.equalsIgnoreCase("sun");
            updateBlindsState(shouldClose);
        } else {
            getContext().getLog().info("Film läuft – Wetter ignoriert.");
        }
        return this;
    }

    private void updateBlindsState(Boolean shouldClose) {
        if (shouldClose == null) {
            getContext().getLog().info("Blinds status re-evaluated, film override: {}", forcedByFilm);
            return;
        }

        if (shouldClose != isClosed) {
            isClosed = shouldClose;
            getContext().getLog().info("Blinds {}", isClosed ? "closed" : "opened");
            if (mediaStation != null) {
                mediaStation.tell(new MediaStation.BlindsState(isClosed));
            }
        }
    }
}
