package com.example.alumniportal.dto;

import com.example.alumniportal.entry.AlumniEntry;
import com.example.alumniportal.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniDetailsResponseDTO {

    ResponseStatus status;

    @Builder.Default
    List<AlumniEntry> data = new ArrayList<>();
}
