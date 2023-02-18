package com.klid.s3db.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StoreEntityNotFoundException extends RuntimeException {

    private final UUID id;

    public StoreEntityNotFoundException(UUID id) {
        super("Store entity not found");
        this.id = id;
    }
}
