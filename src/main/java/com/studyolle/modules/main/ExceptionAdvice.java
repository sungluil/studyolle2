package com.studyolle.modules.main;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

	@ExceptionHandler
	public String handleRuntimeException(@CurrentUser Account account, RuntimeException e
			, HttpServletRequest request) {
		
		if(account != null) {
			log.info(" '{}' requested '{}' ", account.getNickname(), request.getRequestURI());
		} else {
			log.info(" requested '{}' ", request.getRequestURI());
		}
		
		log.error("dad request", e);
		return "error";
	}
}
