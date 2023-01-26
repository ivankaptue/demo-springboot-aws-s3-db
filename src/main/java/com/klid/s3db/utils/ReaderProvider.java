package com.klid.s3db.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Ivan Kaptue
 */
@Component
public class ReaderProvider {

    public BufferedReader provideReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
