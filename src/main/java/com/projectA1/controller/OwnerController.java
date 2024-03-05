package com.projectA1.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.projectA1.config.auth.PrincipalUser;
import com.projectA1.model.FitnessCenter;
import com.projectA1.model.Owner;
import com.projectA1.model.User;
import com.projectA1.service.FitnessCenterService;
import com.projectA1.service.OwnerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/owner/*")
@RequiredArgsConstructor
public class OwnerController {

	// 오너 추가
	// 오너 마이페이지 => 정보수정, 회원탈퇴

	private final OwnerService ownerService;
	private final FitnessCenterService fitnessCenterService;

	// 오너 마이페이지
	@GetMapping("ownerpage")

	public String ownerPage(@AuthenticationPrincipal PrincipalUser principalUser, Model model) {
		// 사용자 정보를 통해 해당 사용자가 관리하는 센터의 이름을 조회하여 모델에 추가합니다.
		Owner owner = (Owner) principalUser.getUser();
		FitnessCenter fitnessCenter = owner.getFitnessCenter();
		model.addAttribute("center", fitnessCenter);


		return "/owner/ownerpage";
	}

	// 오너 추가폼 변경완료
	@GetMapping("join")
	public String join() {
		return "/owner/join";
	}

	// 오너 정보추가 => 추가 후, 로그인 페이지
	@PostMapping("join")
	@ResponseBody
	public String join(@RequestBody Owner owner) {
		List<String> roles = new ArrayList<>();
		roles.add("ROLE_OWNER");
		owner.setRole(roles);
		System.out.println("111");
		ownerService.join(owner);
		return "success"; // 페이지 수정 필요
	}

	// 오너 마이페이지(상세보기)
	@GetMapping("view/{id}")
	public String view(@AuthenticationPrincipal PrincipalUser principalUser, Model model) {
		Owner owner = (Owner) principalUser.getUser();
		model.addAttribute("owner", ownerService.view(owner.getId()));
		return "/owner/view";
	}

	// 오너 정보수정폼
	@GetMapping("updateForm")
	public String update(@AuthenticationPrincipal PrincipalUser principalUser, Model model) {
		Owner owner = (Owner) principalUser.getUser();
		model.addAttribute("owner", ownerService.view(owner.getId()));
		return "/owner/updateForm";
	}

	// 오너 정보수정
	@PostMapping("update")
	@ResponseBody
	public String update(@AuthenticationPrincipal PrincipalUser principalUser, @RequestBody Owner owner) {
		Owner currentOwner = (Owner) principalUser.getUser();
		ownerService.update(currentOwner, owner);
		return "success";
	}

	// 오너 회원탈퇴
	@GetMapping("delete")
	public String delete(@AuthenticationPrincipal PrincipalUser principalUser, HttpServletRequest request,
			HttpServletResponse response) {
		Owner owner = (Owner) principalUser.getUser();
		ownerService.delete(owner.getId());
		// 세션 무효화
		invalidateSession(request);
		return "redirect:/";
	}

	// 세션 무효화
	private void invalidateSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}
