package com.studyolle.modules.main;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyolle.infra.config.PageVo;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.account.AccountService;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.event.EventRepository;
import com.studyolle.modules.study.QStudy;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.StudyService;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final StudyRepository studyRepository;  
	private final JPAQueryFactory query;
	private final TestRepository testRepository; 
	private final AccountService accountService;
	private final AccountRepository accountRepository; 
	private final EventRepository eventRepository; 
	private final StudyService studyService; 
	
	@GetMapping("/")
	public String home(@CurrentUser Account account, Model model) {
		if(account != null) {
			model.addAttribute(account);
		}
		
		//Long count = notificationRepository.countByAccountAndChecked(account, false);
		//model.addAttribute("hasNotification", count>0);
		/**
		 * 이렇게 구현시에 여기 페이지에서 여기핸들러에서만 작동 모든페이지에서
		 * 작동되게 하려면 스프링 mvc핸들러를 이용
		 */
		Set<Tag> tags = null;
		Set<Zone> zones = null;
		List<Event> enrollEvent = null;
		if(account != null) {			
			tags = accountService.getTags(account);
			zones = accountService.getZones(account);
			enrollEvent = testRepository.findEnrollEvent(account);
		}
		
		
		//참석한 모임에 대한 스터디정보
		
		List<Study> studyPage = studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false);
//		List<Event> studyPage = testRepository.findByStudy();
//		System.out.println(studyPage);
//		List<Study> studyPage = studyRepository.findAll();
		model.addAttribute("studyPage", studyPage);
		model.addAttribute("enrollEvent", enrollEvent);
		model.addAttribute("tags", tags);
		model.addAttribute("zones", zones);
		
		return "index";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@Transactional
	@GetMapping("/search/study")
	public String searchStudy(String keyword, Model model,@RequestParam(defaultValue = "1") int page,
		@PageableDefault(size = 9,direction = Direction.DESC, sort = "publishedDateTime") Pageable pageable) {
//		QStudy study = QStudy.study;
//		List<Study> studyList = query.selectFrom(study)
//			.where(study.published.isTrue()
//			.and(study.title.containsIgnoreCase(keyword))
//			.or(study.tags.any().title.containsIgnoreCase(keyword))
//			.or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword))
//			).fetch();
		
		Page<Study> studyPage = testRepository.findByKeyword(keyword, pageable);
//		List<Study> studyList = studyRepository.findByKeyword(keyword);
		PageVo pageVo = new PageVo(page, pageable.getPageSize(), (int)studyPage.getTotalElements(), 10);
		
		System.out.println("총 갯수"+studyPage.getTotalElements());
		System.out.println("총 페이지"+pageVo.getTotalPage());
		System.out.println(pageVo.getStartPage());
		System.out.println(pageVo.getEndPage());
		System.out.println(studyPage.getContent());
		
		model.addAttribute("pageVo", pageVo);
		model.addAttribute("studyPage", studyPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
		
		return "study/search";
	}
}
















