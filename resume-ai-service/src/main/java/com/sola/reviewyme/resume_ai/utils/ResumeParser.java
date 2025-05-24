package com.sola.reviewyme.resume_ai.utils;

import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;

public class ResumeParser {

    @SneakyThrows
    public static String extractText(File file) throws IOException, TikaException {
        Tika tika = new Tika();
        return tika.parseToString(file);
    }
}
