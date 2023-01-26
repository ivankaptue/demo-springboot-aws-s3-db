package com.klid.s3db.service.converter;

import com.klid.s3db.exception.SaleConversionException;
import com.klid.s3db.service.persistence.entity.SaleEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Ivan Kaptue
 */
@Service
public class SaleItemConverter {

    private static final String ITEM_SEPARATOR = ",";

    public SaleEntity convert(String line) {
        validateLine(line);
        var data = line.split(ITEM_SEPARATOR);
        validateData(data);

        return SaleEntity.builder()
            .product(data[0].strip())
            .quantity(NumberUtils.parseNumber(data[1], Short.class))
            .price(NumberUtils.parseNumber(data[2], BigDecimal.class).setScale(2, RoundingMode.UNNECESSARY))
            .build();
    }

    private void validateData(String[] data) {
        if (data.length != 3) {
            throw new SaleConversionException("Line must contains exactly 3 columns");
        }

        if (!StringUtils.hasText(data[0])) {
            throw new SaleConversionException("Line must contains product name in first column");
        }

        try {
            var quantity = NumberUtils.parseNumber(data[1], Short.class);
            if (quantity < 1) {
                throw new SaleConversionException("Quantity must be a number greater than 1");
            }
        } catch (Exception ex) {
            throw new SaleConversionException("Line must contains quantity in second column and it must be a number", ex);
        }

        try {
            NumberUtils.parseNumber(data[2], BigDecimal.class);
        } catch (Exception ex) {
            throw new SaleConversionException("Line must contains price in third column and it must be a positive decimal number", ex);
        }
    }

    private void validateLine(String line) {
        if (!StringUtils.hasText(line)) {
            throw new SaleConversionException("Line must be defined");
        }
    }
}
