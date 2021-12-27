package com.studyolle.modules.event;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.form.EventForm;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.event.StudyUpdateEvent;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final EnrollmentRepository enrollmentRepository;
	private final ApplicationEventPublisher eventPublisher;

	
	public Event createNewEvent(Event event, Study study, Account account) {
		event.setCreatedBy(account);
		event.setCreatedDateTime(LocalDateTime.now());
		event.setStudy(study);
		eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), event.getTitle()+" 모임을 만들었습니다."));
		eventRepository.save(event);
		return event;
	}


	public Event updateNewEvent(Event event, EventForm eventForm) {
		modelMapper.map(eventForm, event);//모델 복사
		event.acceptWaitingList();
		// TODO Auto-generated method stub
//		event.setTitle(eventForm.getTitle());
//		event.setEventType(eventForm.getEventType());
//		event.setLimitOfEnrollments(eventForm.getLimitOfEnrollments());
//		event.setEndEnrollmentDateTime(eventForm.getEndEnrollmentDateTime());
//		event.setStartDateTime(eventForm.getStartDateTime());
//		event.setEndDateTime(eventForm.getEndDateTime());
//		event.setDescription(eventForm.getDescription());
//		eventRepository.save(event);
		eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), event.getTitle()+" 모임 정보를 수정했습니다. 확인해주세요."));
		return event;
	}


	public void deleteEvent(Event event) {
		// TODO Auto-generated method stub
		eventRepository.delete(event);
		eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(), event.getTitle()+" 모임을 취소했습니다."));
	}


	public void addEnroll(Account account, Event event) {
		// TODO Auto-generated method stub
		if(!enrollmentRepository.existsByEventAndAccount(event,account)) {
			Enrollment enrollment = new Enrollment();
			enrollment.setEnrolledAt(LocalDateTime.now());
			enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());
//			enrollment.setAccepted(true);
			enrollment.setAccount(account);
			enrollment.setEvent(event);
			event.addEnrollments(enrollment);
			enrollmentRepository.save(enrollment);
			eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
		}
	}


	public void cancleEnroll(Account account, Event event) {
		// TODO Auto-generated method stub
		Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event,account);
		eventPublisher.publishEvent(new EnrollmentCancleEvent(enrollment));
		event.removeEnrollment(enrollment);
		enrollmentRepository.delete(enrollment);
		
		if(event.isAbleToAcceptWaitingEnrollment()) {//추가적으로 인원 참여가 가능한상태
			Enrollment enrollmentToAccept = event.getFirstWaitingEnrollment();
			if(enrollmentToAccept != null) {
				enrollmentToAccept.setAccepted(true);
				eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
			}
		}
	}


	public void accept(Event event, Enrollment enrollment) {
		// TODO Auto-generated method stub
		if(event.isAbleToAcceptWaitingEnrollment2()) {
			enrollment.setAccepted(true);
			eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
		}
	}


	public void reject(Event event, Enrollment enrollment) {
		// TODO Auto-generated method stub
		if(event.isAbleToAcceptWaitingEnrollment2()) {
			enrollment.setAccepted(false);
			eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
		}
	}


	public void checkin(Enrollment enrollment) {
		// TODO Auto-generated method stub
		enrollment.setAttended(true);
	}


	public void cancelCheckin(Enrollment enrollment) {
		// TODO Auto-generated method stub
		enrollment.setAttended(false);
	}
	
}
