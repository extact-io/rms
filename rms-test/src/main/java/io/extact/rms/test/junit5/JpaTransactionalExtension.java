package io.extact.rms.test.junit5;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * Acquire and release EntityManager in pre-processing and post-processing for each test.
 * If a transaction is required, @TransactionalTest can be annotated so that the test method
 * can multiply the transaction that is the transaction boundary.
 * <pre>
 * @ExtendWith(JpaTransactionalExtension.class)
 * class RentalItemJpaRepositoryTest {
 *
 *   @BeforeEach
 *   void setup(EntityManager em) {
 *      ....
 *   }
 *   @TransactionalTest(shouldCommit = false)
 *   void addTest() {
 *      ....
 *   }
 * </pre>
 */
public class JpaTransactionalExtension implements
        BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback,
        BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private static final String CURRENT_ENTITY_FACTORY = "CURRENT_ENTITY_MANAGER_FACTORY";
    private static final String CURRENT_ENTITY_MANAGER = "CURRENT_ENTITY_MANAGER";
    private static final String CURRENT_ENTITY_TRANSACTION = "CURRENT_ENTITY_TRANSACTION";


    // ----------------------------------------------------- before methods

    @Override
    public void beforeAll(ExtensionContext context) {
        var unitName = geTragetUnitName();
        var properties = getPersistenceProperties();
        var emf = Persistence.createEntityManagerFactory(unitName, properties);
        getEntityManagerFactoryStore(context).put(CURRENT_ENTITY_FACTORY, new CloseableWrapper(emf));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        EntityManagerFactory emf = getEntityManagerFactoryStore(context).get(CURRENT_ENTITY_FACTORY, CloseableWrapper.class).unwrap();
        getEntityManagerStore(context).put(CURRENT_ENTITY_MANAGER, new CloseableWrapper(emf.createEntityManager()));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType() == EntityManager.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getEntityManagerStore(extensionContext).get(CURRENT_ENTITY_MANAGER, CloseableWrapper.class).unwrap();
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        if (!AnnotationSupport.isAnnotated(context.getTestClass(), TransactionalForTest.class)
                && !AnnotationSupport.isAnnotated(context.getTestMethod(), TransactionalForTest.class)) {
            return;
        }

        EntityManager em = getEntityManagerStore(context).get(CURRENT_ENTITY_MANAGER, CloseableWrapper.class).unwrap();
        if (em == null) {
            throw new IllegalStateException("EntityManage is unset.");
        }

        var tx = em.getTransaction();
        tx.begin();
        getEntityManagerStore(context).put(CURRENT_ENTITY_TRANSACTION, tx);
    }


    // ----------------------------------------------------- after methods

    @Override
    public void afterTestExecution(ExtensionContext context) {
        // Give priority to Method Annotation
        TransactionalForTest transactionalTest = AnnotationSupport
                    .findAnnotation(context.getRequiredTestMethod(), TransactionalForTest.class)
                .orElse(AnnotationSupport.findAnnotation(context.getRequiredTestClass(), TransactionalForTest.class).orElse(null));
        if (transactionalTest == null) {
            return;
        }

        var tx = getEntityManagerStore(context).remove(CURRENT_ENTITY_TRANSACTION, EntityTransaction.class);
        if (transactionalTest.shouldCommit()) {
            tx.commit();
        } else {
            tx.rollback();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getEntityManagerStore(context).remove(CURRENT_ENTITY_MANAGER, CloseableWrapper.class).close();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        getEntityManagerFactoryStore(context).remove(CURRENT_ENTITY_FACTORY, CloseableWrapper.class).close();
    }


    // ----------------------------------------------------- private methods

    private String geTragetUnitName() {
        return ConfigProvider.getConfig().getValue("test.db.connection.unitname", String.class);
    }

    private Map<String, String> getPersistenceProperties() {
        var config = ConfigProvider.getConfig();
        var keys = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(key -> key.startsWith("test.db.connection.properties."))
                .toList();
        return keys.stream().collect(Collectors.toMap(
                    key -> StringUtils.remove(key, "test.db.connection.properties."), // prop-key
                    key -> config.getOptionalValue(key, String.class).orElse("") // prop-value
                ));
    }

    private Store getEntityManagerFactoryStore(ExtensionContext context) {
        return context.getStore(Namespace.create(context.getRequiredTestClass()));
    }

    private Store getEntityManagerStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass(), context.getRequiredTestMethod()));
    }


    // ----------------------------------------------------- private methods

    static class CloseableWrapper implements Store.CloseableResource {
        private Object org;

        public CloseableWrapper(EntityManagerFactory closeable) {
            this.org = closeable;
        }
        public CloseableWrapper(EntityManager closeable) {
            this.org = closeable;
        }

        @Override
        public void close() {
            if (org instanceof EntityManagerFactory) {
                var closeable = (EntityManagerFactory) org;
                if (closeable.isOpen()) {
                    closeable.close();
                }
            }
            if (org instanceof EntityManager) {
                var closeable = (EntityManager) org;
                if (closeable.isOpen()) {
                    closeable.close();
                }
            }
        }
        @SuppressWarnings("unchecked")
        public <T> T unwrap() {
            return (T) org;
        }
    }
}
