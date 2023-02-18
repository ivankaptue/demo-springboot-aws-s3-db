package com.klid.s3db.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class StoreEntityNotFound extends RuntimeException {

    private final UUID id;

    public StoreEntityNotFound(UUID id) {
        super("Store entity not found");
        this.id = id;
    }
}
