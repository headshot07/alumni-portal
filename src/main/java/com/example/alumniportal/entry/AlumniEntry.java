package com.example.alumniportal.entry;


import com.example.alumniportal.entities.AlumniEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniEntry {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String currentRole;

    @NotBlank(message = "University cannot be empty")
    private String university;

    private String location;

    private String linkedinHeadline;

    @NotNull(message = "Pass out year cannot be empty")
    private Integer passoutYear;


    @Builder(builderMethodName = "convertEntryToEntity")
    public static AlumniEntity.AlumniEntityBuilder convertEntryToEntity(AlumniEntry alumniEntry) {
        return AlumniEntity.builder()
                .name(alumniEntry.getName())
                .currentRole(alumniEntry.getCurrentRole())
                .university(alumniEntry.getUniversity())
                .location(alumniEntry.getLocation())
                .linkedinHeadline(alumniEntry.getLinkedinHeadline())
                .passoutYear(alumniEntry.getPassoutYear());
    }
}
