package com.studyolle.modules.account;

import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountServiceData {
	public AccountRepository accountRepository;
	public JavaMailSender javaMailSender;
	public PasswordEncoder passwordEncoder;
	public ModelMapper modelMapper;

	public AccountServiceData() {
	}
}