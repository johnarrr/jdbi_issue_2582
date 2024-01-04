package test;

import javax.measure.Unit;

public class UnitBean {
    private long id;
    private Unit<?> unit;

    public UnitBean(Unit<?> unit) {
        this.unit = unit;
    }

    public UnitBean(long id, Unit<?> unit) {
        this.id = id;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public Unit<?> getUnit() {
        return  unit;
    }
}
