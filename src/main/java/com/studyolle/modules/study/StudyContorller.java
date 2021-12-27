package com.studyolle.modules.study;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.UserAccount;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;
import com.studyolle.modules.notification.NotificationService;
import com.studyolle.modules.study.form.StudyForm;
import com.studyolle.modules.study.validator.StudyFormValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudyContorller {

	private final StudyService studyService;
	private final ModelMapper modelMapper; 
	private final StudyFormValidator studyFormValidator; 
	private final StudyRepository studyRepository;  
	private final NotificationRepository notificationRepository; 
	private final NotificationService notificationService;
	
	@InitBinder("studyForm")
	public void studyFormInitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(studyFormValidator);
	}
	
	
	@GetMapping("/new-study")
	public String newStudyForm(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new StudyForm());
		return "study/form";
	}
	
	@PostMapping("/new-study")
	public String newStudySubmit(@CurrentUser Account account, 
			@Valid StudyForm studyForm, Errors errors, Model model) {
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "study/form";
		}
		
//		studyFormValidator.validate(studyForm, errors);
//		if(errors.hasErrors()) {
//			return "study/form"; 
//		} //@InitBinder("studyForm") 대체함

		Study newStudy = studyService.createNewStudy(modelMapper.map(studyForm, Study.class), account);
		System.out.println(newStudy.getManagers());
		
		

		
//		model.addAttribute(attributeValue);
		return "redirect:/study/"+URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8); 
	}

	@GetMapping("/study/{path}")
	public String viewStudy(@CurrentUser Account account ,@PathVariable String path, Model model) {
//		Study study = studyRepository.findByPath(path);
//		if(study == null) {
//			throw new IllegalArgumentException(path+"에 해당하는 스터디가 없습니다.");
//		}
		
		Study study = studyService.getStudy(path);
		//String notiPath = "/study/"+path;
		//Notification notification = notificationRepository.findByLink(notiPath);
		//notificationService.check(notification);
		model.addAttribute(account);
		model.addAttribute(study);
		return "study/view";
	}
	
//	@GetMapping("/study/{path}/true")
//	public String viewTrueStudy(@CurrentUser Account account ,@PathVariable String path, Model model) {
//		
//		Study study = studyService.getStudy(path);
//		String notiPath = "/study/"+path;
//		Notification notification = notificationRepository.findByLink(notiPath);
//		notificationService.check(notification);
//		model.addAttribute(account);
//		model.addAttribute(study);
//		return "study/view";
//	}
	
	@GetMapping("/study/{path}/members")
	public String viewStudyMembers(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudy(path);
		model.addAttribute(account);
		model.addAttribute(study);
//		model.addAttribute(studyRepository.findByPath(path));
		return "study/members";
	}
	
	
	@GetMapping("/study/{path}/join")
	public String studyJoin(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudy(path);
		model.addAttribute(account);
		model.addAttribute(study);
		studyService.addMember(study,account);
		return "study/members";
	}
	
	@GetMapping("/study/{path}/leave")
	public String studyLeave(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudy(path);
		model.addAttribute(account);
		model.addAttribute(study);
		studyService.leaveMember(study,account);
		return "study/members";
	}
	
	
	
	
	
	
	
	@GetMapping("/test")
	public String test(@CurrentUser Account account) {
		return "study/test";
	}
	
	
	@GetMapping("/study/data")
	public String testData(@CurrentUser Account account) {
		studyService.generatedStudy(account);
		return "redirect:/";
	}
}


















