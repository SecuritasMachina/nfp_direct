package com.ackdev.nfp;

import com.ackdev.nfp.model.Pub78;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface Pub78Repository extends PagingAndSortingRepository<Pub78, Integer> {

    Page<Pub78> findAll(Pageable pageable);

    @Query("SELECT t FROM Pub78 t where t.EIN = :id")
    Pub78 findById(@Param("id") String id);
}
