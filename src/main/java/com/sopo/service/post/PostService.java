package com.sopo.service.post;

import com.sopo.dto.post.request.PostCreateRequest;
import com.sopo.dto.post.request.PostUpdateRequest;
import com.sopo.dto.post.response.PostDetailResponse;
import com.sopo.dto.post.response.PostSummaryResponse;
import com.sopo.repository.community.post.cond.PostSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    Long create(Long memberId, PostCreateRequest request);

    void update(Long memberId, Long postId, PostUpdateRequest request);

    void delete(Long memberId, Long postId);

    PostDetailResponse get(Long postId);

    /**
     * 게시판 목록 조회
     * - 기본 게시판 화면 / 검색용
     * - cond.keyword / cond.from / cond.to 기반 필터
     * **/
    Page<PostSummaryResponse> search(PostSearchCond cond, Pageable pageable);

    /** 마이페이지 : 내가 작성한 글 목록
     * - 내부적으로는 PostSearchCond.memberId를 세팅해서 search()를 호출하는 형태가 될 가능성이 큼 */
    Page<PostSummaryResponse> getByMember(Long memberId, Pageable pageable);
}