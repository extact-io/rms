package io.extact.rms.client.console.ui.textio;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.beryx.textio.InputReader;
import org.beryx.textio.TextTerminal;

import lombok.Getter;

public class RmsStringInputReader extends InputReader<String, RmsStringInputReader> {

    private PatternMessage patternMessage;
    private int minLength = 0;
    private int maxLength = -1;
    private String excludeCheckString;

    public RmsStringInputReader(TextTerminal<?> textTerminalSupplier) {
        super(() -> textTerminalSupplier);
        valueCheckers.add((val, propName) -> getLengthValidationErrors(val));
        valueCheckers.add((val, propName) -> getPatternValidationErrors(val));
    }

    public RmsStringInputReader withPattern(PatternMessage pm) {
        this.patternMessage = pm;
        return this;
    }

    public RmsStringInputReader withMinLength(int minLength) {
        this.minLength = minLength;
        return this;
    }

    public RmsStringInputReader withMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public InputReader<String, RmsStringInputReader> withExcludeCheckString(String string) {
        this.excludeCheckString = string;
        return this;
    }

    @Override
    protected ParseResult<String> parse(String s) {
        return new ParseResult<>(s);
    }


    @Override
    protected void checkConfiguration() throws IllegalArgumentException {
        super.checkConfiguration();
        if (minLength > 0 && maxLength > 0 && minLength > maxLength) {
            throw new IllegalArgumentException("minLength = " + minLength + ", maxLength = " + maxLength);
        }
    }

    protected List<String> getLengthValidationErrors(String s) {
        if (isExcludeCheckString(s)) {
            return null;
        }
        int len = (s == null) ? 0 : s.length();
        if (minLength > 0 && len < minLength) {
            return Collections.singletonList("Please enter at least " + minLength  + "character.");
        }
        if (maxLength > 0 && maxLength < len) {
            return Collections.singletonList("Please enter within " +maxLength + "characters.");
        }
        return null;
    }

    protected List<String> getPatternValidationErrors(String s) {
        if (isExcludeCheckString(s)) {
            return null;
        }
        if ((patternMessage != null) && !patternMessage.getPattern().matcher(s).matches()) {
            return Collections.singletonList(patternMessage.getMessage());
        }
        return null;
    }

    private boolean isExcludeCheckString(String s) {
        return excludeCheckString != null && excludeCheckString.equals(s);
    }

    @Getter
    public enum PatternMessage {
        SERIAL_NO("[0-9a-zA-Z\\-]*", "Please enter in half-width alphanumeric hyphen."),
        PHONE_NUMBER("[0-9\\-]*", "Please enter in half-width number hyphen.");
        private final Pattern pattern;
        private final String message;
        private PatternMessage(String pattern, String message) {
            this.pattern = Pattern.compile(pattern);
            this.message = message;
        }
    }

}
