/**
 * ThermostatAdapter — Object Adapter (GoF) wrapping the legacy LegacyThermostat.
 *
 * Translates discrete String dial states ('IDLE', 'LOW', 'MEDIUM', 'MAX')
 * into the boolean / integer contract required by SmartDevice.
 */
public class ThermostatAdapter implements SmartDevice {

    // Composition: private final reference to the adaptee (read-only vendor class).
    private final LegacyThermostat thermostat;

    public ThermostatAdapter(LegacyThermostat thermostat) {
        if (thermostat == null) {
            throw new IllegalArgumentException("LegacyThermostat must not be null");
        }
        this.thermostat = thermostat;
    }

    @Override
    public void turnOn() {
        // Idempotent operation: only start heating from standby ('IDLE').
        // If a non-IDLE setting already exists, do not override it.
        if ("IDLE".equals(thermostat.checkDial())) {
            thermostat.rotateDial("LOW");
        }
    }

    @Override
    public void turnOff() {
        // Any current state -> safe standby mode.
        thermostat.rotateDial("IDLE");
    }

    @Override
    public boolean isOn() {
        // Actively heating on any operational dial state.
        String dial = thermostat.checkDial();
        return "LOW".equals(dial) || "MEDIUM".equals(dial) || "MAX".equals(dial);
    }

    @Override
    public int getPowerPercent() {
        // Discrete dial stage -> power percentage mapping.
        switch (thermostat.checkDial()) {
            case "LOW":
                return 33;   // low power stage
            case "MEDIUM":
                return 66;   // medium power stage
            case "MAX":
                return 100;  // maximum power stage
            default:
                return 0;    // 'IDLE' standby consumes 0%
        }
    }
}
