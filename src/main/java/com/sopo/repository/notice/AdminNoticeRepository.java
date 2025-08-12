package com.sopo.repository.notice;

import com.sopo.domain.notice.AdminNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminNoticeRepository extends JpaRepository<AdminNotice, Long> {
}
