package com.studyolle.modules.event;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.studyolle.modules.account.Account;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Event event; //모임과 이벤트 연결
	
	@ManyToOne
	private Account account;//등록된 유저정보
	
	private LocalDateTime enrolledAt; //등록된시간으로 차등으로 구별하기때문에 중요
	
	private boolean accepted; //true면 확정
	
	private boolean attended; //실제참석
}
