package persistence.study;

import example.entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.EntityScanner;
import persistence.sql.ddl.create.component.DdlCreateQueryBuilder;
import persistence.sql.ddl.create.component.column.ColumnComponentBuilder;
import persistence.sql.ddl.create.component.constraint.ConstraintComponentBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class QueryBuilderTest {
    private static final Logger logger = LoggerFactory.getLogger(QueryBuilderTest.class);

    @Test
    @DisplayName("DDL 쿼리 테이블 컬럼 컴포넌트 생성 테스트")
    void createDdlColumnComponentTest() {
        Class<Person> personClass = Person.class;
        for (Field field : personClass.getDeclaredFields()) {
            String columnComponent = ColumnComponentBuilder.from(field).getComponentBuilder().toString();
            logger.debug("Person.{} : {}", field.getName(),columnComponent);
        }
    }

    @Test
    @DisplayName("DDL 쿼리 constraint 컴포넌트 생성 테스트")
    void createDdlConstraintComponentTest() {
        Class<Person> personClass = Person.class;
        for (Field field : personClass.getDeclaredFields()) {
            for (ConstraintComponentBuilder constraintComponentBuilder : ConstraintComponentBuilder.from(field)) {
                logger.debug("Constraint : {}", constraintComponentBuilder.getComponentBuilder().toString());
            }
        }
    }

    @Test
    @DisplayName("DDL 쿼리 생성 테스트")
    void createDdlQuery() {
        Class<Person> personClass = Person.class;

        Field[] fields = personClass.getDeclaredFields();
        DdlCreateQueryBuilder queryBuilder = DdlCreateQueryBuilder.newInstance();
        for (Field field : fields) {
            queryBuilder.add(ColumnComponentBuilder.from(field));
            queryBuilder.add(ConstraintComponentBuilder.from(field));
        }
        String ddlQuery = queryBuilder.build(personClass.getSimpleName());

        logger.debug("DDL Query : {}", ddlQuery);
    }

    @Test
    @DisplayName("엔티티 스캐너 스캔 테스트")
    void createBasicDdl() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        EntityScanner entityScanner = new EntityScanner();
        entityScanner.scan("example.entity");

        Class<EntityScanner> entityScannerClass = EntityScanner.class;
        Field entityClassesField = entityScannerClass.getDeclaredField("entityClasses");
        entityClassesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Set<Class<?>> entityClasses = (Set<Class<?>>) entityClassesField.get(entityScanner);

        logger.debug("scanned entity classed : {}", entityClasses);
    }

    @Test
    @DisplayName("엔티티 스캔 > DDL 쿼리 생성")
    void createBasicDdlQueries() throws ClassNotFoundException {
        EntityScanner entityScanner = new EntityScanner();
        entityScanner.scan("example.entity");
        List<String> ddlQueries = entityScanner.getDdlCreateQueries();
        System.out.println(ddlQueries);
    }
}