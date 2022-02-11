package io.extact.rms.platform.jaxrs.converter;

import java.time.format.DateTimeFormatter;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.ext.ContextResolver;

import org.eclipse.microprofile.config.ConfigProvider;

// Provider Class
// register by @RegisterProvider or Application#getClasseses()
public class JsonbRmsConfig implements ContextResolver<Jsonb> {

    private Jsonb jsonb;

    public JsonbRmsConfig() {
        // ConfigがなぜかInjectで取得できないためProvierクラス経由で取得
        var dateTimeFormat = ConfigProvider.getConfig().getValue("json.format.dateTime", String.class);
        var serializers = new LocalDateTimeSerializers(DateTimeFormatter.ofPattern(dateTimeFormat));
        var config = new JsonbConfig()
                    .withSerializers(serializers.getSerializer())
                    .withDeserializers(serializers.getDeserializer());
        jsonb = JsonbBuilder.create(config);
    }

    @Override
    public Jsonb getContext(Class<?> type) {
        return jsonb;
    }
}
