package com.example.alumniportal.services;

import com.example.alumniportal.dto.AlumniDetailsRequestDTO;
import com.example.alumniportal.dto.AlumniDetailsResponseDTO;

public interface IAlumniService {
    AlumniDetailsResponseDTO fetchAllAlumniDetails();
    AlumniDetailsResponseDTO searchForAlumni(AlumniDetailsRequestDTO alumniDetailsRequestDTO);
}
