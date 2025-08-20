CREATE INDEX idx_address_member ON `address` (member_id);
CREATE INDEX idx_address_member_default ON `address` (member_id, is_default);