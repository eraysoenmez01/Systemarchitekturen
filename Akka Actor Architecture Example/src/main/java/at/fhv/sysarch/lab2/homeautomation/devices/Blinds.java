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

    public static class OverrideBlinds implements BlindsCommand {
        public final boolean forceClosed;

        public OverrideBlinds(boolean forceClosed) {
            this.forceClosed = forceClosed;
        }
    }

    // --- State ---
    private boolean isClosed = false;
    private boolean forcedByFilm = false;
    private ActorRef<MediaStation.MediaCommand> mediaStation = null;
    private String lastKnownWeather = "clear";

    public static Behavior<BlindsCommand> create() {
        return Behaviors.setup(context -> new Blinds(context));
    }

    public Blinds(ActorContext<BlindsCommand> context) {
        super(context);
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

        if (forcedByFilm) {
            getContext().getLog().info("Film gestartet – Blinds {}.", isClosed ? "bereits geschlossen" : "werden geschlossen");
            updateBlindsState(true); // immer schließen
        } else {
            getContext().getLog().info("Film gestoppt – Blinds werden neu bewertet (film override deaktiviert).");
            // direkt neu bewerten mit letztem Wetter
            return onWeatherInfo(new WeatherInfo(lastKnownWeather));
        }

        return this;
    }

    private void reEvaluateBlinds() {
        if (forcedByFilm) {
            updateBlindsState(true); // Immer schließen, wenn Film läuft
        } else {
            getContext().getLog().info("Blinds status re-evaluated, last known weather: {}", lastKnownWeather);
            boolean shouldClose = lastKnownWeather.equalsIgnoreCase("sun");
            updateBlindsState(shouldClose);
        }
    }

    private Behavior<BlindsCommand> onWeatherInfo(WeatherInfo msg) {
        lastKnownWeather = msg.weather; // wichtig!
        if (!forcedByFilm) {
            boolean shouldClose = msg.weather.equalsIgnoreCase("sun");
            updateBlindsState(shouldClose);
        } else {
            getContext().getLog().info("Film läuft – Wetter ignoriert.");
        }
        return this;
    }

    private void updateBlindsState(boolean shouldClose) {
        if (shouldClose != isClosed) {
            isClosed = shouldClose;
            getContext().getLog().info("Blinds {}", isClosed ? "closed" : "opened");
        } else {
            getContext().getLog().info("Blinds remain {}", isClosed ? "closed" : "open");
        }

        // Immer Rückmeldung an MediaStation schicken:
        if (mediaStation != null) {
            mediaStation.tell(new MediaStation.BlindsState(isClosed));
        }
    }
}
