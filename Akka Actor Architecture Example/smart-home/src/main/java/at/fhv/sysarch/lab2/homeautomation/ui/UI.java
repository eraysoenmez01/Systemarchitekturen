package at.fhv.sysarch.lab2.homeautomation.ui;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.*;
import at.fhv.sysarch.lab2.homeautomation.devices.env.EnvironmentManager;
import at.fhv.sysarch.lab2.homeautomation.devices.MediaStation;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Fridge;
import at.fhv.sysarch.lab2.homeautomation.devices.fridge.Receipt;

import java.util.Map;
import java.util.Scanner;

public class UI extends AbstractBehavior<UI.UICommand> {

    /** Marker-Interface für alle UI-internen Nachrichten */
    public interface UICommand {}

    /** Wrapper für Fridge.Inventory-Antworten */
    public static final class WrappedInventory implements UICommand {
        public final Map<String, Integer> items;
        public WrappedInventory(Map<String, Integer> items) {
            this.items = items;
        }
    }

    /** Wrapper für Receipt-Antworten */
    public static final class WrappedReceipt implements UICommand {
        public final Receipt receipt;
        public WrappedReceipt(Receipt receipt) { this.receipt = receipt; }
    }

    /** Wrapper für WeightUpdated-Antworten */
    public static final class WrappedWeight implements UICommand {
        public final double totalWeight;
        public WrappedWeight(double totalWeight) { this.totalWeight = totalWeight; }
    }

    /** Wrapper für SpaceUpdated-Antworten */
    public static final class WrappedSpace implements UICommand {
        public final int usedSlots;
        public WrappedSpace(int usedSlots) { this.usedSlots = usedSlots; }
    }

    private final ActorRef<EnvironmentManager.EnvironmentCommand> envManager;
    private final ActorRef<MediaStation.MediaCommand> mediaStation;
    private final ActorRef<Fridge.FridgeCommand> fridge;

    private final ActorRef<Fridge.Inventory> inventoryAdapter;
    private final ActorRef<Receipt> receiptAdapter;
    private final ActorRef<Fridge.WeightUpdated> weightAdapter;
    private final ActorRef<Fridge.SpaceUpdated>  spaceAdapter;

    public static Behavior<UICommand> create(
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            ActorRef<MediaStation.MediaCommand> mediaStation,
            ActorRef<Fridge.FridgeCommand> fridge
    ) {
        return Behaviors.setup(ctx -> new UI(ctx, envManager, mediaStation, fridge));
    }

    private UI(
            ActorContext<UICommand> context,
            ActorRef<EnvironmentManager.EnvironmentCommand> envManager,
            ActorRef<MediaStation.MediaCommand> mediaStation,
            ActorRef<Fridge.FridgeCommand> fridge
    ) {
        super(context);
        this.envManager   = envManager;
        this.mediaStation = mediaStation;
        this.fridge       = fridge;

        // Adapter für Receipt
        this.receiptAdapter = context.messageAdapter(
                Receipt.class,
                WrappedReceipt::new
        );

        // Adapter für Inventory
        this.inventoryAdapter = context.messageAdapter(
                Fridge.Inventory.class,
                inv -> new WrappedInventory(inv.items)
        );

        // Adapter für Gewichtsmessung
        this.weightAdapter = context.messageAdapter(
                Fridge.WeightUpdated.class,
                msg -> new WrappedWeight(msg.totalWeight)
        );

        // Adapter für Platzmessung
        this.spaceAdapter = context.messageAdapter(
                Fridge.SpaceUpdated.class,
                msg -> new WrappedSpace(msg.usedSlots)
        );

        // CLI-Thread starten
        new Thread(this::runCommandLine).start();
        getContext().getLog().info("UI started");
    }

    @Override
    public Receive<UICommand> createReceive() {
        return newReceiveBuilder()
                .onMessage(WrappedInventory.class, this::onWrappedInventory)
                .onMessage(WrappedReceipt.class,   this::onWrappedReceipt)
                .onMessage(WrappedWeight.class,    this::onWrappedWeight)
                .onMessage(WrappedSpace.class,     this::onWrappedSpace)
                .onSignal(PostStop.class, sig -> onPostStop())
                .build();
    }

