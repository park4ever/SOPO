-- [V4] Coupon 리팩토링 ① : 신규 컬럼 추가 + 스펙 보정 (널 허용, 안전 단계)
-- 테이블: coupon
-- 목적: 이후 데이터 이행(V5), 제약 강화(V6)를 위한 준비

-- 1) 금액 스펙 통일 (min_order_price -> DECIMAL(15,2))
ALTER TABLE coupon
    MODIFY COLUMN min_order_price DECIMAL(15,2) NOT NULL;

-- 2) 신규 컬럼 추가 (널 허용)
ALTER TABLE coupon
    ADD COLUMN IF NOT EXISTS fixed_amount        DECIMAL(15,2) NULL AFTER discount_type,
    ADD COLUMN IF NOT EXISTS percentage          SMALLINT NULL AFTER fixed_amount,
    ADD COLUMN IF NOT EXISTS max_discount_amount DECIMAL(15,2) NULL AFTER percentage,
    ADD COLUMN IF NOT EXISTS valid_from          DATETIME NULL,
    ADD COLUMN IF NOT EXISTS valid_until         DATETIME NULL;

-- 3) 소프트 삭제 플래그(이미 있을 수 있음. 없다면 추가)
ALTER TABLE coupon
    ADD COLUMN IF NOT EXISTS is_deleted BIT NOT NULL DEFAULT 0;

-- 4) 인덱스(읽기 필터/배치용)
-- MySQL 8.x에서 IF NOT EXISTS 지원 버전이 애매하면 DROP 후 CREATE를 사용하거나
-- 이미 있으면 에러가 날 수 있으니 필요 시 수동 정리.
CREATE INDEX IF NOT EXISTS idx_coupon_is_deleted   ON coupon (is_deleted);
CREATE INDEX IF NOT EXISTS idx_coupon_valid_until  ON coupon (valid_until);
-- (옵션) 시작일 인덱스
-- CREATE INDEX IF NOT EXISTS idx_coupon_valid_from  ON coupon (valid_from);