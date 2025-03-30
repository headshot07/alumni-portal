package com.example.alumniportal.entities;

import com.example.alumniportal.entry.AlumniEntry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alumni")
@ToString
@EqualsAndHashCode(callSuper = true)
public class AlumniEntity extends BaseEntity  {

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String currentRole;

    @Column(nullable = false)
    private String university;

    private String location;

    private String linkedinHeadline;

    @Column(nullable = false)
    private Integer passoutYear;

    @Builder(builderMethodName = "convertEntityToEntry")
    public static AlumniEntry.AlumniEntryBuilder convertEntityToEntry(AlumniEntity alumniEntity) {
        return AlumniEntry.builder()
                .name(alumniEntity.getName())
                .currentRole(alumniEntity.getCurrentRole())
                .university(alumniEntity.getUniversity())
                .location(alumniEntity.getLocation())
                .linkedinHeadline(alumniEntity.getLinkedinHeadline())
                .passoutYear(alumniEntity.getPassoutYear());
    }
}
