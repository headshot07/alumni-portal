package com.example.alumniportal.controllers;

import com.example.alumniportal.dto.AlumniDetailsRequestDTO;
import com.example.alumniportal.dto.AlumniDetailsResponseDTO;
import com.example.alumniportal.services.IAlumniService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    @Autowired
    private IAlumniService alumniService;

    @PostMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody ResponseEntity<AlumniDetailsResponseDTO> createOrders(@Valid @RequestBody AlumniDetailsRequestDTO alumniDetailsRequestDTO) {
        AlumniDetailsResponseDTO alumniDetailsResponseDTO = null;
        return ResponseEntity.ok(alumniDetailsResponseDTO);
    }

    @GetMapping(path = "/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody ResponseEntity<AlumniDetailsResponseDTO> createDispatchPlan() {
        AlumniDetailsResponseDTO alumniDetailsResponseDTO = null;
        return ResponseEntity.ok(alumniDetailsResponseDTO);
    }
}
