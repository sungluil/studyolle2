package com.studyolle.modules.event;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.studyolle.modules.event.form.EventForm;

@Component
public class EventValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return EventForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		// TODO Auto-generated method stub
		EventForm eventForm = (EventForm) object;
		if(eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now())) {
			errors.rejectValue("endEnrollmentDateTime","wrong.datatime", "등록 마감 일시를 정확히 입력하세요.");
		}
		
		if(isNotValidEntDateTime(eventForm)) {
			errors.rejectValue("endDateTime","wrong.datatime", "모임 접수 종료 일시를 정확히 입력하세요.");
		}
		
		if(isNotValidStartDateTime(eventForm)) {
			errors.rejectValue("startDateTime","wrong.datatime", "모임 시작 일시를 정확히 입력하세요.");
		}
	}

	private boolean isNotValidStartDateTime(EventForm eventForm) {
		// TODO Auto-generated method stub
		return eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
	}

	private boolean isNotValidEntDateTime(EventForm eventForm) {
		return eventForm.getEndDateTime().isBefore(eventForm.getStartDateTime()) 
		|| eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime());
	}

	
}
