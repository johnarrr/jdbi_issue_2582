package test;

import javax.measure.Unit;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class UnitColumnMapper implements ColumnMapper<Unit<?>> {
    @Override
    public Unit<?>  map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        String value = r.getString(columnNumber);
        if (value == null) {
            return null;
        }
        return CustomUnitFormat.getInstance().parse(value);
    }

    @Override
    public Unit<?> map(ResultSet r, String columnLabel, StatementContext ctx) throws SQLException {
        String value = r.getString(columnLabel);
        if (value == null) {
            return null;
        }
        return CustomUnitFormat.getInstance().parse(value);
    }
}
