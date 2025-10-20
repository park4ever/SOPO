-- [V5] Coupon 리팩토링 ② : 기존 데이터 이행/보정
-- 전제: V4에서 신규 컬럼/스펙이 추가되어 있음

-- 1) expiration_date -> valid_until 이행 (유지보수 이력: 과거 단일 만료일을 사용했음)
UPDATE coupon
   SET valid_until = expiration_date
 WHERE valid_until IS NULL;

-- 2) valid_from 채우기 (정책: 없으면 created_at로 세팅)
UPDATE coupon
   SET valid_from = COALESCE(valid_from, created_at);

-- 3) discount_value를 타입별로 분배
--   - 정액(FIXED)  : fixed_amount로
--   - 정률(RATE)   : percentage로 (정수 해석, 반올림/절삭 규칙 필요시 여기에서)
UPDATE coupon
   SET fixed_amount = discount_value
 WHERE discount_type = 'FIXED' AND fixed_amount IS NULL;

UPDATE coupon
   SET percentage = CAST(discount_value AS SIGNED)
 WHERE discount_type = 'RATE'  AND percentage IS NULL;

-- 4) (선택) 정률 cap 기본값 정책이 있다면 여기서 일괄 세팅
-- UPDATE coupon
--    SET max_discount_amount = 100000
--  WHERE discount_type = 'RATE' AND max_discount_amount IS NULL;

-- 5) 퍼센트 범위를 벗어난 기존 데이터(NOT 1..100)는 운영자가 확인하도록 남겨둔다.
--    필요 시 임시 보정(예: 0 -> 1, 120 -> 100)을 넣을 수 있으나, 보통은 리포트 후 수동 정정이 안전.
--    SELECT id, name, percentage FROM coupon WHERE percentage IS NOT NULL AND (percentage < 1 OR percentage > 100);