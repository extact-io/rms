package io.extact.rms.platform.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;

import lombok.extern.slf4j.Slf4j;

/**
 * Dump out Config at startup.
 * Filter the output by setting configdump.filters as below.
 * <pre>
 * configdump:
 *   filter-enable: true
 *   filters:
 *     - filter: server
 *     - filter: security
 * </pre>
 */
@ApplicationScoped
@Slf4j(topic = "ConfigDump")
public class MpConfigDump {

    private Config config;

    @Inject
    public MpConfigDump(Config config) {
        this.config = config;
    }
    void onInialized(@Observes @Initialized(ApplicationScoped.class) Object event) {

        if (!log.isDebugEnabled()) {
            return;
        }

        List<String> filters = new ArrayList<>();
        if (config.getOptionalValue("configdump.filter-enable", Boolean.class).orElse(false)) {
            filters = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                    .filter(s -> s.startsWith("configdump.filters"))
                    .map(s -> config.getValue(s, String.class))
                    .collect(Collectors.toList());
        }

        Predicate<String> containsForwardMatch = new ContainsForwardMatch(filters);
        String configDump = StreamSupport.stream(config.getPropertyNames().spliterator(), false)
            .filter(containsForwardMatch)
            .map(name -> name + "=" + config.getValue(name, String.class))
            .sorted()
            .collect(Collectors.joining(System.lineSeparator()));

        log.debug(System.lineSeparator() + configDump);
    }

    static class ContainsForwardMatch implements Predicate<String> {

        private List<String> filters;
        ContainsForwardMatch(List<String> filters) {
            this.filters = filters;
        }

        @Override
        public boolean test(String name) {
            return filters.isEmpty() || filters.stream().anyMatch(name::startsWith);
        }
    }
}
