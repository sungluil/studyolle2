package com.studyolle.modules.notification;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.studyolle.modules.account.Account;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
public class Notification {

	@Id @GeneratedValue
	private Long id;
	
	private String title;
	
	private String link;
	
	private String message;
	
	private boolean checked;
	
	@ManyToOne
	private Account account;
	
	private LocalDateTime createdLocalDateTime;
	
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	
	
}
