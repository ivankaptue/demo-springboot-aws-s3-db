package com.klid.s3db.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Ivan Kaptue
 */
class UUIDProviderTest {

    private static final String UUID_PATTERN = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})";

    private final UUIDProvider uuidProvider = new UUIDProvider();

    @Test
    void shouldReturnsValidUUID() {
        var uuid = uuidProvider.uuid();

        assertThat(uuid).isNotBlank();
        assertThat(uuid).matches(Pattern.compile(UUID_PATTERN));
    }
}
