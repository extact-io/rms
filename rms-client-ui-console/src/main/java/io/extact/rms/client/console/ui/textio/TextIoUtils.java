package io.extact.rms.client.console.ui.textio;

import static io.extact.rms.client.console.ui.ClientConstants.*;

import org.beryx.textio.EnumInputReader;
import org.beryx.textio.TextIoFactory;

import io.extact.rms.client.api.dto.UserAccountClientDto;
import io.extact.rms.client.api.exception.BusinessFlowClientException;

public class TextIoUtils {

    public static void printScreenHeader(UserAccountClientDto loginUser, String screenName) {
        var msg = System.lineSeparator()
                + "===================================================" + System.lineSeparator()
                + "レンタル予約システム － %s" + System.lineSeparator()
                + "===================================================" + System.lineSeparator()
                + "ログイン：%s" + System.lineSeparator()
                + System.lineSeparator();

        var terminal = TextIoFactory.getTextIO().getTextTerminal();
        terminal.printf(msg, screenName, loginUser.getUserName());
    }

    public static void waitPressEnter() {
        TextIoFactory.getTextIO().newStringInputReader()
            .withDefaultValue("ENTER")
            .read("Please press enter.");
    }

    public static void printServerError(BusinessFlowClientException e) {
        TextIoFactory.getTextTerminal().executeWithPropertiesConfigurator(
                props -> props.setPromptColor("red"),
                t -> t.println(e.getMessage() + System.lineSeparator())
        );
    }

    public static void printErrorInformation(String msg) {
        TextIoFactory.getTextTerminal().executeWithPropertiesConfigurator(
                props -> props.setPromptColor("red"),
                t -> t.println(msg + System.lineSeparator())
        );
    }

    public static void printWarningInformation(String msg) {
        TextIoFactory.getTextTerminal().executeWithPropertiesConfigurator(
                props -> props.setPromptColor("yellow"),
                t -> t.println(msg + System.lineSeparator())
        );
    }

    public static void blankLine() {
        TextIoFactory.getTextTerminal().println();
    }

    public static void println(String msg) {
        TextIoFactory.getTextTerminal().println(msg);
    }

    public static void printf(String format, Object... args) {
        TextIoFactory.getTextTerminal().printf(format, args);
    }

    public static RmsStringInputReader newStringInputReader() {
        return new RmsStringInputReader(TextIoFactory.getTextIO().getTextTerminal());
    }

    public static RmsIntInputReader newIntInputReader() {
        return new RmsIntInputReader(TextIoFactory.getTextIO().getTextTerminal());
    }

    public static LocalDateInputReader newLocalDateReader() {
        return new LocalDateInputReader(TextIoFactory.getTextIO().getTextTerminal());
    }

    public static LocalDateTimeInputReader newLocalDateTimeReader() {
        return new LocalDateTimeInputReader(TextIoFactory.getTextIO().getTextTerminal());
    }

    public static <T extends Enum<T>> EnumInputReader<T> newEnumInputReader(Class<T> enumClass) {
        return new EnumInputReader<>(TextIoFactory::getTextTerminal, enumClass);
    }

    public static boolean isBreak(String input) {
        return input != null && input.equals(SCREEN_BREAK_KEY);
    }
    public static boolean isBreak(int input) {
        return input == SCREEN_BREAK_VALUE;
    }
}
