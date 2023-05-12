package testutils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.NonWorkingDay;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public final class JsonUtil {
    private JsonUtil() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static List<NonWorkingDay> readFromFile(String fileName) {
        try {
            return MAPPER.readValue(new File(fileName), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeToFile(String fileName, List<NonWorkingDay> nonWorkingDays) {
        try {
            MAPPER.writeValue(new File(fileName), nonWorkingDays);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
