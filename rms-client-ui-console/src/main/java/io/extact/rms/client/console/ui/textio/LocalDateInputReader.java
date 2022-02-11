package io.extact.rms.client.console.ui.textio;

import static io.extact.rms.client.console.ui.ClientConstants.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.beryx.textio.InputReader;
import org.beryx.textio.TextTerminal;

public class LocalDateInputReader extends InputReader<LocalDate, LocalDateInputReader> {

    public LocalDateInputReader(TextTerminal<?> textTerminal) {
        super(() -> textTerminal);
    }

    @Override
    protected ParseResult<LocalDate> parse(String s) {
        try {
            return new ParseResult<>(LocalDate.parse(s, DATE_FORMAT));
        } catch (DateTimeParseException e) {
            return new ParseResult<>(null,
                    List.of(
                            getDefaultErrorMessage(s),
                            "Please enter in YYYY/MM/DD format"
                            ));
        }
    }
}
