package com.example.alumniportal.repositories;

import com.example.alumniportal.entities.AlumniEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlumniDao  extends JpaRepository<AlumniEntity, Long> {
}
