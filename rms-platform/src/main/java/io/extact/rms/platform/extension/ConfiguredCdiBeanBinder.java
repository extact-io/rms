package io.extact.rms.platform.extension;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.config.Config;

public class ConfiguredCdiBeanBinder {

    private static final Map<String, Annotation> BEAN_SCOPES = Map.of(
            "application", ApplicationScoped.Literal.INSTANCE,
            "request", RequestScoped.Literal.INSTANCE,
            "dependent", Dependent.Literal.INSTANCE);

    private Config config;
    private String registerPrefix;
    private List<AliasKey> aliasKeys;


    // ----------------------------------------------------- factory methods

    public static ConfiguredCdiBeanBinder newBinder(Config config) {
        var configBinder = new ConfiguredCdiBeanBinder();
        configBinder.config = config;
        return configBinder;
    }

    public static ConfiguredCdiBeansListBinder newListBinder(Config config) {
        return ConfiguredCdiBeanBinder.newBinder(config).new ConfiguredCdiBeansListBinder();
    }


    // ----------------------------------------------------- public methods

    public ConfiguredCdiBeanBindFinisher key(String regsiterPrefix) {
        this.registerPrefix = regsiterPrefix;
        return new ConfiguredCdiBeanBindFinisher();
    }

    public ConfiguredCdiBeanBinder alias(String aliasPrefixKey) {
        this.aliasKeys = seekAliasKeys(aliasPrefixKey);
        return this;
    }

    private ConfiguredCdiBeanBinder aliasKeys(List<AliasKey> aliasKeys) {
        this.aliasKeys = aliasKeys;
        return this;
    }


    // ----------------------------------------------------- private methods

    private List<AliasKey> seekAliasKeys(String prefix) {
        // (config)
        // a.b.c.x.class = Foo
        // a.b.c.x.scope = application
        // a.b.c.y.tyepe = Bar
        // a.b.c.y.scope = request
        // ---------------------------
        // keyPrefix => "a.b.c"
        // map       => ["a.b.c.x", "a.b.c.x", "a.b.c.y", "a.b.c.y"]
        // distinct  => ["a.b.c.x", "a.b.c.y"]
        // map       => [{"x":"a.b.c.x"}, {"y":"a.b.c.y"}]
        //
        int endPosOfPrefix = prefix.length() + 1;
        return StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(s -> s.startsWith(prefix))
                .map(s -> s.substring(0, endPosOfPrefix + s.indexOf('.', endPosOfPrefix) - (endPosOfPrefix)))
                .distinct()
                .map(s -> new AliasKey(s.substring(endPosOfPrefix), s))
                .collect(Collectors.toList());
    }


    // ----------------------------------------------------- inner class(Binder)

    public class ConfiguredCdiBeanBindFinisher {

        private ConfiguredCdiBeanBindFinisher() {
            // nop
        }

        public ConfiguredCdiBean bind() {

            Optional<String> alias = config.getOptionalValue(registerPrefix + ".alias", String.class);
            if (alias.isPresent()) {
                if (aliasKeys == null) {
                    throw new IllegalStateException("alias() not called!");
                }
                AliasKey aliasKey = aliasKeys.stream()
                        .filter(key -> alias.get().equals(key.aliasName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("alias is unmatch. [" + alias.get() + "]"));
                return ConfiguredCdiBeanBinder.newBinder(config).key(aliasKey.aliasConfigKey).bind();
            }

            var configCdiBean = new ConfiguredCdiBean();
            configCdiBean.beanClass = config.getOptionalValue(registerPrefix + ".class", Class.class)
                    .orElseThrow(() -> new IllegalArgumentException("class is required. [" + registerPrefix + ".class]")); // class is required.

            String scopeValue = config.getOptionalValue(registerPrefix + ".scope", String.class).orElse("application"); // applicaiton is default.
            configCdiBean.scope = Optional.ofNullable(BEAN_SCOPES.get(scopeValue))
                    .orElseThrow(() -> new IllegalArgumentException("unknown scoped value. [" + registerPrefix + ".scope:" + scopeValue + "]"));

            configCdiBean.id = config.getOptionalValue(registerPrefix + ".id", String.class).orElse(configCdiBean.beanClass.getName()); // FQCN is default.

            return configCdiBean;
        }
    }


    // ----------------------------------------------------- inner classes(ListBinder)

    public class ConfiguredCdiBeansListBinder {

        private ConfiguredCdiBeansListBinder() {
            // nop
        }

        private String listAliasPrefix;
        private String listRegisterPrefix;

        public ConfiguredCdiBeansListBinder alias(String listAliasPrefix) {
            this.listAliasPrefix = listAliasPrefix;
            return this;
        }

        public ConfiguredCdiBeansListBindFinisher key(String listRegisterPrefix) {
            this.listRegisterPrefix = listRegisterPrefix;
            return new ConfiguredCdiBeansListBindFinisher(this);
        }

    }

    public class ConfiguredCdiBeansListBindFinisher {

        private ConfiguredCdiBeansListBinder parent;

        private ConfiguredCdiBeansListBindFinisher(ConfiguredCdiBeansListBinder listBinder) {
            this.parent = listBinder;
        }

        public List<ConfiguredCdiBean> bind() {

            List<AliasKey> foundAliasKeys = List.of();
            if (parent.listAliasPrefix != null) {
                foundAliasKeys = seekAliasKeys(parent.listAliasPrefix);
            }

            // (by single config)
            // a.b.c.class = Foo
            // a.b.c.scope = application
            //
            Optional<String> clazz = config.getOptionalValue(parent.listRegisterPrefix + ".class", String.class);
            Optional<String> alias = config.getOptionalValue(parent.listRegisterPrefix + ".alias", String.class);
            if (clazz.isPresent() || alias.isPresent()) {
                ConfiguredCdiBean configCdiBean =
                        ConfiguredCdiBeanBinder.newBinder(config)
                            .aliasKeys(foundAliasKeys)
                            .key(parent.listRegisterPrefix)
                            .bind();
                return List.of(configCdiBean);
            }

            // (by list config)
            // a.b.c.0.class = Foo
            // a.b.c.0.scope = application
            // a.b.c.1.tyepe = Bar
            // a.b.c.1.scope = request
            // ---------------------------
            // keyPrefix => "a.b.c"
            // map       => ["a.b.c.0", "a.b.c.0", "a.b.c.1", "a.b.c.1"]
            // distinct  => ["a.b.c.0", "a.b.c.1"]
            //
            int endPosOfPrefix = parent.listRegisterPrefix.length() + 1;
            List<String> keys = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                    .filter(s -> s.startsWith(parent.listRegisterPrefix))
                    .map(s -> s.substring(0, endPosOfPrefix + s.indexOf('.', endPosOfPrefix) - endPosOfPrefix))
                    .distinct()
                    .collect(Collectors.toList());

            List<ConfiguredCdiBean> configCdiBeans = new ArrayList<>();
            for (int i = 0; i < keys.size(); i++) {
                configCdiBeans.add(ConfiguredCdiBeanBinder.newBinder(config).aliasKeys(foundAliasKeys).key(parent.listRegisterPrefix + "." + i).bind());
            }
            return configCdiBeans;
        }
    }


    // ----------------------------------------------------- inner classes(AliasKey)

    static class AliasKey {
        private String aliasName;
        private String aliasConfigKey;
        private AliasKey(String aliasName, String aliasConfigKey) {
            this.aliasName = aliasName;
            this.aliasConfigKey = aliasConfigKey;
        }
    }
}
