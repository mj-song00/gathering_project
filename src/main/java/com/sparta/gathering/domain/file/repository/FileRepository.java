package com.sparta.gathering.domain.file.repository;

import com.sparta.gathering.domain.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByGatherId(Long gatherId);
}
