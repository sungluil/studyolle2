package com.studyolle.modules.main;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.study.Study;

@Transactional(readOnly = true)
public interface TestRepository {

	Page<Study> findByKeyword(String keyword,Pageable pageable);
	
	List<Study> findByStudy();

	List<Event> findEnrollEvent(Account account);
	
}
