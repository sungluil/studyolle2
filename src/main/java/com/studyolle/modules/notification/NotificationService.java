package com.studyolle.modules.notification;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

	private final NotificationRepository notificationRepository;
	
	public void markAsRead(List<Notification> notifications) {
		// TODO Auto-generated method stub
		notifications.forEach(e->{
			e.setChecked(true);
		});
        notificationRepository.saveAll(notifications);
	}

	public void delete(List<Notification> notifications) {
		// TODO Auto-generated method stub
        notificationRepository.deleteAll(notifications);
	}
	
	public void check(Notification notification) {
		notification.setChecked(true);
		notificationRepository.save(notification);
	}

}
