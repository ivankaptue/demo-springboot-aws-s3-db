package com.klid.s3db.model.dto;

import com.klid.s3db.model.enums.StatusEnum;

import java.io.Serializable;

/**
 * @author Ivan Kaptue
 */
public record Store(String id, String name, StatusEnum status) implements Serializable {
}
