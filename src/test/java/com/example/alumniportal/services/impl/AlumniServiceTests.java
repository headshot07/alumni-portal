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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlumniServiceTests {

    @InjectMocks
    private AlumniService alumniService;

    @Mock
    private AlumniDao alumniDao;

    @Mock
    private PhantomBusterFacade phantomBusterFacade;

    private List<AlumniEntry> getAlumniEntryList() {
        return List.of(
                AlumniEntry.builder()
                        .name("John Doe")
                        .currentRole("Software Engineer")
                        .university("University of XYZ")
                        .location("New York, NY")
                        .linkedinHeadline("Passionate Software Engineer at XYZ Corp")
                        .passoutYear(2020)
                        .build(),
                AlumniEntry.builder()
                        .name("Jane Smith")
                        .currentRole("Data Scientist")
                        .university("University of XYZ")
                        .location("San Francisco, CA")
                        .linkedinHeadline("Data Scientist | AI Enthusiast")
                        .passoutYear(2019)
                        .build()
        );
    }

    private AlumniDetailsRequestDTO getAlumniDetailsRequestDTO() {
        return AlumniDetailsRequestDTO.builder()
                .university("University of XYZ")
                .designation("Software Engineer")
                .passoutYear("2020")
                .build();
    }

    private List<AlumniEntity> getAlumniEntityListFromEntryList(List<AlumniEntry> alumniEntries) {
        List<AlumniEntity> alumniEntities = new ArrayList<>();
        for (AlumniEntry alumniEntry : alumniEntries) {
            alumniEntities.add(AlumniEntry.convertEntryToEntity(alumniEntry).build());
        }
        return alumniEntities;
    }

    @Test
    void testFetchAllAlumniDetails_Success() {
        List<AlumniEntry> alumniEntries = getAlumniEntryList();
        List<AlumniEntity> alumniEntities = getAlumniEntityListFromEntryList(alumniEntries);

        doReturn(alumniEntities).when(alumniDao).findAll();

        AlumniDetailsResponseDTO responseDTO = alumniService.fetchAllAlumniDetails();
        assertEquals(ResponseStatus.SUCCESS, responseDTO.getStatus());
        assertEquals(alumniEntries.size(), responseDTO.getData().size());
        assertEquals(alumniEntries.get(0).getName(), responseDTO.getData().get(0).getName());
        assertEquals(alumniEntries.get(1).getName(), responseDTO.getData().get(1).getName());

        verify(alumniDao, times(1)).findAll();
    }

    @Test
    void testFetchAllAlumniDetails_EmptyList() {
        doReturn(new ArrayList<>()).when(alumniDao).findAll();
        AlumniDetailsResponseDTO responseDTO = alumniService.fetchAllAlumniDetails();
        assertEquals(ResponseStatus.SUCCESS, responseDTO.getStatus());
        assertEquals(0, responseDTO.getData().size());
        verify(alumniDao, times(1)).findAll();
    }

    @Test
    void testFetchAllAlumniDetails_DatabaseError() {
        doThrow(new DataAccessResourceFailureException("Database error")).when(alumniDao).findAll();
        assertThatThrownBy(() -> alumniService.fetchAllAlumniDetails())
                .isInstanceOf(AlumniProcessingException.class)
                .hasMessage("Error while fetching alumni records: Database error");
        verify(alumniDao, times(1)).findAll();
    }

    @Test
    void testFetchAllAlumniDetails_ThrowsRunTimeException() {
        doThrow(new RuntimeException("Unexpected error occurred")).when(alumniDao).findAll();
        assertThatThrownBy(() -> alumniService.fetchAllAlumniDetails())
                .isInstanceOf(AlumniProcessingException.class)
                .hasMessage("An unexpected error occurred while processing alumni data: Unexpected error occurred");

        verify(alumniDao, times(1)).findAll();
    }

    @Test
    void testSearchForAlumni_SuccessWithSave() {
        List<AlumniEntry> alumniEntries = getAlumniEntryList();
        AlumniDetailsRequestDTO requestDTO = getAlumniDetailsRequestDTO();
        AlumniDetailsResponseDTO responseDTO = AlumniDetailsResponseDTO.builder()
                .status(ResponseStatus.SUCCESS)
                .data(alumniEntries)
                .build();

        doReturn(responseDTO).when(phantomBusterFacade).fetchAlumniDetailsFromPhantomBuster(requestDTO);

        AlumniDetailsResponseDTO alumniDetailsResponseDTO = alumniService.searchForAlumni(requestDTO);
        assertEquals(ResponseStatus.SUCCESS, alumniDetailsResponseDTO.getStatus());
        assertEquals(alumniEntries.size(), alumniDetailsResponseDTO.getData().size());
        assertEquals(alumniEntries.get(0).getName(), alumniDetailsResponseDTO.getData().get(0).getName());
        assertEquals(alumniEntries.get(1).getName(), alumniDetailsResponseDTO.getData().get(1).getName());
        verify(alumniDao, times(1)).saveAll(anyList());
    }

    @Test
    void testSearchForAlumni_FetchError() {
        AlumniDetailsRequestDTO requestDTO = getAlumniDetailsRequestDTO();

        doThrow(new PhantomBusterException("Failed to fetch data"))
                .when(phantomBusterFacade)
                .fetchAlumniDetailsFromPhantomBuster(requestDTO);

        assertThatThrownBy(()-> alumniService.searchForAlumni(requestDTO))
                .isInstanceOf(AlumniProcessingException.class)
                        .hasMessage(  "Error occurred while searching alumni records from Phantom Buster: Failed to fetch data");

        verify(alumniDao, never()).saveAll(anyList());
    }

    @Test
    void testSearchForAlumni_SaveError() {
        List<AlumniEntry> alumniEntries = getAlumniEntryList();
        AlumniDetailsRequestDTO requestDTO = getAlumniDetailsRequestDTO();
        AlumniDetailsResponseDTO responseDTO = AlumniDetailsResponseDTO.builder()
                .status(ResponseStatus.SUCCESS)
                .data(alumniEntries)
                .build();

        doReturn(responseDTO).when(phantomBusterFacade).fetchAlumniDetailsFromPhantomBuster(requestDTO);
        doThrow(new DataIntegrityViolationException("Database error")).when(alumniDao).saveAll(anyList());

        assertThatThrownBy(() -> alumniService.searchForAlumni(requestDTO))
                .isInstanceOf(AlumniProcessingException.class)
                .hasMessageContaining("Database error");

        verify(alumniDao, times(1)).saveAll(anyList());
    }

    @Test
    void testSearchForAlumni_UnexpectedError() {
        List<AlumniEntry> alumniEntries = getAlumniEntryList();
        AlumniDetailsRequestDTO requestDTO = getAlumniDetailsRequestDTO();
        AlumniDetailsResponseDTO responseDTO = AlumniDetailsResponseDTO.builder()
                .status(ResponseStatus.SUCCESS)
                .data(alumniEntries)
                .build();

        doReturn(responseDTO).when(phantomBusterFacade).fetchAlumniDetailsFromPhantomBuster(requestDTO);
        doThrow(new RuntimeException("Runtime exception")).when(alumniDao).saveAll(anyList());

        assertThatThrownBy(() -> alumniService.searchForAlumni(requestDTO))
                .isInstanceOf(AlumniProcessingException.class)
                .hasMessageContaining("Runtime exception");

        verify(alumniDao, times(1)).saveAll(anyList());
    }
}
