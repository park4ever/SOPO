package com.sopo.repository.item;

import com.sopo.domain.item.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {

    @Query("""
           select c from ItemCategory c
           where (:includeDeleted = true or c.isDeleted = false)
           """)
    List<ItemCategory> findAllForTree(@Param("includeDeleted") boolean includeDeleted);

    // 형제(같은 부모) 안에서 이름 중복 존재 여부(삭제된 항목 제외)
    @Query("""
           select case when count(c) > 0 then true else false end
           from ItemCategory c
           where c.isDeleted = false
             and c.name = :name
             and (
                    (:parentId is null and c.parent is null)
                 or (:parentId is not null and c.parent.id = :parentId)
             )
           """)
    boolean existsSiblingName(@Param("parentId") Long parentId, @Param("name") String name);

    // 자기 자신을 제외한 형제 중복 검사(rename/move용)
    @Query("""
           select case when count(c) > 0 then true else false end
           from ItemCategory c
           where c.isDeleted = false
             and c.name = :name
             and c.id <> :selfId
             and (
                    (:parentId is null and c.parent is null)
                 or (:parentId is not null and c.parent.id = :parentId)
             )
           """)
    boolean existsSiblingNameExcludingSelf(@Param("selfId") Long selfId,
                                           @Param("parentId") Long parentId,
                                           @Param("name") String name);

    // 특정 부모의 자식 목록(삭제 포함 여부 옵션)
    @Query("""
           select c from ItemCategory c
           where (
                    (:parentId is null and c.parent is null)
                 or (:parentId is not null and c.parent.id = :parentId)
                 )
             and (:includeDeleted = true or c.isDeleted = false)
           order by c.name asc
           """)
    List<ItemCategory> findChildren(@Param("parentId") Long parentId,
                                    @Param("includeDeleted") boolean includeDeleted);
}