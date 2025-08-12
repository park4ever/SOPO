package com.sopo.repository.member;

import com.sopo.domain.member.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
