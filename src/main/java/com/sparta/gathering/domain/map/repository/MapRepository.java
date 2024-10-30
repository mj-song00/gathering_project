package com.sparta.gathering.domain.map.repository;

import com.sparta.gathering.domain.map.entity.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository  extends JpaRepository<Map,Long> {
}
