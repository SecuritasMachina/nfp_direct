package com.ackdev.nfp;

import com.ackdev.nfp.model.Postcard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface PostcardRepository extends PagingAndSortingRepository<Postcard, Integer> {

    Page<Postcard> findAll(Pageable pageable);

    @Query("SELECT t FROM Postcard t where t.id = :id")
    Postcard findById(@Param("id") String id);

    @Query("SELECT t FROM Postcard t where t.company_name = :id")
    Postcard findByName(@Param("id") String id);

}
