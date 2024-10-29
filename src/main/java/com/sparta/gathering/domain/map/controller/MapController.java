package com.test.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 검색 API 요청을 처리하는 메인 컨트롤러입니다.
 *
 * @author 이승원
 * @since 2024.02.28.
 */
@Controller
public class MapController {

    /**
     * 검색을 요청할 페이지로 이동합니다.
     *
     * @param model 검색 결과를 뷰에 전달하기 위한 데이터 모델
     * @return 검색 결과를 표시할 뷰의 경로
     */
    @GetMapping(value = "/search")
    public String search(Model model) {
        return "user/api/search";
    }

}