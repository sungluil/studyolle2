package com.studyolle.modules.main;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.QAccount;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.event.QEvent;
import com.studyolle.modules.study.QStudy;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.tag.QTag;
import com.studyolle.modules.zone.QZone;

import lombok.RequiredArgsConstructor;


@Configuration
public class TestRepositoryImpl extends QuerydslRepositorySupport implements TestRepository {
	
	public TestRepositoryImpl() {
		super(Study.class);
		// TODO Auto-generated constructor stub
	}

	@Autowired
	JPAQueryFactory query;

	@Override
	public Page<Study> findByKeyword(String keyword,Pageable pageable) {
		QStudy study = QStudy.study;
		JPQLQuery<Study> studyList = query.selectFrom(study)
			.where(study.published.isTrue()
			.and(study.title.containsIgnoreCase(keyword))
			.or(study.tags.any().title.containsIgnoreCase(keyword))
			.or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
			.leftJoin(study.tags,QTag.tag).fetchJoin()
			.leftJoin(study.zones,QZone.zone).fetchJoin()
			.leftJoin(study.members,QAccount.account).fetchJoin()
			.distinct();
		
		JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, studyList);
		QueryResults<Study> fetchResults = pageableQuery.fetchResults();
		return new PageImpl<>(fetchResults.getResults(),pageable,fetchResults.getTotal());
	}

	@Override
	public List<Study> findByStudy() {
		// TODO Auto-generated method stub
		QStudy study = QStudy.study;
		List<Study> studyPage = query
				.selectFrom(study)
				.where(study.published.isTrue())
				.orderBy(study.publishedDateTime.desc())
				.limit(9)
				.fetch();
		
		return studyPage;
	}

	@Override
	public List<Event> findEnrollEvent(Account account) {
		// TODO Auto-generated method stub
		QStudy study = QStudy.study;
		QEvent event = QEvent.event;
		
		List<Event> findEvent = query
				.selectFrom(event)
				.where(event.enrollments.any().account.id.in(account.getId()))
				.limit(4)
				.fetch();
		
		return findEvent;
	}

	
	

	
}
