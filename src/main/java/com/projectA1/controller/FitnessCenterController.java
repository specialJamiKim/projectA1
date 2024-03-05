package com.projectA1.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.projectA1.config.auth.PrincipalUser;
import com.projectA1.model.FitnessCenter;
import com.projectA1.model.Owner;
import com.projectA1.service.FitnessCenterService;
import com.projectA1.service.OwnerService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/centerManage/*")
@RequiredArgsConstructor
public class FitnessCenterController {
	
	private final FitnessCenterService fitnessCenterService;
	private final OwnerService ownerService;
	
    //center 등록 폼
    @GetMapping("joinForm")
    public String CenterJoinForm() {
    	return "/center/centerJoin";
    }
	
	// 피트니스 센터 추가 (사장님에 center추가, 센터 추가)
    @PostMapping("register")
    @ResponseBody
    public String join(@AuthenticationPrincipal PrincipalUser principalUser, @RequestBody FitnessCenter fitnessCenter) {	   	
    	fitnessCenterService.join(fitnessCenter);
    	Owner owner = (Owner) principalUser.getUser();
    	//현재 owner에 센터아이디 등록
    	ownerService.addFitnessCenterToOwner(owner,fitnessCenter);
        return "success";
    }

	//수정폼
    @GetMapping("update")
    public String update(@AuthenticationPrincipal PrincipalUser principalUser, Model model) {
    	Owner owner = (Owner) principalUser.getUser();
    	Long id = owner.getFitnessCenter().getId();	
        model.addAttribute("center", fitnessCenterService.view(id));
        return "/center/updateForm";
    }
	        
	//수정
    @PostMapping("update")
    @ResponseBody
    public String updateFitnessCenter(@AuthenticationPrincipal PrincipalUser principalUser, @RequestBody FitnessCenter fitnessCenter) {
    	fitnessCenterService.update(fitnessCenter);
        return "redirect:/owner/ownerpage";
    }
	
	//삭제
    @GetMapping("delete")
    public String delete(@AuthenticationPrincipal PrincipalUser principalUser) {
    	// 고유 아이디로 삭제처리
    	Owner owner = (Owner) principalUser.getUser();
    	Long id = owner.getFitnessCenter().getId();

    	//센터아이디 삭제
    	ownerService.clearCenterId(owner);    	    	
    	fitnessCenterService.deleteFitnessCenter(id);
        return "redirect:/";
    }
	
	// 피트니스 센터 상세보기
    @GetMapping("view/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("fitnessCenter", fitnessCenterService.view(id));
        return "center/gymview";
    }
    
	//전체보기
    @GetMapping("gymlist")
    public String getAllFitnessCenters(Model model) {
        model.addAttribute("fitnessCenters", fitnessCenterService.viewAll());
        return "/center/gymlist";
    }
}
