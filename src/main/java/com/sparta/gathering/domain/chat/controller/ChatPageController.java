package com.sparta.gathering.domain.chat.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatPageController {

    @GetMapping("/chat/{gatheringId}")
    public String chatPage(@PathVariable String gatheringId, Model model) {
        // 가상의 모임 이름을 설정
        String gatheringName = "Gathering " + gatheringId;
        model.addAttribute("gatheringId", gatheringId);
        model.addAttribute("gatheringName", gatheringName);
        model.addAttribute("username", "User1"); // 실제 로그인된 사용자 이름을 여기에 넣어야 함
        return "chat";
    }
}
