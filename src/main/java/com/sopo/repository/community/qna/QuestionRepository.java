package com.sopo.repository.community.qna;

import com.sopo.domain.community.qna.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
