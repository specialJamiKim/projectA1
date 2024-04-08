package com.projectA1.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.plaf.synth.SynthConstants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectA1.config.auth.PrincipalUser;
import com.projectA1.model.Reservation;
import com.projectA1.model.User;
import com.projectA1.service.DiaryService;
import com.projectA1.service.FitnessCenterService;
import com.projectA1.service.OwnerService;
import com.projectA1.service.ReservationService;
import com.projectA1.service.ReviewService;
import com.projectA1.service.UserService;
import com.projectA1.service.VisitCountingService;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/m_user/*") // 주소를 요청했을 때 모든 것에 반응
@RequiredArgsConstructor
@Log4j2
public class M_UserController {

	// 사용자 추가
	// 사용자 마이페이지 => 정보수정, 회원탈퇴

	private final UserService userService;
	private final OwnerService ownerService;
	private final VisitCountingService visitCountingService;
	private final ReservationService reservationService;
	private final FitnessCenterService fitnessCenterService;
	private final DiaryService diaryService;
	private final ReviewService reviewService;

	////////// 안드로이드 용 추가 부분 ///////////////////////
	///////////////////////////////////////////////////
	///////////////////////////////////////////////////

	// 로그인 된 인증객체 반환
	public User getAuthUser(@AuthenticationPrincipal PrincipalUser princialUser) {
		User user = (User) princialUser.getUser();
		return user;
	}

	// 사용자 수정
	@PostMapping("update")
	@Transactional
	public ResponseEntity<String> update(@RequestBody User updatedUser) {
		User user = userService.findByEmail(updatedUser.getEmail());

		// 업데이트 작업 수행
		userService.update(user, updatedUser);
		System.out.println("사용자 수정 , 성공!!");
		return ResponseEntity.ok("success");
	}

	/*
	 * // 사용자 수정
	 * 
	 * @PutMapping("update")
	 * 
	 * @Transactional public ResponseEntity<String> update(@RequestBody User
	 * updatedUser, HttpSession session) { // 세션에서 PrincipalUser 가져오기 PrincipalUser
	 * principalUser = (PrincipalUser) session.getAttribute("user"); if
	 * (principalUser != null) { // PrincipalUser에서 User 객체 가져오기 User user = (User)
	 * principalUser.getUser();
	 * 
	 * // 업데이트 작업 수행 userService.update(user, updatedUser);
	 * System.out.println("성공!!"); return ResponseEntity.ok("success"); } else { //
	 * 세션에서 사용자 정보를 가져올 수 없는 경우 에러 응답 System.out.println("세션에서 사용자 정보를 가져올 수 없음");
	 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized"); }
	 * }
	 */

	// 사용자 정보 조회
	@PostMapping("/user-info")
	public ResponseEntity<User> getUserInfo(@RequestParam String email) {
		User user = userService.findByEmail(email);
		if (user != null) {
			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 회원가입 => 아이디 중복검사(email)
	@PostMapping("/inquiryEmail")
	public ResponseEntity<User> checkEmailAvailability(@RequestParam String email) {
		User user = userService.findByEmail(email);
		if (user != null) {
			// 이미 존재하는 이메일이라면 HttpStatus.CONFLICT(409) 반환
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} else {
			// 이메일이 존재하지 않으면 HttpStatus.OK(200) 반환
			return ResponseEntity.ok().build();
		}
	}

	// 사용자 추가 => 추가 후, 로그인 페이지
	@PostMapping("join")
	public ResponseEntity<String> join(@RequestBody User user) {
		// 사용자 역할 설정
		List<String> roles = new ArrayList<>();
		roles.add("ROLE_USER");
		user.setRole(roles);

		// 사용자 추가
		userService.join(user);

		return ResponseEntity.ok("success"); // 사용자 추가 성공 시 성공 반환
	}

	// 사용자 마이페이지(상세보기) => 예약자 리스트도 표시
	// 이거 기준으로 짜면됩니다.
	@GetMapping("mypage")
	public ResponseEntity<String> viewMyPage(@AuthenticationPrincipal PrincipalUser principalUser,
			@RequestParam String email) {
		// 로그인된 사용자의 정보를 가져옵니다.
		// User user = (User) principalUser.getUser();
		User user2 = userService.findByEmail(email);

		List<Reservation> reservations = reservationService.findByUserId(user2.getId());
		// 센터 방문횟수를 가져와 화면에 표시(userid, centerid)
		long visitCount = visitCountingService.visitCounting(user2.getId());

		return ResponseEntity.ok("/user/mypage");
	}

	
	// 사용자 삭제
/*	@DeleteMapping("/deleteUser")
	@Transactional
	public ResponseEntity<Void> deleteUser(@RequestParam String email) {
		//이거 아예 새로 짜야함
		//User user = userService.findByEmail(email);
		//visitCountingService.deleteByUserId(user);
		//reviewService.deleteByUserId(user);
		userService.delete(email);
		
		return ResponseEntity.ok().build();
	}*/
	
	// 사용자 삭제
	@DeleteMapping("/deleteUser")
	@Transactional
	public ResponseEntity<Void> deleteUser(@RequestParam String email) {
	    // 사용자 이메일을 사용하여 사용자 엔티티 조회
	    User user = userService.findByEmail(email);
	    System.out.println(user.getName());
	    System.out.println(user.getEmail());
	    System.out.println(user.getId());
	    
	    
	    // 방문 기록 삭제
	    visitCountingService.deleteByUserId(user);
	    System.out.println("1번");   
	    // 리뷰 삭제
	    reviewService.deleteByUserId(user);
	    System.out.println("2번");
	    // 예약기록삭제
	    reservationService.deleteByUserId(user);
	    System.out.println("3번");
	    // 사용자 삭제
	    userService.delete(email);
	    System.out.println("4번");
	    
	    return ResponseEntity.ok().build();
	}


	
	
}