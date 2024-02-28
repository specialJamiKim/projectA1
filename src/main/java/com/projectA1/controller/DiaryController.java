package com.projectA1.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.projectA1.model.Diary;
import com.projectA1.service.DiaryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/diary/*")
@RequiredArgsConstructor
public class DiaryController {
	
	private final DiaryService diaryService;
	
	@GetMapping("diaryList")
	public String list(Model model) {
		// 로그인된 사용자의 정보를 가져옵니다.
		List<Diary> list=diaryService.findAll();
		model.addAttribute("diarys", list);
		return "/diary/diaryList";
	}
}
