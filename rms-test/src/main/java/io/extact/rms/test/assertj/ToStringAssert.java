package io.extact.rms.test.assertj;

import java.util.List;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.internal.ComparisonStrategy;
import org.assertj.core.internal.Objects;
import org.assertj.core.internal.StandardComparisonStrategy;

public class ToStringAssert {


    // ----------------------------------------------------- using equals strategry

    private static final ComparisonStrategy TO_STRING_STRATEGY = new StandardComparisonStrategy() {
        @Override
        public boolean areEqual(Object actual, Object other) {
            if (actual == other) {
                return true;
            } else if (actual == null || other == null) {
                return false;
            } else {
                return actual.toString().equals(other.toString());
            }
        }
    };


    // ----------------------------------------------------- public methods

    public static <T> ObjectAssert<T> assertThatToString(T actual) {
        return new ObjectToStringComparisonAssert<>(actual);
    }

    public static <E> ListAssert<E> assertThatToString(List<? extends E> actual) {
        return new ListToStringComparisonAssert<>(actual);
    }


    // ----------------------------------------------------- inner classes

    public static class ObjectToStringComparisonAssert<T> extends ObjectAssert<T> {

        public ObjectToStringComparisonAssert(T actual) {
            super(actual);
            this.objects = new Objects(TO_STRING_STRATEGY);
        }
    }

    public static class ListToStringComparisonAssert<ELEMENT> extends ListAssert<ELEMENT> {
        public ListToStringComparisonAssert(List<? extends ELEMENT> actual) {
            super(actual);
            this.usingComparisonStrategy(TO_STRING_STRATEGY);
        }
    }
}
