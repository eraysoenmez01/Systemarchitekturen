package at.fhv.sysarch.lab2.homeautomation.utils;

public class FormatUtils {
    public static String formatTemperature(double temp) {
        return String.format("%.2f °C", temp);
    }
}
