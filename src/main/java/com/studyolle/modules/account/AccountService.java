package com.studyolle.modules.account;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.studyolle.infra.config.AppProperties;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import com.studyolle.modules.account.form.Notifications;
import com.studyolle.modules.account.form.Profile;
import com.studyolle.modules.account.form.SignUpForm;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {
	
	private final AccountRepository accountRepository;
	private final EmailService emailService; 
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender javaMailSender;
	private final ModelMapper modelMapper;
	private final TemplateEngine templateEngine;
	private final AppProperties appProperties;
 

	public Account processNewAccount(SignUpForm signUpForm) {
		Account newAccount = saveNewAccount(signUpForm);
//		newAccount.generateEmailCheckToken();
		sendSignUpConfirmEmail(newAccount);
		return newAccount;
	}
	
	private Account saveNewAccount(@Valid SignUpForm signUpForm) {
		signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
		Account account = modelMapper.map(signUpForm, Account.class);
		account.generateEmailCheckToken();
		
//		Account account = Account.builder()
//				.email(signUpForm.getEmail())
//				.nickname(signUpForm.getNickname())
//				.password(passwordEncoder.encode(signUpForm.getPassword()))
//				.studyCreatedByWeb(true)
//				.studyEnrollmentResultByWeb(true)
//				.studyUpdatedByWeb(true)
//				.build();
		return accountRepository.save(account);
		
	}

	public void sendSignUpConfirmEmail(Account newAccount) {
		
		Context context = new Context();
		context.setVariable("link", "/check-email-token?token="+newAccount.getEmailCheckToken()+"&email="+newAccount.getEmail());
		context.setVariable("nickname", newAccount.getNickname());
		context.setVariable("linkName", "이메일 인증하기");
		context.setVariable("message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요.");
		context.setVariable("host", appProperties.getHost());
		
		String message = templateEngine.process("mail/simple-link", context);
		
		EmailMessage emailMessage = EmailMessage.builder()
				.to(newAccount.getEmail())
				.subject("스터디올래, 회원 가입 인증")
				.message(message)
				.build();
		emailService.sendEmail(emailMessage);
		
		
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setTo(newAccount.getEmail());
//		mailMessage.setSubject("스터디올래, 회원 가입 인증");
//		mailMessage.setText("/check-email-token?token="+newAccount.getEmailCheckToken() +
//				"&email="+newAccount.getEmail());
//		javaMailSender.send(mailMessage);
		
	}

	public void login(Account account) {
		// TODO Auto-generated method stub	
		UsernamePasswordAuthenticationToken token = 
				new UsernamePasswordAuthenticationToken(
						new UserAccount(account), 
						account.getPassword(),
						List.of(new SimpleGrantedAuthority("ROLE_USER")));
		SecurityContextHolder.getContext().setAuthentication(token);
		//UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account.getNickname(), account.getPassword());
		//Authentication authentication = authenticationManager.authenticate(token);
		//SecurityContext context = SecurityContextHolder.getContext();
		//context.setAuthentication(token);
		
	}

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		log.info("emailOrNickname = "+emailOrNickname);
		Account account = accountRepository.findByEmail(emailOrNickname);
		if(account == null) {
			account = accountRepository.findByNickname(emailOrNickname);
		}
		
		if(account == null) {
			throw new UsernameNotFoundException(emailOrNickname);
		}
		
		return new UserAccount(account);
	}

	public void completeSignUp(Account account) {
		account.completeSignUp();
		login(account);
	}

	public void updateProfile(Account account, @Valid Profile profile) {
		modelMapper.map(profile, account);
		//account.setUrl(profile.getUrl());
		//account.setOccupation(profile.getOccupation());
		//account.setLocation(profile.getLocation());
		//account.setBio(profile.getBio());
		//account.setProfileImage(profile.getProfileImage());
		accountRepository.save(account);
		
	}

	public void updatePassword(Account account, String newPassword) {
		account.setPassword(passwordEncoder.encode(newPassword));
		accountRepository.save(account);
	}

	public void updateNotifications(Account account, Notifications notifications) {
		modelMapper.map(notifications, account);
//		account.setStudyCreatedByEmail(notifications.isStudyCreatedByEmail());
//		account.setStudyCreatedByWeb(notifications.isStudyCreatedByWeb());
//		account.setStudyEnrollmentResultByEmail(notifications.isStudyEnrollmentResultByEmail());
//		account.setStudyEnrollmentResultByWeb(notifications.isStudyEnrollmentResultByWeb());
//		account.setStudyUpdatedByEmail(notifications.isStudyUpdatedByEmail());
//		account.setStudyUpdatedByWeb(notifications.isStudyUpdatedByWeb());
		accountRepository.save(account);
	}

	public void accountUpdate(Account account, String nickname) {
		// TODO Auto-generated method stub
		account.setNickname(nickname);
		accountRepository.save(account);
		login(account);
	}

	public void sendLoginLink(Account newAccount) {
		// TODO Auto-generated method stub
		Context context = new Context();
		context.setVariable("link", "/login-email-token?token="+newAccount.getEmailCheckToken()+"&email="+newAccount.getEmail());
		context.setVariable("nickname", newAccount.getNickname());
		context.setVariable("linkName", "스터디올래 로그인하기");
		context.setVariable("message", "로그인하려면 아래 링크를 클릭하세요.");
		context.setVariable("host", appProperties.getHost());
		String message = templateEngine.process("mail/simple-link", context);
		
		EmailMessage emailMessage = EmailMessage.builder()
				.to(newAccount.getEmail())
				.subject("스터디올래, 로그인 링크")
				.message(message)
				.build();
		emailService.sendEmail(emailMessage);
		
		
		
//		newAccount.generateEmailCheckToken();//토큰 새로생성
//		SimpleMailMessage mailMessage = new SimpleMailMessage();
//		mailMessage.setTo(newAccount.getEmail());
//		mailMessage.setSubject("스터디올래, 이메일 로그인 안내");
//		mailMessage.setText("/login-email-token?token="+newAccount.getEmailCheckToken() +
//				"&email="+newAccount.getEmail());
//		javaMailSender.send(mailMessage);
		
	}

	public void addTag(Account account, Tag tag) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getTags().add(tag));
	}

	public Set<Tag> getTags(Account account) {
		// TODO Auto-generated method stub
		Optional<Account> byId = accountRepository.findById(account.getId());
		return byId.orElseThrow().getTags();
	}

	public void removeTag(Account account, Tag tag) {
		// TODO Auto-generated method stub
		Account tags = accountRepository.findById(account.getId()).orElse(null);
		if(tags != null) {
			tags.getTags().remove(tag);
		}
//		tags.ifPresent(a -> {
//			a.getTags().remove(tag);
//		});
	}

    public Set<Zone> getZones(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getZones();
    }

	public void addZone(Account account, Zone zone) {
		Optional<Account> byId = accountRepository.findById(account.getId());
		byId.ifPresent(a -> a.getZones().add(zone));
	}
	
	public void removeZone(Account account, Zone zone) {
		Account zones = accountRepository.findById(account.getId()).orElse(null);
		if(zones != null) {
			zones.getZones().remove(zone);
		}
	}

	public Account getAccount(String nickname) {
		Account account = accountRepository.findByNickname(nickname);
		if(nickname == null) {
			throw new IllegalArgumentException(nickname+"에 해당하는 사용자가 없습니다.");
		}
		return account;
	}


}
