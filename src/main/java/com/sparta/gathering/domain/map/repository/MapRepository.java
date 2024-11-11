package com.sparta.gathering.domain.map.repository;

import com.sparta.gathering.domain.map.entity.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MapRepository extends JpaRepository<Map, Long> {


    @Query("SELECT l FROM Map l WHERE " +
            "(6371 * acos(cos(radians(:latitude))\n" +
            "                     * cos(radians(l.latitude))\n" +
            "                     * cos(radians(l.longitude)\n" +
            "            - radians(:longitude))\n" +
            "        + sin(radians(:latitude))\n" +
            "                     * sin(radians(l.latitude)))) < 2.5" +
            " ORDER BY " +
            " (6371 * acos(cos(radians(:latitude))\n" +
            "                     * cos(radians(l.latitude))\n" +
            "                     * cos(radians(l.longitude)\n" +
            "            - radians(:longitude))\n" +
            "        + sin(radians(:latitude))\n" +
            "                     * sin(radians(l.latitude))))")
    List<Map> findWithinBounds(@Param("longitude") double longitude,//x
                               @Param("latitude") double latitude); //y

    Optional<Map> findByGatherId(Long id);
}