package com.studyolle.modules.notification;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.account.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {
	

	@Id @GeneratedValue
	private Long id;
	
	private String title;
	
	private String link;
	
	private String message;
	
	private boolean checked;
	
	@ManyToOne
	private Account account;
	
	private LocalDateTime createdDateTime;
	
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	

	public void checkedTrue(boolean check) {
		this.checked = check;
		System.out.println(checked);
	}
	
}
