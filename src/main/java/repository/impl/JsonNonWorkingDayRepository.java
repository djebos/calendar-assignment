package repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import exception.DataAccessException;
import entity.NonWorkingDay;
import repository.NonWorkingDayRepository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

class JsonNonWorkingDayRepository implements NonWorkingDayRepository {
    private static final Logger LOG = Logger.getLogger(JsonNonWorkingDayRepository.class.getName());
    private final ObjectMapper mapper;
    private final File storageFile;

    public JsonNonWorkingDayRepository(ObjectMapper mapper, String storageFilePath) {
        this.mapper = mapper;
        this.storageFile = new File(storageFilePath);
        initStorage();
    }
    @Override
    public void save(NonWorkingDay nonWorkingDay) {
        try {
            List<NonWorkingDay> nonWorkingDays = mapper.readValue(storageFile, new TypeReference<>() {
            });
            nonWorkingDays.add(nonWorkingDay);
            mapper.writeValue(storageFile, nonWorkingDays);
        } catch (IOException e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<NonWorkingDay> getAll() {
        try {
            return mapper.readValue(storageFile, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new DataAccessException(e);
        }
    }

    private void initStorage() {
        if (!storageFile.exists()) {
            LOG.info("Creating storage file");
            try {
                storageFile.createNewFile();
                mapper.writeValue(storageFile, Collections.emptyList());
            } catch (IOException e) {
                LOG.severe("Couldn't initialize data storage file!");
                throw new DataAccessException(e);
            }
        }
    }
}
