package at.fhv.sysarch.lab2.homeautomation.devices;


import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;

public class MediaStation extends AbstractBehavior<MediaStation.MediaCommand> {

    // --- Commands ---
    public interface MediaCommand {}

    public static final class BlindsState implements MediaCommand {
        public final boolean blindsClosed;
        public BlindsState(boolean blindsClosed) {
            this.blindsClosed = blindsClosed;
        }
    }

    public static final class StartFilm implements MediaCommand {
        public StartFilm() {}
    }

    public static final class StopFilm implements MediaCommand {
        public StopFilm() {}
    }

    public static final class SetBlinds implements MediaCommand {
        public final ActorRef<Blinds.BlindsCommand> blinds;
        public SetBlinds(ActorRef<Blinds.BlindsCommand> blinds) {
            this.blinds = blinds;
        }
    }

    // --- State ---
    private boolean filmRunning = false;
    private boolean blindsClosed = false;
    private ActorRef<Blinds.BlindsCommand> blinds = null;

    public static Behavior<MediaCommand> create() {
        return Behaviors.setup(MediaStation::new);
    }

    private MediaStation(ActorContext<MediaCommand> context) {
        super(context);
        getContext().getLog().info("MediaStation started");
    }

    @Override
    public Receive<MediaCommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(BlindsState.class, this::onBlindsState)
                .onMessage(StartFilm.class, this::onStartFilm)
                .onMessage(StopFilm.class, this::onStopFilm)
                .onMessage(SetBlinds.class, this::onSetBlinds)
                .build();
    }

    private Behavior<MediaCommand> onBlindsState(BlindsState msg) {
        this.blindsClosed = msg.blindsClosed;
        getContext().getLog().info("MediaStation received blinds state: {}", msg.blindsClosed);
        return this;
    }

    private Behavior<MediaCommand> onSetBlinds(SetBlinds msg) {
        this.blinds = msg.blinds;
        return this;
    }

    private Behavior<MediaCommand> onStartFilm(StartFilm msg) {
        if (filmRunning) {
            getContext().getLog().info("Film läuft bereits.");
        } else {
            filmRunning = true;
            getContext().getLog().info("Film gestartet.");
            if (blinds != null && !blindsClosed) {
                blinds.tell(new Blinds.OverrideBlinds(true));
            }
        }
        return this;
    }

    private Behavior<MediaCommand> onStopFilm(StopFilm msg) {
        if (filmRunning) {
            filmRunning = false;
            getContext().getLog().info("Film gestoppt.");
            if (blinds != null) {
                blinds.tell(new Blinds.OverrideBlinds(false));
            }
        }
        return this;
    }
}
