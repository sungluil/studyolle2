package com.studyolle.modules.event;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.event.form.EventForm;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

	private final StudyService studyService; 
	private final ModelMapper modelMapper; 
	private final EventService eventService;
	private final EventValidator eventValidator; 
	private final EventRepository eventRepository;
	private final EnrollmentRepository enrollmentRepository; 
	
	/**
	 * 유효성검사한걸 eventForm에 적용
	 * 등록 마감 일시, 모임시작일시 ,모임 접수 종료일시 유효성검사
	 */
	
	@InitBinder("eventForm")
	public void InitBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(eventValidator);
	}
	
	/**
	 * 새모임만들기 폼페이지를 보여줌
	 */
	@GetMapping("/new-event")
	public String newEventForm(@CurrentUser Account account
			, @PathVariable String path, Model model) {
		Study study = studyService.getStudy(path);
		model.addAttribute(study);
		model.addAttribute(account);
		model.addAttribute(new EventForm());
		return "event/form";
	}
	
	/**
	 * 새모임 만들기 폼데이터를 db에 전송
	 * 폼데이터를 이벤트객체에 복사한뒤에
	 * 모임 생성자정보,생성시간,스터디정보까지 추가로 삽입
	 */
	@PostMapping("/new-event")
	public String newEventCreate(@CurrentUser Account account, @PathVariable String path,
			@Valid EventForm eventForm, Errors errors, RedirectAttributes attributes, Model model) {
		Study study = studyService.getStudy(path);
		if(errors.hasErrors()) {
			model.addAttribute(study);
			model.addAttribute(account);
			return "event/form";
		}
		Event event = eventService.createNewEvent(modelMapper.map(eventForm, Event.class),study, account);
		
		return "redirect:/study/" + study.getEncodedPath()+"/events/"+event.getId();
	}
	
	/**
	 * 생성된 모임정보 페이지
	 */
	@GetMapping("/events/{id}")
	public String eventView(@CurrentUser Account account, @PathVariable String path,
			@PathVariable Long id, Model model) {
		Study study = studyService.getStudy(path);
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute("event",eventRepository.findById(id).orElseThrow());
		return "event/view";
	}
	
	/**
	 * 스터디의 모임 탭 페이지 구현
	 * 모임을 스터디로 검색하여 생성된시간으로 오더바이로 검색한뒤
	 * forEach를 이용해 현재시간이 종료시간보다 빠르면 지난모임 현재보다 미래일땐 새 모임
	 */
	@GetMapping("/events")
	public String eventsViewList(@CurrentUser Account account, @PathVariable String path
			,Model model) {
		Study study = studyService.getStudy(path);
		model.addAttribute(account);
		model.addAttribute(study);
		List<Event> event = eventRepository.findByStudyOrderByStartDateTime(study);
		List<Event> newEvents = new ArrayList<>();
		List<Event> oldEvents = new ArrayList<>();
		
		event.forEach(e-> {
			//모집종료시간이 현재시간보다 이전이면 
			if(e.getEndDateTime().isBefore(LocalDateTime.now())) {
				oldEvents.add(e);				
			}
 			newEvents.add(e);
		});
		
		model.addAttribute("newEvents", newEvents);
		model.addAttribute("oldEvents", oldEvents);
		return "study/events";
	}
	
	/**
	 * 모임 페이지 수정버튼 이벤트
	 */
	@GetMapping("/events/{id}/edit")
	public String updateEventForm(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id,Model model) {
		Study study = studyService.getStudyToUpdate(account, path);//매니저만가져옴
		Event event = eventRepository.findById(id).orElseThrow();
		model.addAttribute(account);
		model.addAttribute(study);
		model.addAttribute(event);
		model.addAttribute(modelMapper.map(event, EventForm.class));
		return "event/update-form";
	}
	
	/**
	 * 모임 페이지 수정폼 액션
	 * 수정권한이 있는지 체크하고 현재 확안된 참가신청보다 모집인원적게 넣는지 검사
	 * 서비스단에서 모델매퍼로 복사로 간단하게 업데이트처리
	 */
	
	@PostMapping("/events/{id}/edit")
	public String updateEventFormAction(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id,Model model, @Valid EventForm eventForm, Errors errors,RedirectAttributes attributes) {
		Study study = studyService.getStudyToUpdate(account, path);//매니저만가져옴
		Event event = eventRepository.findById(id).orElseThrow();
		eventForm.setEventType(event.getEventType());
		
		if(eventForm.getLimitOfEnrollments() < event.getLimitOfEnrollments()) {
			errors.rejectValue("limitEnrollments", "wrong.value", "확인된 참가신청보다 모집 인원수가 커야 합니다.");
		}
		
		if(errors.hasErrors()) {
			model.addAttribute(account);
			model.addAttribute(study);
			model.addAttribute(event);
			return "event/update-form";
		}
		
		eventService.updateNewEvent(event, eventForm);
		
		attributes.addFlashAttribute("message", "모임을 수정했습니다.");
		return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
	}
	
	/**
	 * 모임 취소버튼 이벤트
	 * 매너지인지 체크
	 */
	
	@DeleteMapping("/events/{id}")
	public String deleteEvent(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id, Model model) {
		Study study = studyService.getStudyToUpdate(account, path);
		Event event = eventRepository.findById(id).orElseThrow();
		eventService.deleteEvent(event);
		
		return "redirect:/study/" + study.getEncodedPath()+"/events/";
	}
	
	/**
	 * 모임 참가신청 버튼
	 * 스터디조회후 참가신청이 된 유저인지 체크후
	 * 참가유저객체(Enrollment)에 데이터 삽입
	 * event객체에도 참가자 정보 넣어줌
	 */
	@PostMapping("/events/{id}/enroll")
	public String enrollEvnet(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id, Model model) {
		
		Study study = studyService.getStudyToEnroll(path);//스터디 정보만 가져옴
		Event event = eventRepository.findById(id).orElseThrow();
		eventService.addEnroll(account,event);
		
		return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
	}
	
	/**
	 * 이벤트 참가신청취소 버튼 이벤트
	 * 주의할점
	 * 모임 타입이 선착순일경우 참가상태가 확정으로 관리자권한일경우에는 대기중으로
	 * 모집인원2명일때 확정인원2명에 추가참가자가 있을경우 확정인원이 취소할경우
	 * 가장 빠른 대기열에 있는 사람이 확정으로 진급
	 * 
	 * 스터디 정보와 모임 정보를 가져와서 모임에 참가한 인원을 찾은뒤
	 * 모임(Event)에서 해당 인원 정보 삭제
	 * 참가자객체(Enrollment)에서도 해당 인원 정보를 삭제
	 * 모임 타입이 선착순일경우에 삭제후 참가자객체갯수와 해당모임의 모집인원을비교해
	 * 참여가 가능한 상태면 for문으로 참가자를 조회후 가장먼저 나온 해당인원을
	 * 확정상태로 변경시킴
	 * 
	 */
	@PostMapping("/events/{id}/disenroll")
	public String disenrollEvnet(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id, Model model) {
		
		Study study = studyService.getStudyToEnroll(path);//스터디 정보만 가져옴
		Event event = eventRepository.findById(id).orElseThrow();
		eventService.cancleEnroll(account,event);
		
		return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
	}
	
	
	@GetMapping("/events/{id}/enrollments/{enrollmentId}/accept")
	public String enrollmentAccept(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id, @PathVariable Long enrollmentId) {
		
		Study study = studyService.getStudyToUpdate(account, path);
		Event event = eventRepository.findById(id).orElseThrow();
		Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
		eventService.accept(event,enrollment);		
		
		return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
	}
	
	@GetMapping("/events/{id}/enrollments/{enrollmentId}/reject")
	public String enrollmentReject(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id, @PathVariable Long enrollmentId) {
		
		Study study = studyService.getStudyToUpdate(account, path);
		Event event = eventRepository.findById(id).orElseThrow();
		Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
		eventService.reject(event,enrollment);		
		
		return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
	}
	
	@GetMapping("/events/{id}/enrollments/{enrollmentId}/checkin")
	public String enrollmentCheckin(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id, @PathVariable Long enrollmentId) {
		
		Study study = studyService.getStudyToUpdate(account, path);
		Event event = eventRepository.findById(id).orElseThrow();
		Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
		eventService.checkin(enrollment);		
		
		return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
	}
	
	@GetMapping("/events/{id}/enrollments/{enrollmentId}/cancel-checkin")
	public String enrollmentCancelCheckin(@CurrentUser Account account, @PathVariable String path
			,@PathVariable Long id, @PathVariable Long enrollmentId) {
		
		Study study = studyService.getStudyToUpdate(account, path);
		Event event = eventRepository.findById(id).orElseThrow();
		Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
		eventService.cancelCheckin(enrollment);		
		
		return "redirect:/study/"+study.getEncodedPath()+"/events/"+event.getId();
	}
}












