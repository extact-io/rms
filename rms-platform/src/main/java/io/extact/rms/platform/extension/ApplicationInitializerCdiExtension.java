package io.extact.rms.platform.extension;

import java.util.List;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;

import org.eclipse.microprofile.config.ConfigProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationInitializerCdiExtension implements Extension {

    private static final String CDI_REGISTER_CONFIG_KEY = "configuredCdi.register";
    private static final String CDI_ALIAS_CONFIG_KEY = "configuredCdi.alias";

    void enabledIfRuntimeConfig(@Observes @WithAnnotations(EnabledIfRuntimeConfig.class) ProcessAnnotatedType<?> event) {
        EnabledIfRuntimeConfig annotation = event.getAnnotatedType().getAnnotation(EnabledIfRuntimeConfig.class);
        var config = ConfigProvider.getConfig();
        String runtimeConfigValue = config.getValue(annotation.propertyName(), String.class);
        if (runtimeConfigValue.equals(annotation.value())) {
            log.info("EnabledIfRuntimeConfigがアノテートされているCDIクラスを有効化しました [Class:{}]", event.getAnnotatedType().getJavaClass().getSimpleName());
        } else {
            event.veto();
            log.info("EnabledIfRuntimeConfigがアノテートされているCDIクラスを無効化しました [Class:{}]", event.getAnnotatedType().getJavaClass().getSimpleName());
        }
    }

    void registerConfiguredBeans(@Observes BeforeBeanDiscovery event) {
        var config = ConfigProvider.getConfig();
        List<ConfiguredCdiBean> configCdiBeans = ConfiguredCdiBeanBinder.newListBinder(config).alias(CDI_ALIAS_CONFIG_KEY)
                .key(CDI_REGISTER_CONFIG_KEY).bind();

        if (configCdiBeans.isEmpty()) {
            log.info("{}によるCDIBean登録はありませんでした", CDI_REGISTER_CONFIG_KEY);
        }

        configCdiBeans.forEach(configCdiBean -> {
            AnnotatedTypeConfigurator<?> configurator = event.addAnnotatedType(configCdiBean.getBeanClass(), configCdiBean.getId());
            configurator.add(configCdiBean.getScoped());
            log.info("[{}]を設定によりCDIBean登録しました", configCdiBean.getBeanClass().getName());
        });
    }
}
