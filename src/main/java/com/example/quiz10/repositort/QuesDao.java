package com.example.quiz10.repositort;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quiz10.entity.Ques;
import com.example.quiz10.entity.QuesId;

@Repository
public interface QuesDao extends JpaRepository<Ques, QuesId>{
	
	public void deleteByQuizId(int quizId);
	
	public List<Ques> findByQuizId(int quizId);

	public List<Ques> findByQuizIdIn(List<Integer> quizIdList);
	
	public List<Ques> findByQuizIdAndTypeNot(int quizId, String selectType);
}
