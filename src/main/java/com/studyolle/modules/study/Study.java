package com.studyolle.modules.study;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.UserAccount;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.zone.Zone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NamedEntityGraph(name = "Study.withAll", attributeNodes = {
		@NamedAttributeNode("tags"),
		@NamedAttributeNode("zones"),
		@NamedAttributeNode("managers"),
		@NamedAttributeNode("members")
})

@NamedEntityGraph(name = "Study.withTagsAndManagers", attributeNodes = {
		@NamedAttributeNode("tags"),
		@NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withZonesAndManagers", attributeNodes = {
		@NamedAttributeNode("zones"),
		@NamedAttributeNode("managers")
})
@NamedEntityGraph(name = "Study.withManagers", attributeNodes = {
		@NamedAttributeNode("managers")
})

@NamedEntityGraph(name = "Study.withTagsAndZones", attributeNodes = {
		@NamedAttributeNode("tags"),
		@NamedAttributeNode("zones")
})

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>(); //매니저정보

    @ManyToMany
    private Set<Account> members = new HashSet<>(); //멤버정보

    @Column(unique = true)
    private String path;//스터디 주소

    private String title;//제목

    private String shortDescription; //썸네일내용

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;//내용

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;//썸네일이미지

    @ManyToMany
    private Set<Tag> tags = new HashSet<>(); //태그들

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();//위치정보들

    private LocalDateTime publishedDateTime; //공개한시간

    private LocalDateTime closedDateTime;//종료한시간

    private LocalDateTime recruitingUpdatedDateTime;//모집시작시간

    private boolean recruiting;//모집상태

    private boolean published;//공개상태

    private boolean closed;//스터디 종료

    private boolean useBanner;//배너이미지 사용여부

    private int memberCount;//참가인원

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        return this.isPublished() && this.isRecruiting()
                && !this.members.contains(account) && !this.managers.contains(account);

    }

    public boolean isMember(UserAccount userAccount) {
        return this.members.contains(userAccount.getAccount());
    }

    public boolean isManager(UserAccount userAccount) {
    	return this.managers.contains(userAccount.getAccount());
    }
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("%s,%s,%s,%s",path, title, shortDescription, fullDescription);
	}
	
    public String getImage() {
        return image != null ? image : "/images/default_banner.png";
    }

	public boolean isManagedBy(Account account) {
		// TODO Auto-generated method stub
		return this.getManagers().contains(account);
	}

	public void publish() {
		if(!this.closed && !this.published) {//종료하지않았고 공개한상태가아니면
			this.published=true;
			this.publishedDateTime=LocalDateTime.now();
		} else {
			throw new RuntimeException("스터디를 공개할 수 없는 상태입니다. 스터디를 이미 공개했거나 종료했습니다.");
		}
		
	}

	public void close() {
		if(this.published && !this.closed) {//공개되어있고 종료하지않은상태면
			this.closed=true;
			this.closedDateTime=LocalDateTime.now();
		} else {
			throw new RuntimeException("스터디를 종료할 수 없는 상태입니다. 스터디를 공개하지 않았거나 종료한 스터디입니다.");
		}
	}

	public boolean canUpdateRecruiting() {
		// TODO Auto-generated method stub
		//공개중이며 모집시간 비어있고 또는 
		return this.published && this.recruitingUpdatedDateTime == null
				|| this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
	}
	
    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

	public void startRecruit() {
		// TODO Auto-generated method stub
//		if(canUpdateRecruiting()) {
//			this.recruiting = true;
//			this.recruitingUpdatedDateTime = LocalDateTime.now();
//		} else {
//			throw new RuntimeException("인원 모집을 시작할 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
//		}
		this.recruiting = true;
		this.recruitingUpdatedDateTime = LocalDateTime.now();
	}

	public void stopRecruit() {
		// TODO Auto-generated method stub
//		if(canUpdateRecruiting()) {
//		} else {
//			throw new RuntimeException("인원 모집을 멈출 수 없습니다. 스터디를 공개하거나 한 시간 뒤 다시 시도하세요.");
//		}
		this.recruiting = false;
		this.recruitingUpdatedDateTime = LocalDateTime.now();
	}
	
	public boolean isRemovable() {
		return !this.published; // TODO 모임을 했던 스터디는 삭제할 수 없다.
	}

	public void addMember(Account account) {
		// TODO Auto-generated method stub
		this.members.add(account);
	}

	public void leaveMember(Account account) {
		// TODO Auto-generated method stub
		this.members.remove(account);
	}

	public boolean isManagerOf(Account account) {
		// TODO Auto-generated method stub
		return this.getManagers().contains(account);
	}
}














