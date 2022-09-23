package io.extact.rms.test.assertj;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;

import org.assertj.core.api.AbstractAssert;

@SuppressWarnings("rawtypes")
public class ConstraintViolationSetAssert extends AbstractAssert<ConstraintViolationSetAssert, Set<? extends ConstraintViolation>> {

    public ConstraintViolationSetAssert(Set<? extends ConstraintViolation> actual) {
        super(actual, ConstraintViolationSetAssert.class);
    }

    public static ConstraintViolationSetAssert assertThat(Set<? extends ConstraintViolation> actual) {
        return new ConstraintViolationSetAssert(actual);
    }

    public ConstraintViolationSetAssert hasSize(int size) {
        isNotNull();
        if (actual.size() != size) {
            List<String> messages = actual.stream()
                    .map(ConstraintViolation::getMessageTemplate)
                    .toList();
            failWithMessage("Expecting %s violations, but there are %s violations. Violation messages: <%s>", size, actual.size(), messages);
        }
        return this;
    }

    public ConstraintViolationSetAssert hasViolationOnPath(String path) {
        isNotNull();
        if (!containsViolationWithPath(actual, path)) {
            List<String> paths = actual.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .toList();

            failWithMessage("There was no violation with path <%s>. Violation paths: <%s>", path, paths);
        }
        return this;
    }

    public ConstraintViolationSetAssert hasViolationOnPathConstaining(String path) {
        isNotNull();
        if (!containsViolationWithPathContaining(actual, path)) {
            List<String> paths = actual.stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .toList();

            failWithMessage("There was no violation with path constaining <%s>. Violation paths: <%s>", path, paths);
        }
        return this;
    }

    public ConstraintViolationSetAssert hasMessageEndingWith(String suffix) {
        isNotNull();
        if (!containsViolationWithMessageEndingWith(actual, suffix)) {
            List<String> messages = actual.stream()
                    .map(ConstraintViolation::getMessageTemplate)
                    .toList();
            failWithMessage("There was no violation with message ending with <%s> . Violation messages: <%s>", suffix, messages);
        }
        return this;
    }

    public ConstraintViolationSetAssert hasNoViolations() {
        isNotNull();
        if (!actual.isEmpty()) {
            List<String> messages = actual.stream()
                    .map(ConstraintViolation::getMessageTemplate)
                    .toList();
            failWithMessage("Expecting no violations, but there are %s violations. Violation messages: <%s>", actual.size(), messages);
        }
        return this;
    }

    private boolean containsViolationWithPath(Set<? extends ConstraintViolation> violations, String path) {
        return violations.stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().equals(path));
    }

    private boolean containsViolationWithPathContaining(Set<? extends ConstraintViolation> violations, String path) {
        return violations.stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().contains(path));
    }

    private boolean containsViolationWithMessageEndingWith(Set<? extends ConstraintViolation> violations, String suffix) {
        return violations.stream()
                .map(violation -> violation.getMessageTemplate().replace("{", "").replace("}", ""))
                .anyMatch(message -> message.endsWith(suffix));
    }
}