package com.sopo.service.notice;

import com.sopo.dto.notice.request.AdminNoticeCreateRequest;
import com.sopo.dto.notice.request.AdminNoticeUpdateRequest;
import com.sopo.dto.notice.response.AdminNoticeDetailResponse;
import com.sopo.dto.notice.response.AdminNoticeSummaryResponse;

import java.util.List;

public interface AdminNoticeService {

    Long create(AdminNoticeCreateRequest request);

    void update(Long noticeId, AdminNoticeUpdateRequest request);

    void delete(Long noticeId);

    AdminNoticeDetailResponse getById(Long noticeId);

    List<AdminNoticeSummaryResponse> getAll();
}