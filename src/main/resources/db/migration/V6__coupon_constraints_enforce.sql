-- [V6] Coupon 리팩토링 ③ : NOT NULL/제약 강화 + 구식 컬럼 정리 (데이터 이행 후 수행)

-- 1) 기간 컬럼 NOT NULL 전환
ALTER TABLE coupon
    MODIFY COLUMN valid_from  DATETIME NOT NULL,
    MODIFY COLUMN valid_until DATETIME NOT NULL;

-- 2) (선택) CHECK 제약 (MySQL 8.x 이상에서만 실효성)
--    percentage는 1~100 (NULL은 허용: FIXED 타입일 수 있으므로)
-- ALTER TABLE coupon
--     ADD CONSTRAINT chk_coupon_percentage
--     CHECK (percentage IS NULL OR (percentage BETWEEN 1 AND 100));

-- 3) (선택) 구식 컬럼 제거 (완전히 전환을 마쳤을 때만)
-- ALTER TABLE coupon DROP COLUMN expiration_date;
-- ALTER TABLE coupon DROP COLUMN discount_value;