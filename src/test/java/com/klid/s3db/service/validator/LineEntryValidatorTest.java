package com.klid.s3db.service.validator;

import com.klid.s3db.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineEntryValidatorTest {

    private final LineEntryValidator validator = new LineEntryValidator();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldThrowExceptionWhenLineNotDefined(String line) {
        assertConversionException(line, "Line must be defined");
    }

    @ParameterizedTest
    @ValueSource(strings = {",", "a,a", "a,a,a,a"})
    void shouldThrowExceptionWhenLineNotContainsValidColumnsCount(String line) {
        assertConversionException(line, "Line must contains exactly 3 columns");
    }

    @ParameterizedTest
    @ValueSource(strings = {",1,1.00", " ,1,1.00"})
    void shouldThrowExceptionWhenProductNameNotDefinedInFirstColumn(String line) {
        assertConversionException(line, "Line must contains product name in first column");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a,,1.00", "a,a,1.00", "a,1.1,1.00"})
    void shouldThrowExceptionWhenQuantityNotContainsValidNumberValue(String line) {
        assertConversionException(line, "Line must contains quantity in second column and it must be a number");
    }

    @Test
    void shouldThrowExceptionWhenQuantityContainsValueLessThanOne() {
        var line = "a,0,1.00";
        assertThatThrownBy(() -> validator.validate(line))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Line must contains quantity in second column and it must be a number")
            .hasCauseInstanceOf(ValidationException.class)
            .hasRootCauseMessage("Quantity must be a number greater than 1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a,1, ", "a,1,null"})
    void shouldThrowExceptionWhenPriceNotContainsValidDecimalValue(String line) {
        assertConversionException(line, "Line must contains price in third column and it must be a positive decimal number");
    }

    private void assertConversionException(String line, String message) {
        assertThatThrownBy(() -> validator.validate(line))
            .isInstanceOf(ValidationException.class)
            .hasMessage(message);
    }
}
