package io.extact.rms.platform.jaxrs.converter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

public class LocalDateTimeSerializers {

    private DateTimeFormatter dateTimeFormatter;

    // ----------------------------------------------------- public methods

    public LocalDateTimeSerializers(DateTimeFormatter formatter) {
        this.dateTimeFormatter = formatter;
    }

    public JsonbSerializer<LocalDateTime> getSerializer() {
        return new LocalDateTimeSerializer();
    }

    public JsonbDeserializer<LocalDateTime> getDeserializer() {
        return new LocaDateTimeDeserializer();
    }


    // ----------------------------------------------------- inner classes

    public class LocalDateTimeSerializer implements JsonbSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(dateTimeFormatter.format(obj));
        }
    }

    public class LocaDateTimeDeserializer implements JsonbDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            var value = parser.getString();
            return LocalDateTime.parse(value, dateTimeFormatter);
        }
    }
}
