package com.studyolle.modules.study.event;

import com.studyolle.modules.study.Study;

import lombok.Getter;

@Getter
public class StudyCreatedEvents {

	private Study study;
	
	public StudyCreatedEvents(Study study) {
		this.study = study;
	}

}
