package com.studyolle.modules.tag;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {
	
	private final TagRepository tagRepository;

	public Tag findOrCreateNew(String tagTitle) {
		Tag tag = tagRepository.findByTitle(tagTitle);
		if(tag == null) {
			tagRepository.save(Tag.builder().title(tagTitle).build());
		}
		return tag;
	}

}
