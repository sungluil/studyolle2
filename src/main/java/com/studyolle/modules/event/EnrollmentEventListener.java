package com.studyolle.modules.event;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.studyolle.infra.config.AppProperties;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;
import com.studyolle.modules.notification.NotificationType;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Async
@Component
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EnrollmentEventListener {
	
	private final EmailService emailService;
	private final TemplateEngine templateEngine; 
	private final AppProperties appProperties; 
	private final NotificationRepository notificationRepository;
	
	@EventListener
    public void handleStudyCreatedEvent(EnrollmentEvent enrollmentEvent) {
        //등록된 모든 정보들을 가져옴
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        //등록된 회원의정보를 불러옴 
        Account account = enrollment.getAccount();
        //등록된 모임의 정보를 가져옴
        Event event = enrollment.getEvent();
        //등록된 스터디 정보를 가져옴
        Study study = event.getStudy();
        
        if(account.isStudyEnrollmentResultByEmail()) {
        	sendStudyCreatedEmail(enrollmentEvent,account,event,study);        	
        }
        
        if (account.isStudyEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, account, event, study);
        }
    }

    private void sendStudyCreatedEmail(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath()+ "/events/" + event.getId());
        context.setVariable("linkname", study.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link2", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("스터디올래, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, Account account, Event event, Study study) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle() + " / " + event.getTitle());
        notification.setLink("/study/" + study.getEncodedPath() + "/events/" + event.getId());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(enrollmentEvent.getMessage());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }
}
