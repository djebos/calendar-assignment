package service;

import entity.NonWorkingDay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.NonWorkingDayRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeekdaysCalculationServiceTest {
    @Mock
    private NonWorkingDayRepository jsonRepo;
    @InjectMocks
    private WeekdaysCalculationService testedInstance;

    @Test
    void shouldReturn10WeekdaysFor2Weeks() {
        var start = LocalDate.of(2023,5,1);
        var end = LocalDate.of(2023,5,14);

        int actualWeekdays = testedInstance.getWeekdaysBetween(start, end);

        assertThat(actualWeekdays).isEqualTo(10);
    }

    @Test
    void shouldReturn8WeekdaysFor2NonFullWeeks() {
        var start = LocalDate.of(2023,5,2);
        var end = LocalDate.of(2023,5,11);

        int actualWeekdays = testedInstance.getWeekdaysBetween(start, end);

        assertThat(actualWeekdays).isEqualTo(8);
    }

    @Test
    void shouldReturn7WeekdaysFor2WeeksWith3Holidays() {
        var start = LocalDate.of(2023, 5, 1);
        var end = LocalDate.of(2023, 5, 14);
        List<NonWorkingDay> holidays = List.of(new NonWorkingDay(LocalDate.of(2023, 5, 2)),
                new NonWorkingDay(LocalDate.of(2023, 5, 5)),
                new NonWorkingDay(LocalDate.of(2023, 5, 8)));
        when(jsonRepo.getAll()).thenReturn(holidays);

        int actualWeekdays = testedInstance.getWeekdaysBetween(start, end);

        assertThat(actualWeekdays).isEqualTo(7);
    }

    @Test
    void shouldReturn8WeekdaysFor2WeeksWith3HolidaysWhenOneHolidayOverlapWithWeekend() {
        var start = LocalDate.of(2023, 5, 1);
        var end = LocalDate.of(2023, 5, 14);
        List<NonWorkingDay> holidays = List.of(new NonWorkingDay(LocalDate.of(2023, 5, 2)),
                new NonWorkingDay(LocalDate.of(2023, 5, 5)),
                new NonWorkingDay(LocalDate.of(2023, 5, 6)));
        when(jsonRepo.getAll()).thenReturn(holidays);

        int actualWeekdays = testedInstance.getWeekdaysBetween(start, end);

        assertThat(actualWeekdays).isEqualTo(8);
    }

    @Test
    void shouldReturn260WeekdaysFor1YearWithoutHolidaysInPreviousYear() {
        var start = LocalDate.of(2023, 1, 1);
        var end = LocalDate.of(2023, 12, 31);
        List<NonWorkingDay> holidays = List.of(new NonWorkingDay(LocalDate.of(2022, 5, 2)),
                new NonWorkingDay(LocalDate.of(2022, 5, 5)));
        when(jsonRepo.getAll()).thenReturn(holidays);

        int actualWeekdays = testedInstance.getWeekdaysBetween(start, end);

        assertThat(actualWeekdays).isEqualTo(260);
    }

    @Test
    void shouldThrowNPEWhenStartDateNotProvided() {
        assertThatThrownBy(() -> testedInstance.getWeekdaysBetween(null, LocalDate.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("start date must not be null");
    }

    @Test
    void shouldThrowNPEWhenEndDateNotProvided() {
        assertThatThrownBy(() -> testedInstance.getWeekdaysBetween(LocalDate.now(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("end date must not be null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStartDateIsAfterEndDate() {
            assertThatThrownBy(() -> testedInstance.getWeekdaysBetween(LocalDate.now(), LocalDate.now().minus(1, ChronoUnit.DAYS)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("start date must be before end date");
    }

}
