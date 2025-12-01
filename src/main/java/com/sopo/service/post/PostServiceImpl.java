package com.sopo.service.post;

import com.sopo.domain.community.post.Post;
import com.sopo.domain.member.Member;
import com.sopo.dto.post.request.PostCreateRequest;
import com.sopo.dto.post.request.PostUpdateRequest;
import com.sopo.dto.post.response.PostDetailResponse;
import com.sopo.dto.post.response.PostSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.community.post.PostRepository;
import com.sopo.repository.community.post.cond.PostSearchCond;
import com.sopo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long create(Long memberId, PostCreateRequest request) {
        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = Post.create(author, request.title(), request.content());

        postRepository.save(post);
        return post.getId();
    }

    @Override
    @Transactional
    public void update(Long memberId, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        //소유자 검증
        if (!post.isOwner(memberId)) {
            throw new BusinessException(ErrorCode.POST_FORBIDDEN_ACCESS);
        }
        //낙관적 락 검증
        if (!Objects.equals(request.version(), post.getVersion())) {
            throw new BusinessException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }

        post.updateContent(request.title(), request.content());
    }

    @Override
    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isOwner(memberId)) {
            throw new BusinessException(ErrorCode.POST_FORBIDDEN_ACCESS);
        }

        //지금은 물리적으로 삭제, 추후에 필요하면 isDeleted 플래그 도입 예정(소프트 삭제)
        postRepository.delete(post);
    }

    @Override
    public PostDetailResponse get(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        return toDetailResponse(post);
    }

    @Override
    public Page<PostSummaryResponse> search(PostSearchCond cond, Pageable pageable) {
        return postRepository.search(cond, pageable)
                .map(this::toSummaryResponse);
    }

    @Override
    public Page<PostSummaryResponse> getByMember(Long memberId, Pageable pageable) {
        PostSearchCond cond = new PostSearchCond(
                memberId,
                null,
                null,
                null
        );
        return postRepository.search(cond, pageable)
                .map(this::toSummaryResponse);
    }

    private PostSummaryResponse toSummaryResponse(Post post) {
        return new PostSummaryResponse(
                post.getId(),
                post.getAuthorId(),
                post.getAuthor() != null ? post.getAuthor().getName() : null,
                post.getTitle(),
                post.getCreatedDate(),
                post.getLastModifiedDate()
        );
    }

    private PostDetailResponse toDetailResponse(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getAuthorId(),
                post.getAuthor() != null ? post.getAuthor().getName() : null,
                post.getTitle(),
                post.getContent(),
                post.getCreatedDate(),
                post.getLastModifiedDate(),
                post.getVersion()
        );
    }
}