package com.studyolle.modules.event;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.studyolle.modules.study.Study;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>{

	@EntityGraph(value = "Event.withEnrollments",type = EntityGraphType.LOAD)
	List<Event> findByStudyOrderByStartDateTime(Study study);
	
	List<Event> findFirst4ByStudyOrderByStartDateTime(Study study);

}
