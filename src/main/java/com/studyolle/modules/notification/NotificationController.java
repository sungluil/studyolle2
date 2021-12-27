package com.studyolle.modules.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationRepository notificationRepository; 
	private final NotificationService notificationService;
	
	@GetMapping("/notifications")
	public String notification(@CurrentUser Account account, Model model, Notification notificationnn, boolean check) {
		//유저정보와 체크유무를 내림차순으로 조회
		//select * from notification where account = '' and checked = '' order by desc;
		List<Notification> notifications = notificationRepository
				.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, false);
		long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);//읽은알람
		long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);//읽지않은알람
		
		//위에 정보를 가지고 타입별로 나눠줌
		//새로운 스터디알람
		List<Notification> newStudyNotifications = new ArrayList<>();
		//모임 참가신청관련 알람
		List<Notification> eventEnrollmentNotifications = new ArrayList<>();
		//참여하고있는 스터디 수정사항
		List<Notification> watchingStudyNotifications = new ArrayList<>();
		
		for(Notification notification : notifications) {
			
			switch (notification.getNotificationType()) {
			
			case STUDY_CREATED :
						newStudyNotifications.add(notification);
				break;

			case EVENT_ENROLLMENT :
						eventEnrollmentNotifications.add(notification);
				break;
				
			case STUDY_UPDATED :
						watchingStudyNotifications.add(notification);
				break;
			}
		}
		model.addAttribute("numberOfNotChecked", numberOfNotChecked);//안읽은거
		model.addAttribute("numberOfChecked", numberOfChecked);//읽은거
		model.addAttribute("notifications", notifications);//전체알림 리스트
		model.addAttribute("newStudyNotifications", newStudyNotifications);//스터디알람
		model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);//참가신청관련 알림
		model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);//참여한 스터디 수정알림
		
		//알람클릭후 페이지이동시엔 읽음처리 서비스 실행됨
		
		//notificationService.markAsRead(notifications);
//		notificationService.markRead(notificationnn);
		model.addAttribute("notification", notificationnn);
		model.addAttribute("isNew", true);
		return "notification/list";
	}
	
	@GetMapping("/notifications/old")
	public String oldNotification(@CurrentUser Account account, Model model) {
		List<Notification> notifications = notificationRepository
				.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
		
		//위에 정보를 가지고 타입별로 나눠줌
		//새로운 스터디알람
		List<Notification> newStudyNotifications = new ArrayList<>();
		//모임 참가신청관련 알람
		List<Notification> eventEnrollmentNotifications = new ArrayList<>();
		//참여하고있는 스터디 수정사항
		List<Notification> watchingStudyNotifications = new ArrayList<>();
		long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);//읽은알람
		long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);//읽지않은알람
		for(Notification notification : notifications) {
			
			switch (notification.getNotificationType()) {
			
			case STUDY_CREATED :
						newStudyNotifications.add(notification);
				break;

			case EVENT_ENROLLMENT :
						eventEnrollmentNotifications.add(notification);
				break;
				
			case STUDY_UPDATED :
						watchingStudyNotifications.add(notification);
				break;
			}
		}
		model.addAttribute("numberOfNotChecked", numberOfNotChecked);//안읽은거
		model.addAttribute("numberOfChecked", numberOfChecked);//읽은거
		model.addAttribute("notifications", notifications);//전체알림 리스트
		model.addAttribute("newStudyNotifications", newStudyNotifications);//스터디알람
		model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);//참가신청관련 알림
		model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);//참여한 스터디 수정알림
		
		model.addAttribute("isNew", false);
		return "notification/list";
	}
	
	@DeleteMapping("/notifications")
	public String deleteNotification(@CurrentUser Account account, Model model) {
		List<Notification> notifications = notificationRepository
				.findByAccountAndCheckedOrderByCreatedDateTimeDesc(account, true);
		notificationService.delete(notifications);
		return "redirect:/notifications";
	}
}




















