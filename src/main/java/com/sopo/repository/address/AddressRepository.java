package com.sopo.repository.address;

import com.sopo.domain.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    //내 주소 단건 조회
    Optional<Address> findByIdAndMemberId(Long id, Long memberId);

    //내 주소 목록(기본배송지 우선, 최신 ID 역순)
    List<Address> findAllByMemberIdOrderByIsDefaultDescIdDesc(Long memberId);

    //기본배송지 일괄 해제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Address a set a.isDefault = false where a.member.id = :memberId and a.isDefault = true")
    int clearDefaultByMemberId(@Param("memberId") Long memberId);

    boolean existsByMemberIdAndIsDefaultTrue(Long memberId);

    long countByMemberId(Long memberId);

    Optional<Address> findFirstByMemberIdOrderByIdDesc(Long memberId);
}