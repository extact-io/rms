package io.extact.rms.platform.jaxrs.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import org.eclipse.microprofile.config.ConfigProvider;

// Provider Class
// register by @RegisterProvider or Application#getClasseses()
public class ParamRmsConverterProvider implements ParamConverterProvider {

    private final String datePattern;
    private final String dateTimePattern;

    public ParamRmsConverterProvider() {
        // ConfigがなぜかInjectで取得できないためProvierクラス経由で取得
        this.datePattern = ConfigProvider.getConfig().getValue("json.format.date", String.class);
        this.dateTimePattern = ConfigProvider.getConfig().getValue("json.format.dateTime", String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == LocalDate.class) {
            return (ParamConverter<T>) new LocalDateConverter();
        }
        if (rawType == LocalDateTime.class) {
            return (ParamConverter<T>) new LocalDateTimeConverter();
        }
        return null;
    }


    // ----------------------------------------------------- inner classes

    public class LocalDateConverter implements ParamConverter<LocalDate> {
        @Override
        public LocalDate fromString(String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(datePattern));
        }
        @Override
        public String toString(LocalDate value) {
            return value == null ? "" : value.format(DateTimeFormatter.ofPattern(datePattern));
        }
    }

    public class LocalDateTimeConverter implements ParamConverter<LocalDateTime> {
        @Override
        public LocalDateTime fromString(String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(dateTimePattern));
        }
        @Override
        public String toString(LocalDateTime value) {
            return value == null ? "" : value.format(DateTimeFormatter.ofPattern(dateTimePattern));
        }
    }
}
