package com.studyolle.modules.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.account.Account;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	//읽은 메세지
	Long countByAccountAndChecked(Account account, boolean checked);

	@Transactional
	List<Notification> findByAccountAndCheckedOrderByCreatedDateTimeDesc(Account account, boolean checked);
	//select * from Notification
	//where account = ''
	//and checked =  ''
	//order by createdDateTime desc

	Notification findByLink(String notiPath);

}
