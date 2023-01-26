package com.klid.s3db.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Ivan Kaptue
 */
@Component
public class UUIDProvider {

    public String uuid() {
        return UUID.randomUUID().toString();
    }
}
