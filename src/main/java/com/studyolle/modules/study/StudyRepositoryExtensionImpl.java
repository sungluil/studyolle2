package com.studyolle.modules.study;

import java.util.List;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;


public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {

	
	public StudyRepositoryExtensionImpl() {
		super(Study.class);
	}
	
	

	@Override
	public List<Study> findByKeyword(String keyword) {
		// TODO Auto-generated method stub
		QStudy study = QStudy.study;
		JPQLQuery<Study> query 
				= from(study).where(study.published.isTrue()
				.and(study.title.containsIgnoreCase(keyword))
				.or(study.tags.any().title.containsIgnoreCase(keyword))
				.or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword)));
		
		/** 위에 커리가 밑에로됨
		 *  select *
		 *    from study
		 *   where study.published = true
		 *     and study.title like '%spring%'
		 *      or exists(select * from tag where tag.title like '%spring%')
		 *      or exists(select * from zone where zone.local_name_of_city like '%spring%') 
		 */
		return query.fetch();
	}

}
