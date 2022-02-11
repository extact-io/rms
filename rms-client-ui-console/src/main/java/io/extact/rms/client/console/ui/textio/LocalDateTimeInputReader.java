package io.extact.rms.client.console.ui.textio;

import static io.extact.rms.client.console.ui.ClientConstants.*;
import static java.time.temporal.ChronoUnit.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.beryx.textio.InputReader;
import org.beryx.textio.TextTerminal;

public class LocalDateTimeInputReader extends InputReader<LocalDateTime, LocalDateTimeInputReader> {

    private LocalDateTime baseDateTime;

    public LocalDateTimeInputReader(TextTerminal<?> textTerminal) {
        super(() -> textTerminal);
        valueCheckers.add(this::getFutureValidationErrors);
    }

    @Override
    protected ParseResult<LocalDateTime> parse(String s) {
        try {
            return new ParseResult<>(LocalDateTime.parse(s, DATETIME_FORMAT).truncatedTo(MINUTES));
        } catch (DateTimeParseException e) {
            return new ParseResult<>(null,
                        List.of(
                                getDefaultErrorMessage(s),
                                "Please enter in YYYY/MM/DD HH:mm format"
                                ));
        }
    }

    public InputReader<LocalDateTime, LocalDateTimeInputReader> withFutureNow() {
        baseDateTime = LocalDateTime.now().truncatedTo(MINUTES);
        return this;
    }

    public InputReader<LocalDateTime, LocalDateTimeInputReader> withFutureThan(LocalDateTime startDateTime) {
        baseDateTime = startDateTime.truncatedTo(MINUTES);
        return this;
    }

    private List<String> getFutureValidationErrors(LocalDateTime val, String propName) {
        if (!val.isAfter(baseDateTime)) {
            return List.of(
                        "Please enter a future date and time from " + DATETIME_FORMAT.format(baseDateTime)
                        );
        }
        return null;
    }
}
