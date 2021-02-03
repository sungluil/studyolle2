package com.studyolle.modules.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studyolle.modules.account.Account;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Long countByAccountAndChecked(Account account, boolean checked);

}
