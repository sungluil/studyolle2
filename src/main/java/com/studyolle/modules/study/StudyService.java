package com.studyolle.modules.study;

import java.util.HashSet;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.UserAccount;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.study.event.StudyCreatedEvents;
import com.studyolle.modules.study.event.StudyUpdateEvent;
import com.studyolle.modules.study.form.StudyDescriptionForm;
import com.studyolle.modules.tag.Tag;
import com.studyolle.modules.tag.TagRepository;
import com.studyolle.modules.zone.Zone;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

	private final StudyRepository repository;
	private final ModelMapper modelMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final TagRepository tagRepository;
	

    public Study createNewStudy(Study study, Account account) {
//    	study.setId((long) 1);
    	System.out.println("study : "+study.toString());
        Study newStudy = repository.save(study);
        newStudy.addManager(account);
        
        //eventPublisher.publishEvent(new StudyCreateEvent(newStudy));
        
        return newStudy;
    }


	public Study getStudyToUpdate(Account account, String path) {
		// TODO Auto-generated method stub
		Study study = this.getStudy(path);
		checkIfManager(account, study);
		return study;
	}
	
	public Study getStudyToUpdateTag(Account account, String path) {
		Study study = repository.findStudyWithTagsByPath(path);
		checkIfManager(account, study);
		checkIfExistingStudy(path, study);
		return study;
	}

	public Study getStudy(String path) {
		Study study = this.repository.findByPath(path);
		checkIfExistingStudy(path, study);
		return study;
	}

	private void checkIfManager(Account account, Study study) {
		if(!study.isManagerOf(account)) {
			throw new AccessDeniedException("?????? ????????? ????????? ??? ????????????.");
		}
	}

	private void checkIfExistingStudy(String path, Study study) {
		if(study == null) {
			throw new IllegalArgumentException(path+"??? ???????????? ???????????? ????????????.");
		}
	}


	public void updateStudyDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
		modelMapper.map(studyDescriptionForm, study);
		//?????? ?????????
		this.eventPublisher.publishEvent(new StudyUpdateEvent(study,"????????? ????????? ??????????????????."));
		
	}


	public void updateStudyImage(Study study, String image) {
		//????????????????????? ???????????? ?????????????
		study.setImage(image);
	} 
	
	public void enableStudyBanner(Study study) {
		study.setUseBanner(true);
	}
	
	public void disableStudyBanner(Study study) {
		study.setUseBanner(false);
	}


	public void addTag(Study study, Tag tag) {
		study.getTags().add(tag);
		
	}


	public void removeTag(Study study, Tag tag) {
		study.getTags().remove(tag);
		
	}


	public Study getStudyToUpdateZones(Account account, String path) {
		Study study = repository.findStudyWithZonesByPath(path);
		checkIfManager(account, study);
		checkIfExistingStudy(path, study);
		return study;
	}


	public void addZones(Study study, Zone zone) {
		study.getZones().add(zone);
		
	}


	public void removeZones(Study study, Zone zone) {
		study.getZones().remove(zone);
		
	}


	public Study getStudyToUpdateStatus(Account account, String path) {
		Study study = repository.findStudyWithManagersByPath(path);
		checkIfExistingStudy(path, study);
        checkIfManager(account, study);
		return study;
	}


	public void publish(Study study) {
		study.publish();
		//?????? ?????????
		this.eventPublisher.publishEvent(new StudyCreatedEvents(study));
	}
	public void close(Study study) {
		study.close();
		//?????? ?????????
		this.eventPublisher.publishEvent(new StudyUpdateEvent(study,"???????????? ??????????????????."));
				
	}


	public void startRecruit(Study study) {
		// TODO Auto-generated method stub
		study.startRecruit();
		//?????? ?????????
		this.eventPublisher.publishEvent(new StudyUpdateEvent(study,"?????? ????????? ??????????????????."));
				
	}


	public void stopRecruit(Study study) {
		study.stopRecruit();
		//?????? ?????????
		this.eventPublisher.publishEvent(new StudyUpdateEvent(study,"?????? ????????? ??????????????????."));
		
	}

	public void updatePath(Study study, String newPath) {
		// TODO Auto-generated method stub
		study.setPath(newPath);
	}


	public void updateTitle(Study study, String newTitle) {
		// TODO Auto-generated method stub
		study.setTitle(newTitle);
	}


	public void removeStudy(Study study, String path) {
		if(study.isRemovable()) {
			repository.delete(study);			
		} else {
			throw new IllegalArgumentException("???????????? ????????? ??? ????????????.");			
		}
	}


	public void addMember(Study study, Account account) {
		// TODO Auto-generated method stub
		study.addMember(account);
	}


	public void leaveMember(Study study, Account account) {
		// TODO Auto-generated method stub
		study.leaveMember(account);
	}


	public Study getStudyToEnroll(String path) {
		// TODO Auto-generated method stub
		Study study = repository.findStudyOnlyByPath(path);
		checkIfExistingStudy(path, study);
		return study;
	}


	public void generatedStudy(Account account) {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < 50 ; i++) {
			String random = RandomString.make(5);
			Study study =Study.builder()
				.title("????????? ?????????" + random)
				.path("test-" + random)
				.shortDescription("???????????? ????????? ?????????.")
				.fullDescription("test")
				.tags(new HashSet<>())
				.managers(new HashSet<>())
				.build();
			study.publish();
			Study newStudy =  this.createNewStudy(study, account);
			Tag jpa =  tagRepository.findByTitle("spring");
			newStudy.getTags().add(jpa);
		}
	}




	
    

}
