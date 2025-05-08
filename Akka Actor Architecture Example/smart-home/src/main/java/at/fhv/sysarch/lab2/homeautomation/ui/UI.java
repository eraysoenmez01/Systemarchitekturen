package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;

import java.util.Scanner;

public class UI extends AbstractBehavior<Void> {

    private final ActorRef<EnvironmentManager.EnvironmentCommand> envManager;
    private final ActorRef<MediaStation.MediaCommand> mediaStation;
    private final ActorRef<Fridge.FridgeCommand> fridge;

    public static Behavior<Void> create(
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            ActorRef<MediaStation.MediaCommand> mediaStation,
            ActorRef<Fridge.FridgeCommand> fridge
    ) {
        return Behaviors.setup(context -> new UI(context, envManager, mediaStation, fridge));
    }

    private UI(ActorContext<Void> context,
               ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
               ActorRef<MediaStation.MediaCommand> mediaStation,
               ActorRef<Fridge.FridgeCommand> fridge) {
        super(context);
        this.envManager = envManager;
        this.mediaStation = mediaStation;
        this.fridge = fridge;

        new Thread(this::runCommandLine).start();

        getContext().getLog().info("UI started");
    }

    @Override
    public Receive<Void> createReceive() {
        return newReceiveBuilder().onSignal(PostStop.class, signal -> onPostStop()).build();
    }

    private UI onPostStop() {
        getContext().getLog().info("UI stopped");
        return this;
    }

    private void runCommandLine() {
        Scanner scanner = new Scanner(System.in);
        String reader = "";

        while (!reader.equalsIgnoreCase("quit") && scanner.hasNextLine()) {
            reader = scanner.nextLine();
            String[] command = reader.split(" ");

            switch (command[0]) {
                case "t":
                    if (command.length > 1) {
                        double temp = Double.parseDouble(command[1]);
                        envManager.tell(new EnvironmentManager.TemperatureUpdate(temp));
                    }
                    break;
                case "w":
                    if (command.length > 1) {
                        String weather = command[1];
                        envManager.tell(new EnvironmentManager.WeatherUpdate(weather));
                    }
                    break;
                case "internal":
                    if (command.length > 1) {
                        boolean active = command[1].equalsIgnoreCase("on");
                        envManager.tell(new EnvironmentManager.ToggleInternalMode(active));
                    }
                    break;
                case "external":
                    if (command.length > 1) {
                        boolean active = command[1].equalsIgnoreCase("on");
                        envManager.tell(new EnvironmentManager.ToggleExternalMode(active));
                    }
                    break;
                case "film":
                    if (command.length > 1) {
                        if (command[1].equalsIgnoreCase("start")) {
                            mediaStation.tell(new MediaStation.StartFilm());
                        } else if (command[1].equalsIgnoreCase("stop")) {
                            mediaStation.tell(new MediaStation.StopFilm());
                        }
                    }
                    break;
                case "consume":
                    //befehl (consume apfel), nimm 1 apfel weg
                    if (command.length > 1) {
                        String product = command[1];
                        fridge.tell(new Fridge.ConsumeProduct(product));
                        System.out.println("Verbrauche: " + product);
                    } else {
                        System.out.println("Usage: consume <product>");
                    }
                    break;
                default:
                    System.out.println("Unbekannter Befehl: " + reader);
            }
        }

        getContext().getLog().info("UI done");
    }
}
