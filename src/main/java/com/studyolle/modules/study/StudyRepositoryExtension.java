package com.studyolle.modules.study;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

	List<Study> findByKeyword(String keyword);
}