    private Behavior<UICommand> onWrappedInventory(WrappedInventory msg) {
        System.out.println("=== Aktuelles Inventar ===");
        msg.items.forEach((prod, qty) ->
                System.out.printf("• %s: %d Stück%n", prod, qty)
        );
        System.out.println("==========================");
        return this;
    }

    private Behavior<UICommand> onWrappedReceipt(WrappedReceipt msg) {
        Receipt r = msg.receipt;
        System.out.printf("Bestellt: %s x%d für %.2f€%n",
                r.product(), r.amount(), r.totalPrice()
        );
        return this;
    }

    private Behavior<UICommand> onWrappedWeight(WrappedWeight msg) {
        System.out.printf("Aktuelles Gewicht: %.2f kg%n", msg.totalWeight);
        return this;
    }

    private Behavior<UICommand> onWrappedSpace(WrappedSpace msg) {
        System.out.printf("Belegte Slots: %d%n", msg.usedSlots);
        return this;
    }

    private UI onPostStop() {
        getContext().getLog().info("UI stopped");
        return this;
    }

    private void runCommandLine() {
        Scanner scanner = new Scanner(System.in);
        String reader = "";

        while (!"quit".equalsIgnoreCase(reader) && scanner.hasNextLine()) {
            reader = scanner.nextLine();
            String[] command = reader.split("\\s+");

            switch (command[0]) {
                case "t":
                    if (command.length > 1) {
                        double temp = Double.parseDouble(command[1]);
                        envManager.tell(new EnvironmentManager.TemperatureUpdate(temp));
                    }
                    break;
                case "w":
                    if (command.length > 1) {
                        envManager.tell(new EnvironmentManager.WeatherUpdate(command[1]));
                    }
                    break;
                case "internal":
                    if (command.length > 1) {
                        boolean active = "on".equalsIgnoreCase(command[1]);
                        envManager.tell(new EnvironmentManager.ToggleInternalMode(active));
                    }
                    break;
                case "external":
                    if (command.length > 1) {
                        boolean active = "on".equalsIgnoreCase(command[1]);
                        envManager.tell(new EnvironmentManager.ToggleExternalMode(active));
                    }
                    break;
                case "film":
                    if (command.length > 1) {
                        if ("start".equalsIgnoreCase(command[1])) {
                            mediaStation.tell(new MediaStation.StartFilm());
                        } else if ("stop".equalsIgnoreCase(command[1])) {
                            mediaStation.tell(new MediaStation.StopFilm());
                        }
                    }
                    break;
                case "consume":
                    if (command.length > 1) {
                        String product = command[1];
                        int amount = 1;
                        if (command.length > 2) {
                            try {
                                amount = Integer.parseInt(command[2]);
                            } catch (NumberFormatException e) {
                                System.out.println("Ungültige Zahl. Verwende Standardwert 1.");
                            }
                        }
                        fridge.tell(new Fridge.ConsumeProduct(product, amount));
                        System.out.println("Verbrauche: " + amount + " x " + product);
                    } else {
                        System.out.println("Usage: consume <product> [amount]");
                    }
                    break;
                case "order":
                    if (command.length > 2) {
                        String product = command[1];
                        try {
                            int amount = Integer.parseInt(command[2]);
                            fridge.tell(new Fridge.OrderProduct(product, amount, receiptAdapter));
                        } catch (NumberFormatException e) {
                            System.out.println("Ungültige Anzahl: " + command[2]);
                        }
                    } else {
                        System.out.println("Usage: order <produkt> <anzahl>");
                    }
                    break;
                case "inventory":
                    fridge.tell(new Fridge.GetInventory(inventoryAdapter));
                    break;
                case "weight":
                    fridge.tell(new Fridge.RequestWeight(weightAdapter));
                    break;
                case "space":
                    fridge.tell(new Fridge.RequestSpace(spaceAdapter));
                    break;
                default:
                    System.out.println("Unbekannter Befehl: " + reader);
            }
        }

        getContext().getLog().info("UI done");
    }
}