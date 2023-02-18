package com.klid.s3db.service.validator;

import com.klid.s3db.exception.ArgumentValidationException;
import com.klid.s3db.utils.UUIDGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ArgumentValidator {

    public void validate(int page, int size) {
        if (page < 1) {
            throw new ArgumentValidationException("Page must be greater than 1");
        }
        if (size < 10) {
            throw new ArgumentValidationException("Size must be greater than 10");
        }
    }

    public void validate(int page, int size, String id) {
        validate(page, size);
        if (!StringUtils.hasText(id)) {
            throw new ArgumentValidationException("Id must be defined");
        }
        validateUUID(id);
    }

    public void validateUUID(String id) {
        if (!id.matches(UUIDGenerator.UUID_PATTERN)) {
            throw new ArgumentValidationException("Id must be of type UUID");
        }
    }
}
