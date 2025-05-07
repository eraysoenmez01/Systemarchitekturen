package at.fhv.sysarch.lab2.homeautomation.devices.env.internal;


import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;

import java.time.Duration;
import java.util.Random;

public class WeatherEnvSimulator {

    public interface SimCommand {}

    private static final class Tick implements SimCommand {}

    public static Behavior<SimCommand> create(ActorRef<EnvironmentManager.EnvironmentCommand> envManager) {
        return Behaviors.withTimers(timers ->
                Behaviors.setup(context -> new WeatherSimBehavior(context, timers, envManager))
        );
    }

    private static class WeatherSimBehavior extends AbstractBehavior<SimCommand> {

        private final TimerScheduler<SimCommand> timers;
        private final ActorRef<EnvironmentManager.EnvironmentCommand> envManager;
        private final Random random = new Random();

        public WeatherSimBehavior(ActorContext<SimCommand> context,
                                  TimerScheduler<SimCommand> timers,
                                  ActorRef<EnvironmentManager.EnvironmentCommand> envManager) {
            super(context);
            this.timers = timers;
            this.envManager = envManager;

            timers.startTimerAtFixedRate(new Tick(), Duration.ofSeconds(10));
        }

        @Override
        public Receive<SimCommand> createReceive() {
            return newReceiveBuilder()
                    .onMessage(Tick.class, this::onTick)
                    .build();
        }

        private Behavior<SimCommand> onTick(Tick tick) {
            int state = random.nextInt(5); // Simulate 5 different weather states
            String[] weatherStates = {"sunny", "cloudy", "rain", "snow", "storm"};
            String simulatedWeather = weatherStates[state];

            getContext().getLog().info("Generated simulated weather: {}", simulatedWeather);
            envManager.tell(new EnvironmentManager.WeatherUpdate(simulatedWeather));

            return this;
        }
    }
}
