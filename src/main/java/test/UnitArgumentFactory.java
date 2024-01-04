package test;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import javax.measure.Unit;
import java.sql.Types;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.statement.StatementContext;

public class UnitArgumentFactory implements ArgumentFactory {
    @Override
    public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {

        if (!(value instanceof Unit)) {
            return Optional.empty();
        }

        class UnitArgument implements Argument {

            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
                if (value != null) {
                    String unitString = CustomUnitFormat.getInstance().format((Unit)value);
                    statement.setString(position, unitString);
                } else {
                    statement.setNull(position, Types.VARCHAR);
                }
            }
        }
        return Optional.of(new UnitArgument());
    }
}
