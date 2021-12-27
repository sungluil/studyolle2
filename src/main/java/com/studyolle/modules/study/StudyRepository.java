package com.studyolle.modules.study;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.account.Account;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>
, StudyRepositoryExtension{

	boolean existsByPath(String path);

	@EntityGraph(value = "Study.withAll", type = EntityGraphType.LOAD)
	Study findByPath(String path);
	
	@EntityGraph(value = "Study.withTagsAndManagers", type = EntityGraphType.FETCH)
	Study findStudyWithTagsByPath(String path);
	
	@EntityGraph(value = "Study.withZonesAndManagers", type = EntityGraphType.FETCH)
	Study findStudyWithZonesByPath(String path);

	@EntityGraph(value = "Study.withManagers", type = EntityGraphType.FETCH)
	Study findStudyWithManagersByPath(String path);

	Study findStudyOnlyByPath(String path);

	@EntityGraph(value = "Study.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
	Study findStudyWithTagsAndZonesById(Long id);

	@EntityGraph(attributePaths = {"members","managers"})//기본이 패치
	Study findStudyWithManagersAndMembersById(Long id);
	
	List<Study> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean publided, boolean close);
		

}
