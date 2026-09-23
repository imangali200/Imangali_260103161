import java.util.List;

/**
 * Main — integration driver for the OmniHome Smart Controller lab.
 *
 * Stage 3: wraps both heterogeneous legacy appliances in Object Adapters so
 * ModernHub can manage them uniformly as SmartDevice instances (polymorphism).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("           OMNIHOME SMART CONTROLLER: SYSTEM STARTUP");
        System.out.println("============================================================");

        // Step 1: instantiate the two legacy appliances (vendor hardware).
        LegacyBulb rawBulb = new LegacyBulb();
        LegacyThermostat rawThermostat = new LegacyThermostat();
        System.out.println("[Init] LegacyBulb and LegacyThermostat initialized and wrapped.");

        // Type-safety incompatibility experiment (mandatory reflection exercise):
        // ModernHub badHub = new ModernHub(List.of(rawBulb)); // COMPILE ERROR
        /*
         * This line does not compile because ModernHub wants a List<SmartDevice>,
         * but rawBulb is a LegacyBulb and LegacyBulb does not implement SmartDevice.
         * So the types are simply not compatible and the compiler stops me here.
         * The adapter solves this without changing LegacyBulb: BulbAdapter implements
         * SmartDevice and keeps the bulb inside, forwarding every method call to it.
         * That way ModernHub gets a real SmartDevice and the legacy class stays untouched.
         */

        // Step 2: wrap each legacy instance in its respective adapter.
        SmartDevice bulbAdapter = new BulbAdapter(rawBulb);
        SmartDevice thermostatAdapter = new ThermostatAdapter(rawThermostat);

        // Step 3: combine both adapted devices into one polymorphic list.
        List<SmartDevice> deviceList = List.of(bulbAdapter, thermostatAdapter);

        // Step 4: register the fleet with the client.
        ModernHub hub = new ModernHub(deviceList);
        System.out.println("[Hub] Registering 2 adapted devices into ModernHub...");

        // Step 5: batch activation via the client.
        System.out.println("--- OPERATION: ACTIVATE ALL DEVICES ---");
        System.out.println("[Action] ModernHub.activateAll() invoked.");
        hub.activateAll();
        System.out.println("  -> BulbAdapter: Brightness set to " + rawBulb.readBrightness() + ".");
        System.out.println("  -> ThermostatAdapter: Dial set to '" + rawThermostat.checkDial() + "'.");
        System.out.println("[Status] All devices reported active: "
                + (bulbAdapter.isOn() && thermostatAdapter.isOn()));

        // Step 6: fleet-wide average power usage (reflects the personal K seed).
        double average = hub.calculateAveragePowerUsage();
        System.out.printf(java.util.Locale.US,
                "[Power] Fleet Average Power Usage: %.2f%% (Bulb: %d%%, Thermostat: %d%%)%n",
                average, bulbAdapter.getPowerPercent(), thermostatAdapter.getPowerPercent());

        // Step 7: emergency shutdown, then verify both devices reached safe states.
        System.out.println("--- OPERATION: EMERGENCY SHUTDOWN ---");
        System.out.println("[Action] ModernHub.emergencyShutdown() invoked.");
        hub.emergencyShutdown();
        System.out.println("  -> BulbAdapter: Brightness set to " + rawBulb.readBrightness() + ".");
        System.out.println("  -> ThermostatAdapter: Dial rotated to '" + rawThermostat.checkDial() + "'.");
        System.out.printf(java.util.Locale.US, "[Power] Fleet Average Power Usage: %.2f%%%n",
                hub.calculateAveragePowerUsage());
        System.out.println("============================================================");
        System.out.println("               SYSTEM SHUTDOWN COMPLETE");
        System.out.println("============================================================");
    }
}
