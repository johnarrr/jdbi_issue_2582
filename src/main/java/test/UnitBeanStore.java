package test;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import javax.measure.Unit;

import java.sql.*;

public class UnitBeanStore {

    private final UnitBeanDAO dao;

    public UnitBeanStore(Jdbi jdbi) {
        this.dao = jdbi.onDemand(UnitBeanDAO.class);
        jdbi.registerRowMapper(new UnitBeanMapper());
    }

    public long insert(UnitBean unitBean) {
        return dao.insert(unitBean);
    }

    public long insert(Unit<?> unit) {
        return dao.insert(unit);
    }

    public UnitBean get(long unitBeanId) {
        return dao.getById(unitBeanId);
    }

    public static class UnitBeanMapper implements RowMapper<UnitBean> {
        private static final UnitColumnMapper UNIT_COLUMN_MAPPER = new UnitColumnMapper();

        @Override
        public UnitBean map(ResultSet r, StatementContext ctx) throws SQLException {
            UnitBean unitBean = new UnitBean(
                    r.getLong("id"),
                    UNIT_COLUMN_MAPPER.map(r, "unit", ctx)
            );

            return unitBean;
        }
    }

    interface UnitBeanDAO {
        @SqlUpdate("INSERT INTO unit_bean_test (unit) VALUES (:unit)")
        @GetGeneratedKeys(value = "id")
        long insert(@BindBean UnitBean unitBean);

        @SqlUpdate("INSERT INTO unit_bean_test (unit) VALUES (:unit)")
        @GetGeneratedKeys(value = "id")
        long insert(@Bind("unit") Unit<?> unit);

        @SqlQuery("SELECT * FROM unit_bean_test WHERE id = :id")
        UnitBean getById(@Bind("id") long id);
    }
}
