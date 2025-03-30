package com.example.alumniportal.services.impl;

import com.example.alumniportal.dto.AlumniDetailsRequestDTO;
import com.example.alumniportal.dto.AlumniDetailsResponseDTO;
import com.example.alumniportal.entities.AlumniEntity;
import com.example.alumniportal.entry.AlumniEntry;
import com.example.alumniportal.enums.ResponseStatus;
import com.example.alumniportal.exceptions.AlumniProcessingException;
import com.example.alumniportal.exceptions.PhantomBusterException;
import com.example.alumniportal.facades.PhantomBusterFacade;
import com.example.alumniportal.repositories.AlumniDao;
import com.example.alumniportal.services.IAlumniService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlumniService implements IAlumniService {

    @Autowired
    private AlumniDao alumniDao;

    @Autowired
    private PhantomBusterFacade phantomBusterFacade;

    @Override
    public AlumniDetailsResponseDTO fetchAllAlumniDetails() {
        try {
            List<AlumniEntry> alumniEntries = alumniDao.findAll().parallelStream()
                    .map(alumni -> AlumniEntity.convertEntityToEntry(alumni)
                            .build())
                    .toList();
            return buildResponse(ResponseStatus.SUCCESS, alumniEntries);
        } catch (DataAccessException e) {
            log.error("Database error while fetching alumni records: {}", e.getMessage(), e);
            throw new AlumniProcessingException("Error while fetching alumni records: " + e.getMessage());
        }catch (Exception e) {
            log.error("Unexpected error while fetching alumni records: {}", e.getMessage(), e);
            throw new AlumniProcessingException("An unexpected error occurred while processing alumni data: " + e.getMessage());
        }
    }

    @Override
    public AlumniDetailsResponseDTO searchForAlumni(AlumniDetailsRequestDTO alumniDetailsRequestDTO) {
        try {
            AlumniDetailsResponseDTO alumniDetailsResponseDTO = phantomBusterFacade.fetchAlumniDetailsFromPhantomBuster(alumniDetailsRequestDTO);
            if (!alumniDetailsResponseDTO.getData().isEmpty()) {
                saveAlumniData(alumniDetailsResponseDTO.getData());
            }
            return alumniDetailsResponseDTO;
        }catch (PhantomBusterException e) {
            log.error("Error while searching alumni details from Phantom Buster: {}", e.getMessage(), e);
            throw new AlumniProcessingException("Error occurred while searching alumni records from Phantom Buster: " + e.getMessage());

        } catch (DataAccessException e) {
            log.error("Database error while saving alumni records: {}", e.getMessage(), e);
            throw new AlumniProcessingException("Error occurred while saving alumni records to the database: " + e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error while searching for alumni: {}", e.getMessage(), e);
            throw new AlumniProcessingException("An unexpected error occurred while processing alumni data: " + e.getMessage());
        }
    }

    private void saveAlumniData(List<AlumniEntry> alumniEntries) {
        List<AlumniEntity> alumniEntities = alumniEntries.stream()
                .map(alumniEntry -> AlumniEntry.convertEntryToEntity(alumniEntry).build())
                .collect(Collectors.toList());
        alumniDao.saveAll(alumniEntities);
    }

    private AlumniDetailsResponseDTO buildResponse(ResponseStatus status, List<AlumniEntry> data) {
        return AlumniDetailsResponseDTO.builder()
                .status(status)
                .data(data)
                .build();
    }
}
