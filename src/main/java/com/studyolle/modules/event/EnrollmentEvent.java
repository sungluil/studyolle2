package com.studyolle.modules.event;

import lombok.RequiredArgsConstructor;

import lombok.Getter;

@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {

	protected final Enrollment enrollment;
	
	protected final String message;
}
