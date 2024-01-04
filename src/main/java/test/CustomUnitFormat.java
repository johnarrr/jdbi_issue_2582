package test;

import javax.measure.Unit;
import javax.measure.format.MeasurementParseException;
import javax.measure.format.UnitFormat;
import java.io.IOException;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomUnitFormat implements UnitFormat {
    private static final Map<Unit<?>, String> UNIT_TO_SYMBOL = new HashMap<>();
    private static final Map<String, Unit<?>> SYMBOL_TO_UNIT = new HashMap<>();
    public static final CustomUnitFormat DEFAULT_INSTANCE = new CustomUnitFormat();

    private CustomUnitFormat() {
    }

    public static CustomUnitFormat getInstance() {
        return DEFAULT_INSTANCE;
    }

    @Override
    public Appendable format(Unit<?> unit, Appendable appendable) throws IOException {
        return appendable.append(format(unit));
    }

    @Override
    public String format(Unit<?> unit) {
        String symbol = UNIT_TO_SYMBOL.get(unit);
        if (symbol != null) {
            return symbol;
        }

        String message = String.format("'%s' (%s) cannot be formatted because it is an unknown unit",
                unit.getSymbol(), unit.getClass().getName());
        throw new IllegalArgumentException(message);
    }

    @Override
    public void label(Unit<?> unit, String label) {
        // not supported
    }

    @Override
    public boolean isLocaleSensitive() {
        return false;
    }

    @Override
    public Unit<?> parse(CharSequence charSequence, ParsePosition parsePosition) throws MeasurementParseException {
        return parse(charSequence);
    }

    @Override
    public Unit<?> parse(CharSequence charSequence) throws MeasurementParseException {
        String str = charSequence.toString().trim();

        Unit<?> unit = SYMBOL_TO_UNIT.get(str);
        if (unit != null) {
            return unit;
        }

        throw new IllegalArgumentException(String.format("No unit is known for symbol '%s'", str));
    }

    protected static <U extends Unit<?>> U add(U unit, String symbol) {
        Objects.requireNonNull(unit);
        Objects.requireNonNull(symbol);
        UNIT_TO_SYMBOL.put(unit, symbol);
        SYMBOL_TO_UNIT.put(symbol, unit);
        return unit;
    }
}
