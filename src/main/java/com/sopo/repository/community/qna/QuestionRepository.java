package com.sopo.repository.community.qna;

import com.sopo.domain.community.qna.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {

    Optional<Question> findByIdAndAskerId(Long id, Long askerId);
}