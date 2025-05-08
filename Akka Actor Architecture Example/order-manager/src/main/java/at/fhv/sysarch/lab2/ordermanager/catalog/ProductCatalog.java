package at.fhv.sysarch.lab2.ordermanager.catalog;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ProductCatalog {
    private static final Map<String, Double> unitPrices = new ConcurrentHashMap<>();

    static {
        unitPrices.put("schokolade", 1.30);
        unitPrices.put("eier", 2.50);
        unitPrices.put("kaffee", 4.20);
        unitPrices.put("tee", 2.10);
        unitPrices.put("salz", 0.60);
        unitPrices.put("nudeln", 1.20);
        unitPrices.put("reis", 1.50);
        unitPrices.put("öl", 2.80);
    }

    public static double getUnitPrice(String name) {
        return unitPrices.getOrDefault(name.toLowerCase(), 1.0);
    }
}
