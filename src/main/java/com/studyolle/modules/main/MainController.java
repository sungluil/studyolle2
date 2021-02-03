package com.studyolle.modules.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final NotificationRepository notificationRepository; 
	
	@GetMapping("/")
	public String home(@CurrentUser Account account, Model model) {
		if(account != null) {
			model.addAttribute(account);
		}
		
		//Long count = notificationRepository.countByAccountAndChecked(account, false);
		//model.addAttribute("hasNotification", count>0);
		/**
		 * 이렇게 구현시에 여기 페이지에서 여기핸들러에서만 작동 모든페이지에서
		 * 작동되게 하려면 스프링 mvc핸들러를 이용
		 */
		
		
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
}
