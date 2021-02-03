package com.studyolle.modules.study;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.form.ZoneForm;
import com.studyolle.modules.study.form.StudyDescriptionForm;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.tag.TagForm;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.tag.TagService;
import com.studyolle.modules.zone.Zone;
import com.studyolle.modules.zone.ZoneRepository;
import com.studyolle.modules.zone.ZoneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingController {

	private final StudyService studyService;
	private final ModelMapper modelMapper;
	private final TagRepository tagRepository;
	private final ZoneRepository zoneRepository;
	private final ObjectMapper objectMapper;
	private final TagService tagService; 
	private final ZoneService zoneService; 
	
	@GetMapping("/description")
	public String viewStudySetting(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
		return "study/settings/description";
	}
	
	@PostMapping("/description")
	public String updateStudyInfo(@CurrentUser Account account, @PathVariable String path
			,@Valid StudyDescriptionForm studyDescriptionForm, Errors errors, Model model
			,RedirectAttributes attributes) {
		
		Study study = studyService.getStudyToUpdate(account, path);
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			return "study/settings/description";
		}
		
		studyService.updateStudyDescription(study, studyDescriptionForm);
		attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");
		return "redirect:/study/" + getPath(path) + "/settings/description";
	}

	public String getPath(String path) {
		// TODO Auto-generated method stub
		return URLEncoder.encode(path, StandardCharsets.UTF_8);
	}
	
	
	@GetMapping("/banner")
	public String studyImageForm(@CurrentUser Account account, @PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(study);
		return "study/settings/banner";
	}
	
	@PostMapping("/banner")
	public String studyImageSubmit(@CurrentUser Account account, 
			@PathVariable String path, String image,RedirectAttributes redirectAttributes) {
		Study study = studyService.getStudyToUpdate(account, path);
		System.out.println("===================================");
		studyService.updateStudyImage(study, image);
		redirectAttributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");
		return "redirect:/study/"+getPath(path)+"/settings/banner";
	}
	
	@PostMapping("/banner/enable")
	public String enableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
		Study study = studyService.getStudyToUpdate(account, path);
		studyService.enableStudyBanner(study);
		return "redirect:/study/"+getPath(path)+"/settings/banner";
	}
	
	@PostMapping("/banner/disable")
	public String disableStudyBanner(@CurrentUser Account account, @PathVariable String path) {
		Study study = studyService.getStudyToUpdate(account, path);
		studyService.disableStudyBanner(study);
		return "redirect:/study/"+getPath(path)+"/settings/banner";
	}
	
	
	@GetMapping("/tags")
	public String studyTagForm(@CurrentUser Account account, @PathVariable String path, Model model) 
			throws JsonProcessingException {
		Study study = studyService.getStudyToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute("tags",study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));

		List<String> allTagTitles = tagRepository.findAll().stream()
				.map(Tag::getTitle).collect(Collectors.toList());

		model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
		return "study/settings/tags";
	}
	
	
	@PostMapping("/tags/add")
	@ResponseBody
	public ResponseEntity addTag(@CurrentUser Account account, @PathVariable String path,
			@RequestBody TagForm tagForm) {
		
		Study study = studyService.getStudyToUpdateTag(account, path);
		Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
		studyService.addTag(study, tag);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/tags/remove")
	@ResponseBody
	public ResponseEntity removeTag(@CurrentUser Account account, @PathVariable String path,
			@RequestBody TagForm tagForm) {

		Study study = studyService.getStudyToUpdateTag(account, path);
		Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
		if(tag == null) {
			return ResponseEntity.badRequest().build();
		}
		studyService.removeTag(study, tag);
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/zones")
	public String studyZonesForm(@CurrentUser Account account, 
			@PathVariable String path, Model model) throws JsonProcessingException {
		Study study =  studyService.getStudyToUpdateTag(account, path);
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute("zones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
		
		List<String> allZones 
			= zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
		model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
		return "study/settings/zones";
	}
	
	@PostMapping("/zones/add")
	@ResponseBody
	public ResponseEntity addZones(@CurrentUser Account account, 
			@PathVariable String path,@RequestBody ZoneForm zoneForm, Model model) {
		
		Study study = studyService.getStudyToUpdateZones(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName()
        		, zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.addZones(study,zone);
		
		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/zones/remove")
	@ResponseBody
	public ResponseEntity removeZones(@CurrentUser Account account, 
			@PathVariable String path,@RequestBody ZoneForm zoneForm, Model model) {
		Study study = studyService.getStudyToUpdateZones(account, path);
		Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName()
        		, zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
		studyService.removeZones(study,zone);
		return ResponseEntity.ok().build();
	}
	
	
	@GetMapping("/study")
	public String studySetting(@CurrentUser Account account, 
			@PathVariable String path, Model model) {
		Study study = studyService.getStudyToUpdate(account, path);
		model.addAttribute(account);
		model.addAttribute(study);
		return "study/settings/study";
	}
	
	@PostMapping("/study/publish")
	public String studyPublish(@CurrentUser Account account, 
			@PathVariable String path,Model model, RedirectAttributes attributes) {
		Study study = studyService.getStudyToUpdateStatus(account, path);
		studyService.publish(study);
		attributes.addFlashAttribute("message","스터디를 공개했습니다.");
		System.out.println("스터디를 공개함");
		return "redirect:/study/"+getPath(path)+"/settings/study";
	}
	
	@PostMapping("/study/close")
	public String studyClose(@CurrentUser Account account, 
			@PathVariable String path,Model model, RedirectAttributes attributes) {
		Study study = studyService.getStudyToUpdateStatus(account, path);
		studyService.close(study);
		attributes.addFlashAttribute("message","스터디를 종료했습니다.");
		return "redirect:/study/"+getPath(path)+"/settings/study";
	}
	
	@PostMapping("/recruit/start")
	public String studyRecruitStart(@CurrentUser Account account, 
			@PathVariable String path,Model model, RedirectAttributes attributes) {
		Study study = studyService.getStudyToUpdateStatus(account, path);
//		if(!study.canUpdateRecruiting()) {
//            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
//            return "redirect:/study/" + study.getEncodedPath() + "/settings/study";
//		}
		studyService.startRecruit(study);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
		return "redirect:/study/"+getPath(path)+"/settings/study";
	}
	
	@PostMapping("/recruit/stop")
	public String studyRecruitStop(@CurrentUser Account account, 
			@PathVariable String path,Model model, RedirectAttributes attributes) {
		Study study = studyService.getStudyToUpdateStatus(account, path);
		studyService.stopRecruit(study);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
		
		return "redirect:/study/"+getPath(path)+"/settings/study";
	}
	
	@PostMapping("/study/path")
	public String studyPathUpdate(@CurrentUser Account account, 
			@PathVariable String path,String newPath, Model model, RedirectAttributes attributes) {
		Study study = studyService.getStudyToUpdateStatus(account, path);
		studyService.updatePath(study, newPath);
		attributes.addFlashAttribute("message", "스터디 경로를 변경했습니다.");
		return "redirect:/study/"+getPath(newPath)+"/settings/study";
	}
	
	@PostMapping("/study/title")
	public String studyTitleUpdate(@CurrentUser Account account, 
			@PathVariable String path,String newTitle, Model model, RedirectAttributes attributes) {
		Study study = studyService.getStudyToUpdateStatus(account, path);
		studyService.updateTitle(study, newTitle);
		attributes.addFlashAttribute("message", "스터디 경로를 변경했습니다.");
		return "redirect:/study/"+getPath(path)+"/settings/study";
	}
	
	@PostMapping("/study/remove")
	public String studyRemove(@CurrentUser Account account, 
			@PathVariable String path) {
		Study study = studyService.getStudyToUpdateStatus(account, path);
		studyService.removeStudy(study, path);
		return "redirect:/";
	}
}





















