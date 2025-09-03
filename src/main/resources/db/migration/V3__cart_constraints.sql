-- (1) cart.member_id NOT NULL 보장 (이미 not null이라면 스킵되거나 no-op)
ALTER TABLE cart
    MODIFY COLUMN member_id BIGINT NOT NULL;

-- (2) cart.member_id 유니크 제약 (1:1 보장)
ALTER TABLE cart
    ADD CONSTRAINT uk_cart_member UNIQUE (member_id);

-- (3) cart_item (cart_id, item_option_id) 복합 유니크 (동일 옵션 중복 차단)
ALTER TABLE cart_item
    ADD CONSTRAINT uk_cart_item_option UNIQUE (cart_id, item_option_id);