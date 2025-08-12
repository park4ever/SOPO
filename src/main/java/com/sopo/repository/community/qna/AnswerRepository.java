package com.sopo.repository.community.qna;

import com.sopo.domain.community.qna.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
