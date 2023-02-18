package com.klid.s3db.service.converter;

import com.klid.s3db.exception.SaleConversionException;
import com.klid.s3db.service.persistence.entity.SaleEntity;
import com.klid.s3db.service.validator.LineEntryValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Ivan Kaptue
 */
@Slf4j
@Service
public class SaleItemConverter {

    public SaleEntity convert(String line) {
        log.info("Convert entry : {}", line);

        try {
            var data = line.split(LineEntryValidator.ITEM_SEPARATOR);

            return SaleEntity.builder()
                .product(data[0].strip())
                .quantity(NumberUtils.parseNumber(data[1], Integer.class))
                .price(NumberUtils.parseNumber(data[2], BigDecimal.class).setScale(2, RoundingMode.UNNECESSARY))
                .build();
        } catch (Exception ex) {
            throw new SaleConversionException(String.format("Unexpected error when convert line %s", line), ex);
        }
    }
}
