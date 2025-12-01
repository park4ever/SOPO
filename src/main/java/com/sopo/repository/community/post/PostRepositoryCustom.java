package com.sopo.repository.community.post;

import com.sopo.domain.community.post.Post;
import com.sopo.repository.community.post.cond.PostSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<Post> search(PostSearchCond cond, Pageable pageable);
}