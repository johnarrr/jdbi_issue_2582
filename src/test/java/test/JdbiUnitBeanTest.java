package test;

import io.leangen.geantyref.GenericTypeReflector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.qualifier.QualifiedType;
import org.jdbi.v3.core.qualifier.Qualifiers;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.junit5.JdbiExtension;
import org.jdbi.v3.testing.junit5.JdbiH2Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.measure.Unit;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class JdbiUnitBeanTest {

    @RegisterExtension
    private JdbiExtension h2Extension = JdbiH2Extension.h2()
            .withPlugin(new SqlObjectPlugin());

    private Jdbi jdbi;
    private UnitBeanStore store;

    @BeforeEach
    public void beforeEach() {
        jdbi = h2Extension.getJdbi();
        jdbi.registerArgument(new UnitArgumentFactory());
        jdbi.registerColumnMapper(new UnitColumnMapper());

        jdbi.withHandle(handle -> {
            handle.execute("CREATE TABLE unit_bean_test (" +
                    "    id BIGSERIAL PRIMARY KEY," +
                    "    unit TEXT" +
                    ")");

            return null;
        });

        store = new UnitBeanStore(jdbi);
    }

    @ParameterizedTest
    @MethodSource("customUnits")
    public void testInsertWithBindBean(Unit<?> unit) {
        UnitBean unsaved = new UnitBean(unit);

        long id = store.insert(unsaved);

        UnitBean unitBean = store.get(id);
        assertThat(unitBean).isNotNull();
        assertThat(unitBean.getUnit()).isEqualTo(unit);
    }

    @ParameterizedTest
    @MethodSource("customUnits")
    public void testInsertWithBindProperty(Unit<?> unit) {
        long id = store.insert(unit);

        UnitBean unitBean = store.get(id);
        assertThat(unitBean).isNotNull();
        assertThat(unitBean.getUnit()).isEqualTo(unit);
    }

    static Stream<Arguments> customUnits() {
        return Stream.of(
                arguments(CustomUnits.KILOGRAM),
                arguments(CustomUnits.ONE)
        );
    }

    @Disabled
    @Test
    public void testGetQualifiedType() throws Exception {
        Class actualBeanType = UnitBean.class;
        Method readMethod = actualBeanType.getMethod("getUnit");
        Method writeMethod = actualBeanType.getMethod("setUnit", Unit.class);

        Parameter setterParam = Optional.ofNullable(writeMethod)
                .map(m -> m.getParameterCount() > 0 ? m.getParameters()[0] : null)
                .orElse(null);

        Optional<Method> readMethodOptional = Optional.ofNullable(readMethod);
        Type exactReturnType = GenericTypeReflector.getExactReturnType(readMethod, actualBeanType);
        Type exactParameterType = GenericTypeReflector.getExactParameterTypes(writeMethod, actualBeanType)[0];

        Type exactType = GenericTypeReflector.resolveExactType(
                readMethodOptional
                        .map(m -> GenericTypeReflector.getExactReturnType(m, actualBeanType))
                        .orElseGet(() -> exactParameterType),
                actualBeanType
        );

        AnnotatedType annotatedType = GenericTypeReflector.annotate(exactType);

        // this call generates a StackOverflowError
        AnnotatedType reduceBoundedAnnotatedType = GenericTypeReflector.reduceBounded(annotatedType);

        Type rawType = reduceBoundedAnnotatedType.getType();

        QualifiedType qualifiedType = QualifiedType.of(rawType)
                .withAnnotations(
                        new Qualifiers().findFor(readMethod, writeMethod, setterParam)
                );
    }
}
