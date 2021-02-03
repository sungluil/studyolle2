package com.studyolle.modules.notification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.UserAccount;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor{

	
	private final NotificationRepository notificationRepository; 
	
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("실행됨===============================================");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(modelAndView != null && !isRedirectView(modelAndView) && authentication != null && authentication.getPrincipal() instanceof UserAccount) {
			Account account = ((UserAccount) authentication.getPrincipal()).getAccount();
			Long count = notificationRepository.countByAccountAndChecked(account, false);
			System.out.println("갯수는 "+count);
			modelAndView.addObject("hasNotification", count>0);
		}

	}


	private boolean isRedirectView(ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
	}
}