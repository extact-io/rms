package io.extact.rms.platform.extension;

import static org.assertj.core.api.Assertions.*;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.AddExtension;
import io.helidon.microprofile.tests.junit5.DisableDiscovery;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@ExtendWith(JulToSLF4DelegateExtension.class)
public class ApplicationInitializerCdiExtensionTest {

    @HelidonTest(resetPerTest = true) // テストメソッドごとにコンテナ起動させる
    @DisableDiscovery
    @AddExtension(ApplicationInitializerCdiExtension.class)
    // main/resources/application.yamlの本物の設定が干渉しないようにつぶしておく
    @AddConfig(key = "configuredCdi.alias.remote.class", value = "io.extact.rms.platform.extension.ApplicationInitializerCdiExtensionTest$TestConfigBean1")
    @AddConfig(key = "configuredCdi.register.alias", value = "remote")
    @Nested
    class EnabledIfRuntimeConfigTest {
        @Test
        @AddBean(TestBean1.class)
        @AddBean(TestBean2.class)
        @AddConfig(key = "test.testBean", value = "1")
        void testSelectBean1() {
            TestBean bean = CDI.current().select(TestBean.class).get();
            assertThat(bean.getSubjectClass()).isEqualTo(TestBean1.class);
        }

        @Test
        @AddBean(TestBean1.class)
        @AddBean(TestBean2.class)
        @AddConfig(key = "test.testBean", value = "2")
        void testSelectBean2() {
            TestBean bean = CDI.current().select(TestBean.class).get();
            assertThat(bean.getSubjectClass()).isEqualTo(TestBean2.class);
        }

        @Test
        @AddConfig(key = "test.testBean", value = "1")
        void testNoTargetBeanByNoneRegister() {
            Instance<TestBean> instance = CDI.current().select(TestBean.class);
            assertThat(instance.isResolvable()).isFalse(); // 対象のBeanを特定できない
            assertThat(instance.isAmbiguous()).isFalse();  // 対象のBeanが複数ある訳ではない
            assertThat(instance.isUnsatisfied()).isTrue(); // 条件を満たすBeanがない
        }

        @Test
        @AddBean(TestBean1.class)
        @AddBean(TestBean2.class)
        @AddConfig(key = "test.testBean", value = "3")
        void testNoTargetBeanByUnmatch() {
            Instance<TestBean> instance = CDI.current().select(TestBean.class);
            assertThat(instance.isResolvable()).isFalse(); // 対象のBeanを特定できない
            assertThat(instance.isAmbiguous()).isFalse();  // 対象のBeanが複数ある訳ではない
            assertThat(instance.isUnsatisfied()).isTrue(); // 条件を満たすBeanがない
        }

        @Test
        @AddBean(TestBean1.class)
        @AddBean(TestBean2.class)
        @AddBean(TestBean3.class)
        @AddConfig(key = "test.testBean", value = "1")
        void testBeanIsAmbiguous() {
            Instance<TestBean> instance = CDI.current().select(TestBean.class);
            assertThat(instance.isResolvable()).isFalse();  // 対象のBeanを特定できない
            assertThat(instance.isAmbiguous()).isTrue();    // 対象のBeanが複数ある
            assertThat(instance.isUnsatisfied()).isFalse(); // 条件を満たすBeanがある
        }

    }

    @HelidonTest(resetPerTest = true) // テストメソッドごとにコンテナ起動させる
    @DisableDiscovery
    @AddExtension(ApplicationInitializerCdiExtension.class)
    @Nested
    class RegisterConfiguredBeansTest {

        @Test
        @AddConfig(key = "configuredCdi.alias.remote.class", value = "io.extact.rms.platform.extension.ApplicationInitializerCdiExtensionTest$TestConfigBean1")
        @AddConfig(key = "configuredCdi.register.0.alias", value = "remote")
        @AddConfig(key = "configuredCdi.register.1.class", value = "io.extact.rms.platform.extension.ApplicationInitializerCdiExtensionTest$TestConfigBean2")
        @AddConfig(key = "configuredCdi.register.1.scope", value = "application")
        @AddConfig(key = "configuredCdi.register.1.id", value = "test")
        void testRegister01() {

            TestConfigBean1 bean1 = CDI.current().select(TestConfigBean1.class).get();
            assertThat(bean1).isNotNull();
            assertThat(bean1.getSubjectClass()).isEqualTo(TestConfigBean1.class);

            TestConfigBean2 bean2 = CDI.current().select(TestConfigBean2.class).get();
            assertThat(bean2).isNotNull();
            assertThat(bean2.invokePostConstruct()).isTrue();
        }
    }
    // ----------------------------------------------------- inner classes for test

    interface TestBean {
        default Class<?> getSubjectClass() {
            return this.getClass();
        }
    }

    @EnabledIfRuntimeConfig(propertyName = "test.testBean", value = "1")
    static class TestBean1 implements TestBean { // empty class
    }

    @EnabledIfRuntimeConfig(propertyName = "test.testBean", value = "2")
    static class TestBean2 implements TestBean { // empty class
    }

    @EnabledIfRuntimeConfig(propertyName = "test.testBean", value = "1") // duplicate
    static class TestBean3 implements TestBean { // empty class
    }

    static class TestConfigBean1 {
        private boolean invokePostConstruct;

        @PostConstruct
        public void init() {
            invokePostConstruct = true;
        }

        public Class<TestConfigBean1> getSubjectClass() {
            return TestConfigBean1.class;
        }

        public boolean invokePostConstruct() {
            return invokePostConstruct;
        }
    }

    static class TestConfigBean2 {
        @Inject
        TestConfigBean1 bean1;

        public boolean invokePostConstruct() {
            return bean1.invokePostConstruct();
        }
    }

}
