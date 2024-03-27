package com.projectA1.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity	// security 적용하려면 사용
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean	// method보다 훨씬 독립적, 별도의 객체로 뽑아서 진행
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}

	@Bean	// SecurityFilterChain - 로그인했을 때 어떻게 처리하겠다는 것.
	public SecurityFilterChain fileChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(request -> request
						.requestMatchers("/m_user/**","/", "/img/**", "/src/main/**", "/main","/updatePassword", "/join/**", "/login/**", "/centerManage/**", "/diary/**","/test/**",
								"/login/**", "/user/join", "/owner/join", "/fragments/*")
						.permitAll()
						.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
						.requestMatchers("/img/**").permitAll()
						.requestMatchers("/user/**").hasRole("USER")
						.requestMatchers("/owner/**").hasRole("OWNER")
						.anyRequest().authenticated())

				.formLogin(login -> login.loginPage("/login/loginPage") // 로그인 페이지 URL 설정
						.loginProcessingUrl("/loginPro") // 로그인 처리 URL 설정
						.defaultSuccessUrl("/", true) // 로그인 성공 후 리다이렉트할 경로 설정
						.permitAll())

				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/") // 로그아웃 후 리다이렉트할 경로 설정
						.invalidateHttpSession(true) // 세션 무효화
						.deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
						.permitAll());

		return http.build();

	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
