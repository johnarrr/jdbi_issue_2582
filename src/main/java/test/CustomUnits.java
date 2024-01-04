package test;

import tech.units.indriya.AbstractUnit;
import tech.units.indriya.unit.Units;

import javax.measure.Unit;
import javax.measure.quantity.Mass;

public class CustomUnits {
    public static final Unit<Mass> KILOGRAM = CustomUnitFormat.add(Units.KILOGRAM, "kg");
    public static final Unit<?> ONE = CustomUnitFormat.add(AbstractUnit.ONE, "1");

    private CustomUnits() {
    }
}
