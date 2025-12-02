package com.sopo.service.notice;

import com.sopo.domain.notice.AdminNotice;
import com.sopo.dto.notice.request.AdminNoticeCreateRequest;
import com.sopo.dto.notice.request.AdminNoticeUpdateRequest;
import com.sopo.dto.notice.response.AdminNoticeDetailResponse;
import com.sopo.dto.notice.response.AdminNoticeSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.notice.AdminNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final AdminNoticeRepository adminNoticeRepository;

    @Override
    public Long create(AdminNoticeCreateRequest request) {
        AdminNotice notice = AdminNotice.create(
                request.title(),
                request.content(),
                request.pinned()
        );

        AdminNotice saved = adminNoticeRepository.save(notice);
        return saved.getId();
    }

    @Override
    public void update(Long noticeId, AdminNoticeUpdateRequest request) {
        AdminNotice notice = adminNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOTICE_NOT_FOUND));

        if (!Objects.equals(request.version(), notice.getVersion())) {
            throw new BusinessException(ErrorCode.ADMIN_NOTICE_UPDATE_CONFLICT);
        }

        try {
            notice.updateContent(request.title(), request.content());

            //고정 여부 변경
            if (request.pinned()) {
                notice.pin();
            } else {
                notice.unpin();
            }
        } catch (OptimisticLockingFailureException e) {
            throw new BusinessException(ErrorCode.ADMIN_NOTICE_UPDATE_CONFLICT);
        }
    }

    @Override
    public void delete(Long noticeId) {
        AdminNotice notice = adminNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOTICE_NOT_FOUND));

        adminNoticeRepository.delete(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminNoticeDetailResponse getById(Long noticeId) {
        AdminNotice notice = adminNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOTICE_NOT_FOUND));

        return toDetailResponse(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminNoticeSummaryResponse> getAll() {
        List<AdminNotice> notices = adminNoticeRepository.findAll(
                Sort.by(
                        Sort.Order.desc("pinned"),
                        Sort.Order.desc("createdDate")
                )
        );

        return notices.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    private AdminNoticeSummaryResponse toSummaryResponse(AdminNotice notice) {
        return new AdminNoticeSummaryResponse(
                notice.getId(),
                notice.getTitle(),
                notice.isPinned(),
                notice.getCreatedDate()
        );
    }

    private AdminNoticeDetailResponse toDetailResponse(AdminNotice notice) {
        return new AdminNoticeDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.isPinned(),
                notice.getCreatedDate(),
                notice.getLastModifiedDate(),
                notice.getVersion()
        );
    }
}