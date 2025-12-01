package com.sopo.repository.community.post.cond;

import java.time.LocalDateTime;

public record PostSearchCond(
        Long memberId,      //내 글만 보기(마이페이지)
        String keyword,     //제목+내용 통합 검색
        LocalDateTime from, //작성일 시작
        LocalDateTime to    //작성일 끝
) {
    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }
}