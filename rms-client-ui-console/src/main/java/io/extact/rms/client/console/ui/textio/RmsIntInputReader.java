package io.extact.rms.client.console.ui.textio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.beryx.textio.InputReader;
import org.beryx.textio.IntInputReader;
import org.beryx.textio.TextTerminal;

public class RmsIntInputReader extends IntInputReader {

    private List<Integer> selectableValues;

    public RmsIntInputReader(TextTerminal<?> textTerminalSupplier) {
        super(() -> textTerminalSupplier);
        valueCheckers.add((val, propName) -> getSelectableValidationErrors(val));
    }

    public InputReader<Integer, IntInputReader> withSelectableValues(List<Integer> values, int excludeValue) {
        values = new ArrayList<>(values);
        values.add(excludeValue);
        return withSelectableValues(values);
    }

    public InputReader<Integer, IntInputReader> withSelectableValues(List<Integer> values) {
        selectableValues = values;
        return this;
    }

    private List<String> getSelectableValidationErrors(Integer val) {
        if (!selectableValues.contains(val)) {
            return Collections.singletonList("Please enter a selectable value.");
        }
        return null;
    }
}
