package com.projectA1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectA1.model.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

}
