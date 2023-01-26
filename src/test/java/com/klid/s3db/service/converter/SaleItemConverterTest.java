package com.klid.s3db.service.converter;

import com.klid.s3db.exception.SaleConversionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Ivan Kaptue
 */
class SaleItemConverterTest {

    private final SaleItemConverter saleItemConverter = new SaleItemConverter();

    @ParameterizedTest
    @MethodSource("validInputSource")
    void shouldConvertLineToSaleItem(String line, String productName, int quantity, BigDecimal price) {
        var saleEntity = saleItemConverter.convert(line);

        assertThat(saleEntity).isNotNull();
        assertThat(saleEntity.getProduct()).isEqualTo(productName);
        assertThat(saleEntity.getQuantity()).isEqualTo((short) quantity);
        assertThat(saleEntity.getPrice()).isEqualByComparingTo(price);
    }

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
    @ValueSource(strings = {"a,,1.00", "a,a,1.00", "a,1.1,1.00", "a,32768,1.00"})
    void shouldThrowExceptionWhenQuantityNotContainsValidNumberValue(String line) {
        assertConversionException(line, "Line must contains quantity in second column and it must be a number");
    }

    @Test
    void shouldThrowExceptionWhenQuantityContainsValueLessThanOne() {
        var line = "a,0,1.00";
        assertThatThrownBy(() -> saleItemConverter.convert(line))
            .isInstanceOf(SaleConversionException.class)
            .hasMessage("Line must contains quantity in second column and it must be a number")
            .hasCauseInstanceOf(SaleConversionException.class)
            .hasRootCauseMessage("Quantity must be a number greater than 1");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a,1, ", "a,1,null"})
    void shouldThrowExceptionWhenPriceNotContainsValidDecimalValue(String line) {
        assertConversionException(line, "Line must contains price in third column and it must be a positive decimal number");
    }

    private void assertConversionException(String line, String message) {
        assertThatThrownBy(() -> saleItemConverter.convert(line))
            .isInstanceOf(SaleConversionException.class)
            .hasMessage(message);
    }

    public static Stream<Arguments> validInputSource() {
        return Stream.of(
            Arguments.of("PRACTICAL GRANITE COMPUTER,128,7909.45", "PRACTICAL GRANITE COMPUTER", 128, new BigDecimal("7909.45")),
            Arguments.of("SYNERGISTIC MARBLE WALLET,91,12433.39", "SYNERGISTIC MARBLE WALLET", 91, new BigDecimal("12433.39")),
            Arguments.of("ENORMOUS PAPER SHIRT,70,14917.44", "ENORMOUS PAPER SHIRT", 70, new BigDecimal("14917.44")),
            Arguments.of("SMALL CONCRETE SHOES,77,8551.43", "SMALL CONCRETE SHOES", 77, new BigDecimal("8551.43")),
            Arguments.of("SYNERGISTIC PAPER SHIRT,93,12901.18", "SYNERGISTIC PAPER SHIRT", 93, new BigDecimal("12901.18"))
        );
    }
}
