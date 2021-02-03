package com.studyolle.modules.study.validator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.form.StudyForm;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

	private final StudyRepository studyRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return StudyForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		StudyForm studyForm = (StudyForm) target;
//		System.out.println(studyForm.toString());
		if(studyRepository.existsByPath(studyForm.getPath())) {
			errors.rejectValue("path", "wrong.path", "해당 스터디의 경로를 사용할 수 없습니다.");
		}
	}
	
	
}
