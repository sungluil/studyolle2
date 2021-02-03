package com.studyolle.modules.account.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.form.SignUpForm;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator{

	private final AccountRepository accountRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(SignUpForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		SignUpForm signUpForm = (SignUpForm) target;
		if(accountRepository.existsByEmail(signUpForm.getEmail())) {
			errors.rejectValue(
						"email", 
						"invalid.email", 
						new Object[] {
							signUpForm.getEmail()
						},
						"이미 사용중인 이메일입니다."
					);
		}
		if(accountRepository.existsByNickname(signUpForm.getNickname())) {
			errors.rejectValue("nickname", "invalid.nickname"
					, new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임입니다.");
		}
	}

	
}
