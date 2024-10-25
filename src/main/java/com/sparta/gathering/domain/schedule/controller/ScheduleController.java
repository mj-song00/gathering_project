package com.sparta.gathering.domain.schedule.controller;

import com.sparta.gathering.domain.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/{gatherId}")
@Tag(name = "Schedule API", description = "스케쥴 API")
public class ScheduleController {
    private final ScheduleService scheduleService;


}
