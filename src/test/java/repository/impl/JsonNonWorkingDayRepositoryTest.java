package repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.NonWorkingDay;
import exception.DataAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testutils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class JsonNonWorkingDayRepositoryTest {
    private static final String STORAGE_FILE_PATH = "test-storage.json";

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private JsonNonWorkingDayRepository testedInstance;

    @BeforeEach
    void setUp() {
        testedInstance = new JsonNonWorkingDayRepository(objectMapper, STORAGE_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(STORAGE_FILE_PATH));
    }

    @Test
    void shouldSaveNonWorkingDays() {
        var nonWorkingDay = new NonWorkingDay(LocalDate.of(2023, 5,11));
        var weekendHoliday = new NonWorkingDay(LocalDate.of(2023, 5,6));

        testedInstance.save(nonWorkingDay);
        testedInstance.save(weekendHoliday);

        List<NonWorkingDay> nonWorkingDays = JsonUtil.readFromFile(STORAGE_FILE_PATH);
        assertThat(nonWorkingDays).hasSize(2)
                .containsExactly(nonWorkingDay, weekendHoliday);
    }

    @Test
    void shouldThrowDataAccessExceptionOnSaveWhenFailedToParseStorageFile() throws IOException {
        IOException parsingException = new IOException();
        doThrow(parsingException).when(objectMapper).readValue(any(File.class), any(TypeReference.class));
        var nonWorkingDay = new NonWorkingDay(LocalDate.of(2023, 1,1));

        assertThatThrownBy(() -> testedInstance.save(nonWorkingDay))
                .isInstanceOf(DataAccessException.class)
                .hasCause(parsingException);
    }

    @Test
    void shouldFetch0NonWorkingDaysWhenNoneStored() {
        List<NonWorkingDay> actualNonWorkingDays = testedInstance.getAll();

        assertThat(actualNonWorkingDays).hasSize(0);
    }

    @Test
    void shouldFetch2NonWorkingDays() {
        var newYear = new NonWorkingDay(LocalDate.of(2023, 1,1));
        var extraHoliday = new NonWorkingDay(LocalDate.of(2023, 1,2));
        JsonUtil.writeToFile(STORAGE_FILE_PATH, List.of(newYear, extraHoliday));

        List<NonWorkingDay> actualNonWorkingDays = testedInstance.getAll();

        assertThat(actualNonWorkingDays)
                .hasSize(2)
                .containsExactly(newYear, extraHoliday);
    }

    @Test
    void shouldThrowDataAccessExceptionOnGettingAllWhenFailedToParseStorageFile() throws IOException {
        IOException parsingException = new IOException();
        doThrow(parsingException).when(objectMapper).readValue(any(File.class), any(TypeReference.class));

        assertThatThrownBy(() -> testedInstance.getAll())
                .isInstanceOf(DataAccessException.class)
                .hasCause(parsingException);
    }
}
