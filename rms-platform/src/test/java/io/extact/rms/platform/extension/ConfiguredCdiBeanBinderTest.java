package io.extact.rms.platform.extension;


import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.extact.rms.test.junit5.JulToSLF4DelegateExtension;
import io.helidon.microprofile.tests.junit5.AddConfig;
import io.helidon.microprofile.tests.junit5.DisableDiscovery;
import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest(resetPerTest = true)
@DisableDiscovery
@ExtendWith(JulToSLF4DelegateExtension.class)
public class ConfiguredCdiBeanBinderTest {

    @Test
    @AddConfig(key = "testBean.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    void testSinglePattern01() {

        Config config = ConfigProvider.getConfig();
        ConfiguredCdiBean bean = ConfiguredCdiBeanBinder.newBinder(config).key("testBean").bind();

        assertThat(bean).isNotNull();

        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(ApplicationScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo(TestConfigBean1.class.getName());


        List<ConfiguredCdiBean> beans = ConfiguredCdiBeanBinder.newListBinder(config).key("testBean").bind();
        assertThat(beans).hasSize(1);

        bean = beans.get(0);
        assertThat(bean).isNotNull();

        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(ApplicationScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo(TestConfigBean1.class.getName());
    }

    @Test
    @AddConfig(key = "testBean.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.scope", value = "request")
    @AddConfig(key = "testBean.id", value = "test")
    void testSinglePattern02() {

        Config config = ConfigProvider.getConfig();
        ConfiguredCdiBean bean = ConfiguredCdiBeanBinder.newBinder(config).key("testBean").bind();

        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(RequestScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo("test");
    }

    @Test
    @AddConfig(key = "testBean.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$HogeHoge")
    void testSinglePattern03() {
        // .classのクラスが間違ってる
        Config config = ConfigProvider.getConfig();
        catchThrowableOfType(
                () -> ConfiguredCdiBeanBinder.newBinder(config).key("testBean").bind(),
                IllegalArgumentException.class
            );
    }

    @Test
    @AddConfig(key = "testBean.scope", value = "request")
    @AddConfig(key = "testBean.id", value = "test")
    void testSinglePattern04() {
        // .classの設定がない
        Config config = ConfigProvider.getConfig();
        catchThrowableOfType(
                () -> ConfiguredCdiBeanBinder.newBinder(config).key("testBean").bind(),
                IllegalArgumentException.class
            );
    }

    @Test
    @AddConfig(key = "testBean.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.scope", value = "app")
    void testSinglePattern05() {
        // .scopeの値が間違ってる
        Config config = ConfigProvider.getConfig();
        catchThrowableOfType(
                () -> ConfiguredCdiBeanBinder.newBinder(config).key("testBean").bind(),
                IllegalArgumentException.class
            );
    }

    @Test
    @AddConfig(key = "testBean.0.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    void testMultiPattern01() {

        Config config = ConfigProvider.getConfig();
        List<ConfiguredCdiBean> beans = ConfiguredCdiBeanBinder.newListBinder(config).key("testBean").bind();

        assertThat(beans).hasSize(1);

        ConfiguredCdiBean bean = beans.get(0);

        assertThat(bean).isNotNull();
        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(ApplicationScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo(TestConfigBean1.class.getName());
    }

    // testBean.{n}.classのnが2桁になったパターン
    @Test
    @AddConfig(key = "testBean.0.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.1.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.2.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.3.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.4.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.5.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.6.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.7.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.8.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.9.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.10.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    void testMultiPattern02() {

        Config config = ConfigProvider.getConfig();
        List<ConfiguredCdiBean> beans = ConfiguredCdiBeanBinder.newListBinder(config).key("testBean").bind();

        assertThat(beans).hasSize(11);
    }

    @Test
    @AddConfig(key = "testBean.0.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.1.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean2")
    @AddConfig(key = "testBean.1.scope", value = "request")
    @AddConfig(key = "testBean.1.id", value = "test")
    void testMultiPattern03() {

        Config config = ConfigProvider.getConfig();
        List<ConfiguredCdiBean> beans = ConfiguredCdiBeanBinder.newListBinder(config).key("testBean").bind();

        assertThat(beans).hasSize(2);

        ConfiguredCdiBean bean = beans.get(0);
        assertThat(bean).isNotNull();
        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(ApplicationScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo(TestConfigBean1.class.getName());

        bean = beans.get(1);
        assertThat(bean).isNotNull();
        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean2.class);
        assertThat(bean.getScoped()).isEqualTo(RequestScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo("test");
    }

    @Test
    @AddConfig(key = "testBean.alias.foo.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.register.alias", value = "foo")
    void testAliasPattern01() {

        Config config = ConfigProvider.getConfig();
        ConfiguredCdiBean bean = ConfiguredCdiBeanBinder.newBinder(config).alias("testBean.alias").key("testBean.register").bind();

        assertThat(bean).isNotNull();

        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(ApplicationScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo(TestConfigBean1.class.getName());

        catchThrowableOfType(
                () -> ConfiguredCdiBeanBinder.newBinder(config).key("testBean.register").bind(),
                IllegalStateException.class
            );

       List<ConfiguredCdiBean> beans = ConfiguredCdiBeanBinder.newListBinder(config).alias("testBean.alias").key("testBean.register").bind();

       assertThat(beans).hasSize(1);

        bean = beans.get(0);
        assertThat(bean).isNotNull();
        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(ApplicationScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo(TestConfigBean1.class.getName());


        catchThrowableOfType(
                () -> ConfiguredCdiBeanBinder.newListBinder(config).key("testBean.register").bind(),
                IllegalArgumentException.class
            );
    }

    @Test
    @AddConfig(key = "testBean.alias.foo.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean1")
    @AddConfig(key = "testBean.alias.foo.scope", value = "dependent")
    @AddConfig(key = "testBean.alias.foo.id", value = "test")
    @AddConfig(key = "testBean.register.0.class", value = "io.extact.rms.platform.extension.ConfiguredCdiBeanBinderTest$TestConfigBean2")
    @AddConfig(key = "testBean.register.1.alias", value = "foo")
    void testAliasPattern02() {

        Config config = ConfigProvider.getConfig();
        List<ConfiguredCdiBean> beans = ConfiguredCdiBeanBinder.newListBinder(config).alias("testBean.alias").key("testBean.register").bind();

        assertThat(beans).hasSize(2);

        ConfiguredCdiBean bean = beans.stream().filter(b -> b.getBeanClass().equals(TestConfigBean1.class)).findFirst().get();
        assertThat(bean).isNotNull();
        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean1.class);
        assertThat(bean.getScoped()).isEqualTo(Dependent.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo("test");

        bean = beans.stream().filter(b -> b.getBeanClass().equals(TestConfigBean2.class)).findFirst().get();
        assertThat(bean).isNotNull();
        assertThat(bean.getBeanClass()).isEqualTo(TestConfigBean2.class);
        assertThat(bean.getScoped()).isEqualTo(ApplicationScoped.Literal.INSTANCE);
        assertThat(bean.getId()).isEqualTo(TestConfigBean2.class.getName());
    }

    static class TestConfigBean1 {
        private boolean invokePostConstruct;
        @PostConstruct
        public void init() {
            invokePostConstruct = true;
        }
        public boolean invokePostConstruct() {
            return invokePostConstruct;
        }
    }

    static class TestConfigBean2 {
    }

}
