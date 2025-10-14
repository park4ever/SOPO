package com.sopo.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfig implements PageableHandlerMethodArgumentResolverCustomizer {
    @Override
    public void customize(PageableHandlerMethodArgumentResolver resolver) {
        //전역 기본값 : page = 0, size = 20, sort = id,DESC
        resolver.setFallbackPageable(PageRequest.of(0, 20, Sort.by(Sort.Order.desc("id"))));
        //안전 상한
        resolver.setMaxPageSize(200);
        //0부터 시작(true -> 1부터)
        resolver.setOneIndexedParameters(false);
    }
}