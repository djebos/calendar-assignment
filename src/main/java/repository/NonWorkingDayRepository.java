package repository;

import entity.NonWorkingDay;

import java.util.List;

public interface NonWorkingDayRepository {

    void save(NonWorkingDay nonWorkingDay);

    List<NonWorkingDay> getAll();
}
