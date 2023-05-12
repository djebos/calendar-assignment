package entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public record NonWorkingDay(LocalDate value) {
    @JsonCreator
    public NonWorkingDay(@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") @JsonProperty("value") LocalDate value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonWorkingDay that = (NonWorkingDay) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return "NonWorkingDay{" +
                "value=" + value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                '}';
    }
}
