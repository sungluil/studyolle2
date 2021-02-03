package com.studyolle.modules.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.UserAccount;
import com.studyolle.modules.study.Study;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.var;

@NamedEntityGraph(
		name = "Event.withEnrollments",
		attributeNodes = @NamedAttributeNode("enrollments")
)
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	private Study study;//스터디정보
	
	@ManyToOne
	private Account createdBy;//모임 생성자정보
	
	@Column(nullable = false)
	private String title; //제목
	
	//기본전략이 패치가 아니라 레이지라면 에러날까?
	@Lob
	private String description;//내용
	
	@Column(nullable = false)
	private LocalDateTime createdDateTime;//모임생성시간
	
	@Column(nullable = false)
	private LocalDateTime endEnrollmentDateTime;//등록종료시간
	
	@Column(nullable = false)
	private LocalDateTime startDateTime;//모집시작시간
	
	@Column(nullable = false)
	private LocalDateTime endDateTime;//모집종료시간
	
	
	@Column //int와integer 널이들어갈수있냐없냐 차이가있음
	private Integer limitOfEnrollments;//등록제한인원
	
	@OneToMany(mappedBy = "event")
	private List<Enrollment> enrollments;//모임들 리스트
	
	@Enumerated(EnumType.STRING)//기본값이 오디널
	private EventType eventType;//모임의2가지 타입
	
	
	
	public void addEnrollments(Enrollment enrollment) {
		this.enrollments.add(enrollment);
	}
	
	
	public int numberOfRemainSpots() {
		return limitOfEnrollments-enrollments.size();
	}
	
    public boolean isEnrollableFor(UserAccount userAccount) {//가입신청가능한상태
    	//이벤트가 종료되지않았으면서 참석하지 않았으면
        return isNotClosed() && !isAttended(userAccount) && !isAlreadyEnrolled(userAccount);
    }
    
    public boolean isDisenrollableFor(UserAccount userAccount) {//가입신청이 불가능
        return isNotClosed() && !isAttended(userAccount) && isAlreadyEnrolled(userAccount);
    }
    
    private boolean isNotClosed() {
    	//등록마감시간이 현재 시간보다 미래
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }
    
    public boolean isAttended(UserAccount userAccount) {//참가완료
    	
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account) && e.isAttended()) {
                return true;
            }
        }

        return false;
    }
    
    private boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment e : this.enrollments) {
            if (e.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }


	public boolean isAbleToAcceptWaitingEnrollment() {
		return this.eventType == eventType.FCFS &&
				this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
	}
	
	public boolean isAbleToAcceptWaitingEnrollment2() {
		return this.eventType == eventType.CONFIRMATIVE &&
				this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
	}
	
	
	private Long getNumberOfAcceptedEnrollments() {
		// TODO Auto-generated method stub
		return this.enrollments.stream().filter(Enrollment::isAccepted).count();
		//filter 걸러냄
	}


	//신청수락
	public boolean canAccept(Enrollment enrollment) {
		return this.eventType == eventType.CONFIRMATIVE
				&& this.enrollments.contains(enrollment)
				&& !enrollment.isAttended() //참석상태가아님
				&& !enrollment.isAccepted(); //확정상태가아님
	}
	
	//신청취소
	public boolean canReject(Enrollment enrollment) {
		return this.eventType == eventType.CONFIRMATIVE
				&& this.enrollments.contains(enrollment)
				&& !enrollment.isAttended() //참석상태
				&& enrollment.isAccepted(); //확정상태
	}


	public void removeEnrollment(Enrollment enrollment) {
		// TODO Auto-generated method stub
		this.enrollments.remove(enrollment);
		 enrollment.setEvent(null);
	}

	//첫번째 대기열
	public Enrollment getFirstWaitingEnrollment() {
		// TODO Auto-generated method stub
		for(Enrollment e : this.enrollments) {
			if(!e.isAccepted()) {
				System.out.println("대기열"+e);
				return e;
			}
		}
		return null;
	}


	public void acceptWaitingList() {
		// TODO Auto-generated method stub
        if (this.isAbleToAcceptWaitingEnrollment()) {
            var waitingList = getWaitingList();
            int numberToAccept = (int) Math.min(this.limitOfEnrollments - this.getNumberOfAcceptedEnrollments()
            		, waitingList.size());
             waitingList.subList(0, numberToAccept).forEach(e -> e.setAccepted(true));
        }
	}
	
    private List<Enrollment> getWaitingList() {
        return this.enrollments
        		.stream()
        		.filter(enrollment -> !enrollment.isAccepted())
        		.collect(Collectors.toList());
    }
	
}
