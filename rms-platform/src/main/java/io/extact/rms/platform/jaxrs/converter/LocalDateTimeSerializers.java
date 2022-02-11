package io.extact.rms.platform.jaxrs.converter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;

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
