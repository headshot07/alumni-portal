package com.example.alumniportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniDetailsRequestDTO {
    String university;
    String designation;
    String passoutYear;
}