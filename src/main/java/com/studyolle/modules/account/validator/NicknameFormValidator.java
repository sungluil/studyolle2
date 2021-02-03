package com.studyolle.modules.account.validator;

import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.form.NicknameForm;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class NicknameFormValidator implements Validator{

	private final AccountRepository accountRepository; 
	
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return NicknameForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		//닉네임변경은 둘이 비교하는게아니라 db에 있는 닉네임과 일치하는게없는지 있는지확인
		NicknameForm nicknameForm = (NicknameForm) target;
		Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
		if(byNickname != null) {
			errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임은 사용할 수 없습니다.");
		}
	}

	
}
