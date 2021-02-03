package com.studyolle.modules.account;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.account.form.NicknameForm;
import com.studyolle.modules.account.form.Notifications;
import com.studyolle.modules.account.form.PasswordForm;
import com.studyolle.modules.account.form.Profile;
import com.studyolle.modules.account.form.ZoneForm;
import com.studyolle.modules.account.validator.NicknameFormValidator;
import com.studyolle.modules.account.validator.PasswordFormValidator;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.tag.TagForm;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.zone.ZoneRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SettingController {

	
	public final static String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
	public final static String SETTINGS_PROFILE_URL = "/settings/profile";

	private final AccountService accountService;
	private final ModelMapper modelMapper;
	private final NicknameFormValidator nicknameFormValidator;
	private final TagRepository tagRepository;
	private final ObjectMapper objectMapper;
	private final ZoneRepository zoneRepository; 
	
	@InitBinder("passwordForm")
	public void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new PasswordFormValidator());
	}
	@InitBinder("nicknameForm")
	public void initBinder2(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(nicknameFormValidator);
	}
	
	@GetMapping("/settings/profile")
	public String profileUpdatePage(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(modelMapper.map(account, Profile.class));
//		model.addAttribute(new Profile(account));
		return SETTINGS_PROFILE_VIEW_NAME;
	}
	
	@PostMapping("/settings/profile")
	public String updateProfileForm(@CurrentUser Account account,
			@Valid Profile profile,Errors errors, Model model,
			RedirectAttributes attributes) {

		if(errors.hasErrors()) {
			model.addAttribute(account);
			return SETTINGS_PROFILE_VIEW_NAME;
		}
		
		accountService.updateProfile(account, profile);
		attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
		return "redirect:" + SETTINGS_PROFILE_URL;
	}
	
	@GetMapping("/settings/password")
	public String passwordUpdatePage(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(new PasswordForm());
		return "settings/password";
	}
	
	@PostMapping("/settings/password")
	public String passwordUpdateform(@Valid PasswordForm passwordForm, Errors errors, Model model,RedirectAttributes attributes,@CurrentUser Account account) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "settings/password";
		}
		
		accountService.updatePassword(account, passwordForm.getNewPassword());
		attributes.addFlashAttribute("message", "패스워드를 수정했습니다.");
		return "redirect:/settings/password";
		
	}
	
	@GetMapping("/settings/notifications")
	public String updateNotifications(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(modelMapper.map(account, Notifications.class));
//		model.addAttribute(new Notifications(account));
		return "settings/notifications";
	}
	
	@PostMapping("/settings/notifications")
	public String updateNotificationsForm(@CurrentUser Account account, 
			@Valid Notifications notifications, Errors errors,
			Model model, RedirectAttributes attributes) {
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			attributes.addFlashAttribute("message", "에러가 발생했습니다.");
			return "settings/notifications";
		}
		
		accountService.updateNotifications(account, notifications);
		attributes.addFlashAttribute("message", "알람을 수정했습니다.");
		return "redirect:/settings/notifications";
	}
	
	@GetMapping("/settings/account")
	public String accountUpdate(@CurrentUser Account account, Model model) {
		model.addAttribute(account);
		model.addAttribute(modelMapper.map(account, NicknameForm.class));
//		model.addAttribute(new NicknameForm(account));
		return "settings/account";
	}
	
	@PostMapping("/settings/account")
	public String accountUpdateForm(@CurrentUser Account account, 
			@Valid NicknameForm nicknameForm, Errors errors, Model model, RedirectAttributes attributes) {
		if(errors.hasErrors()) {
			model.addAttribute(account);
			return "/settings/account";
		}
		accountService.accountUpdate(account, nicknameForm.getNickname());
		attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
		return "redirect:/settings/account";
	}
	
	@GetMapping("/settings/tags")
	public String tagPage(@CurrentUser Account account, Model model) throws JsonProcessingException {
		model.addAttribute(account);
		Set<Tag> tags = accountService.getTags(account);
		Set<Tag> tags2 = new HashSet<Tag>();
		
		System.out.println(tags2.iterator());
//		String text="";
//		for(Tag t:tags) {
//			text+=t.getTitle()+",";
//		}
//		Stream<Tag> stream = tags.stream();
//		stream.forEach(item -> {
//			String name = item.getTitle();
//			System.out.println(name+",");
//		});
//		tags.stream().forEach(item->{
//			System.out.println(item.getTitle());
//		});
//		Set<Tag> tags2 = accountService.getTags(account);
//		System.out.println(tags2.stream().map(Tag::getTitle).collect(Collectors.toList()));
//		System.out.println(tags2.stream().map(Tag::getTitle).collect(Collectors.joining(",")));
		model.addAttribute("tags", 
				//tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
				tags.stream().map(Tag::getTitle).collect(Collectors.joining(",")));
		
		List<String> allTags = tagRepository.findAll()
										    .stream()
										    .map(Tag::getTitle)
										    .collect(Collectors.toList());
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
		
		
		return "settings/tags";
	}
	
	@PostMapping("/settings/tags/add")
	@ResponseBody
	public ResponseEntity addTag(@CurrentUser Account account,@RequestBody TagForm tagForm) {
		String title = tagForm.getTagTitle();
		
		Tag tag = tagRepository.findByTitle(title);
		if(tag == null) {
			tag = tagRepository.save(tag.builder().title(title).build());
		}
		accountService.addTag(account, tag);
//		Tag tag = tagRepository.findByTitle(title)
//				.orElseGet(()->
//					tagRepository.save(
//							Tag.builder().tagTitle(tagForm.getTagTitle()).build()));
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/settings/tags/remove")
	@ResponseBody
	public ResponseEntity removeTag(@CurrentUser Account account
			, @RequestBody TagForm tagForm) {
		String title = tagForm.getTagTitle();
		Tag tag = tagRepository.findByTitle(title);
		if(tag == null) {
			return ResponseEntity.badRequest().build();		
		}
		accountService.removeTag(account, tag);
		
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/settings/zones")
    public String updateZonesForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));

        return "settings/zones";
    }
	
	@PostMapping("/settings/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {

        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.addZone(account, zone);

        return ResponseEntity.ok().build();
    }
	
	@PostMapping("/settings/zones/remove")
	@ResponseBody
	public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
		
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
		
		if(zone == null) {
			return ResponseEntity.badRequest().build();
		}
		
		accountService.removeZone(account, zone);
		
		return ResponseEntity.ok().build();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
