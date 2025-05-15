package at.fhv.sysarch.lab2.homeautomation.devices.fridge;

public record Receipt(String product, int amount, double price, double totalPrice) {

    @Override
    public String toString() {
        return "Produkt: " + product + ", Menge: " + amount + ", Einzelpreis: " + price + ", Gesamtpreis: " + totalPrice;
    }
}