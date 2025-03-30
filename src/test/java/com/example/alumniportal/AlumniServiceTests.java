package com.example.alumniportal;

import com.example.alumniportal.services.IAlumniService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AlumniServiceTests {

    @InjectMocks
    private IAlumniService alumniService;
}
