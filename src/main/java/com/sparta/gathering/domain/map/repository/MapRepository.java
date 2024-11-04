package com.sparta.gathering.domain.map.repository;

import com.sparta.gathering.domain.map.entity.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MapRepository extends JpaRepository<Map, Long> {


    @Query("SELECT l FROM Map l WHERE " +
            "(6371 * acos(cos(radians(37.5110804950594))\n" +
            "                     * cos(radians(latitude))\n" +
            "                     * cos(radians(longitude)\n" +
            "            - radians(126.674801640164))\n" +
            "        + sin(radians(37.5110804950594))\n" +
            "                     * sin(radians(latitude)))) < 2" +
            " ORDER BY " +
            " (6371 * acos(cos(radians(37.5110804950594))\n" +
            "                     * cos(radians(latitude))\n" +
            "                     * cos(radians(longitude)\n" +
            "            - radians(126.674801640164))\n" +
            "        + sin(radians(37.5110804950594))\n" +
            "                     * sin(radians(latitude))))")
    List<Map> findWithinBounds(@Param("longitude") double longitude,//x
                               @Param("latitude") double latitude); //y
}