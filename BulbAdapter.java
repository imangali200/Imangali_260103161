/**
 * BulbAdapter — Object Adapter (GoF) wrapping the legacy LegacyBulb hardware.
 *
 * Implements the modern SmartDevice target interface while delegating all
 * real work to the encapsulated adaptee via composition.
 */
public class BulbAdapter implements SmartDevice {

    // Personal calibration seed: LAST SINGLE DIGIT of Student ID.
    private static final int K = 1;

    // Composition: private final reference to the adaptee (read-only vendor class).
    private final LegacyBulb bulb;

    public BulbAdapter(LegacyBulb bulb) {
        if (bulb == null) {
            throw new IllegalArgumentException("LegacyBulb must not be null");
        }
        this.bulb = bulb;
    }

    @Override
    public void turnOn() {
        // Maximum brightness on the raw 0-255 legacy scale.
        bulb.setBrightness(255);
    }

    @Override
    public void turnOff() {
        // Zero brightness = powered off.
        bulb.setBrightness(0);
    }

    @Override
    public boolean isOn() {
        // Active only when the filament carries power AND the register is non-zero.
        return bulb.hasPower() && bulb.readBrightness() > 0;
    }

    @Override
    public int getPowerPercent() {
        // Zero Rule: raw brightness 0 must return strictly 0 (no K offset).
        int rawBrightness = bulb.readBrightness();
        if (rawBrightness == 0) {
            return 0;
        }

        // Raw Percent = floor((rawBrightness * 100) / 255)
        int rawPercent = (rawBrightness * 100) / 255;

        // Calibrated Percent = Raw Percent + K
        int calibrated = rawPercent + K;

        // Capping Rule: never exceed 100.
        return Math.min(calibrated, 100);
    }
}
