package com.example.fitnesstracker.repository;

import com.example.fitnesstracker.entity.ProgressPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressPhotoRepository extends JpaRepository<ProgressPhoto, Long> {
}
