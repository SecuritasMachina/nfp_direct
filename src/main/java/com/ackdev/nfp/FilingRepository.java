package com.ackdev.nfp;

import com.ackdev.nfp.model.Filing;
import com.ackdev.nfp.model.Postcard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface FilingRepository extends PagingAndSortingRepository<Filing, Integer> {

    Page<Filing> findAll(Pageable pageable);

    @Query("SELECT t FROM Filing t where t.EIN = :id")
    Filing findById(@Param("id") String id);
}
