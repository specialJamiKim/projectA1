package com.projectA1.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projectA1.model.Diary;
import com.projectA1.repository.DiaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {
	
	private final DiaryRepository diaryrepository;
	
	public List<Diary> findAll(){
		return diaryrepository.findAll();
	}
	
}
