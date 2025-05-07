package at.fhv.sysarch.lab2.homeautomation.devices.env.internal;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;
import at.fhv.sysarch.lab2.homeautomation.utils.FormatUtils;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TempEnvSimulator {

    public interface SimCommand {}

    private static final class Tick implements SimCommand {}

    public static Behavior<SimCommand> create(ActorRef<EnvironmentManager.EnvironmentCommand> envManager) {
        return Behaviors.withTimers(timers ->
                Behaviors.setup(context -> new TempSimBehavior(context, timers, envManager))
        );
    }

    private static class TempSimBehavior extends AbstractBehavior<SimCommand> {

        private final TimerScheduler<SimCommand> timers;
        private final ActorRef<EnvironmentManager.EnvironmentCommand> envManager;
        private final Random random = new Random();

        public TempSimBehavior(ActorContext<SimCommand> context,
                               TimerScheduler<SimCommand> timers,
                               ActorRef<EnvironmentManager.EnvironmentCommand> envManager) {
            super(context);
            this.timers = timers;
            this.envManager = envManager;

            // alle 10 Sekunden ein Tick-Event
            timers.startTimerAtFixedRate(new Tick(), Duration.ofSeconds(10));
        }

        @Override
        public Receive<SimCommand> createReceive() {
            return newReceiveBuilder()
                    .onMessage(Tick.class, this::onTick)
                    .build();
        }

        private Behavior<SimCommand> onTick(Tick tick) {
            double simulatedTemp = 15 + random.nextDouble() * 15; // Temperatur zwischen 15–30 °C
            getContext().getLog().info("Generated simulated temperature: {}", FormatUtils.formatTemperature(simulatedTemp));
            envManager.tell(new EnvironmentManager.TemperatureUpdate(simulatedTemp));
            return this;
        }
    }
}

