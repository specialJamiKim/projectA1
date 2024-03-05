package com.projectA1.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.projectA1.config.auth.PrincipalUser;
import com.projectA1.model.Diary;
import com.projectA1.model.User;
import com.projectA1.service.DiaryService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/diary/*")
@RequiredArgsConstructor
public class DiaryController {

	private final DiaryService diaryService;

	//전체보기
	@GetMapping("diaryList")
	public String list(@AuthenticationPrincipal PrincipalUser principalUser, Model model) {
		User user  = (User) principalUser.getUser();
		// 로그인된 사용자의 정보를 가져옵니다.
		Long userId = user.getId();
		List<Diary> list = diaryService.findAllByUserId(userId);
		model.addAttribute("diarys", list);
		return "/diary/diaryList";		
	}

	@GetMapping("diaryNew")
	public String addDiary() {
		return "/diary/diaryNew";
	}

	//기록 추가
	@PostMapping("addDiary")
	public String addDiary(Diary diary, @AuthenticationPrincipal PrincipalUser principal) {
		User user = (User) principal.getUser();
		diary.setUser(user);
		diaryService.addDiary(diary);
		return "redirect:/diary/diaryList";
	}
	
	//상세보기
	

}
