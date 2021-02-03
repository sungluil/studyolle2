package com.studyolle.modules.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.account.Account;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

	boolean existsByEventAndAccount(Event event, Account account);

	Enrollment findByEventAndAccount(Event event, Account account);

}
