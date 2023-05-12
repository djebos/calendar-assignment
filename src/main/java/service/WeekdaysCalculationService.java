package service;

import entity.NonWorkingDay;
import repository.NonWorkingDayRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class WeekdaysCalculationService {
    private static final Logger LOG = Logger.getLogger(WeekdaysCalculationService.class.getName());
    private final NonWorkingDayRepository repository;

    public WeekdaysCalculationService(NonWorkingDayRepository repository) {
        this.repository = repository;
    }

    public int getWeekdaysBetween(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start date must not be null");
        Objects.requireNonNull(end, "end date must not be null");

        if (start.isAfter(end)) {
            LOG.severe(format("Start date %s must be before end date %s", start, end));
            throw new IllegalArgumentException("start date must be before end date");
        }

        Set<LocalDate> nonWorkingDays = repository.getAll().stream().map(NonWorkingDay::value).collect(Collectors.toSet());

        return start.datesUntil(end.plus(1, ChronoUnit.DAYS))
                .filter(date -> date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY)
                .filter(date -> !nonWorkingDays.contains(date))
                .reduce(0, (integer, localDate) -> ++integer, Integer::sum);
    }
}
