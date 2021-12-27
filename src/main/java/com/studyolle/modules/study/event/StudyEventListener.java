package com.studyolle.modules.study.event;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
import com.studyolle.modules.account.AccountPredicates;
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
public class StudyEventListener {
	
	private final StudyRepository studyRepository;
	private final AccountRepository accountRepository; 
	private final EmailService emailService;
	private final TemplateEngine templateEngine; 
	private final AppProperties appProperties; 
	private final NotificationRepository notificationRepository;
	
	@EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvents studyCreatedEvent) {
        // Tags와 Zones를 참조할수 있는 Study를 가져왔다.
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()){
                sendStudyCreatedEmail(study, account,"새로운 스터디가 생겼습니다.", "스터디올래, '"+study.getTitle()+"'스터디가 생겼습니다.");
            }

            if (account.isStudyCreatedByWeb()) {
            	createNotification(study, account,study.getShortDescription(),NotificationType.STUDY_CREATED);
            }
        });
    }
	
	@EventListener
	public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
		Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
		Set<Account> accounts = new HashSet<>();
		accounts.addAll(study.getManagers());
		accounts.addAll(study.getMembers());
		
		accounts.forEach(e-> {
			if(e.isStudyUpdatedByEmail()) {
				sendStudyCreatedEmail(study, e, studyUpdateEvent.getMessage(), "스터디올래, '"+study.getTitle()+"'스터디에 새소식이 있습니다.");
			}
			
			if(e.isStudyUpdatedByWeb()) {
				createNotification(study,e,studyUpdateEvent.getMessage(),NotificationType.STUDY_UPDATED);
			}
		});
	}

    private void sendStudyCreatedEmail(Study study, Account account, String contextMessage, String subject) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkname", study.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link2", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(subject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);        
        //notification.setMessage(study.getShortDescription());
        //notification.setNotificationType(NotificationType.STUDY_CREATED);
    }
}
