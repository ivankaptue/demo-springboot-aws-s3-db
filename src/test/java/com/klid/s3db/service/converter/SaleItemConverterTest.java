package com.klid.s3db.service.converter;

import com.klid.s3db.exception.SaleConversionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
        assertThat(saleEntity.getQuantity()).isEqualTo(quantity);
        assertThat(saleEntity.getPrice()).isEqualByComparingTo(price);
    }

    @Test
    void shouldThrowExceptionWhenUnexpectedErrorOccur() {
        var line = "PAPER,93,null";
        assertThatThrownBy(() -> saleItemConverter.convert(line))
            .isInstanceOf(SaleConversionException.class)
            .hasMessageStartingWith("Unexpected error when convert line");
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
