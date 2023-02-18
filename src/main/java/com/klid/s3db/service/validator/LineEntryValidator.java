package com.klid.s3db.service.validator;

import com.klid.s3db.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
public class LineEntryValidator {

    public static final String ITEM_SEPARATOR = ",";

    public void validate(String line) {
        validateLine(line);
        var data = line.split(ITEM_SEPARATOR);
        validateData(data);
    }

    private void validateData(String[] data) {
        if (data.length != 3) {
            throw new ValidationException("Line must contains exactly 3 columns");
        }

        if (!StringUtils.hasText(data[0])) {
            throw new ValidationException("Line must contains product name in first column");
        }

        try {
            var quantity = NumberUtils.parseNumber(data[1], Integer.class);
            if (quantity < 1) {
                throw new ValidationException("Quantity must be a number greater than 1");
            }
        } catch (Exception ex) {
            throw new ValidationException("Line must contains quantity in second column and it must be a number", ex);
        }

        try {
            NumberUtils.parseNumber(data[2], BigDecimal.class);
        } catch (Exception ex) {
            throw new ValidationException("Line must contains price in third column and it must be a positive decimal number", ex);
        }
    }

    private void validateLine(String line) {
        if (!StringUtils.hasText(line)) {
            throw new ValidationException("Line must be defined");
        }
    }
}
