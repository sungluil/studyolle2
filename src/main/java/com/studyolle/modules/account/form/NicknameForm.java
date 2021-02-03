package com.studyolle.modules.account.form;



import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.studyolle.modules.account.Account;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor
public class NicknameForm {

	@NotBlank
	@Length(min = 3, max = 20)
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
	private String nickname;

//	public NicknameForm(Account account) {
//		this.nickname = account.getNickname();
//	}
	
	
}
