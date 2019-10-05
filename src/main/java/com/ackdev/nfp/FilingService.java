package com.ackdev.nfp;

import com.ackdev.nfp.model.Filing;
import com.ackdev.nfp.model.Postcard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("FilingService")
public class FilingService {
    @Autowired
    FilingRepository filingRepository = null;

     public Filing findById(String id) {
         Filing ret =
                filingRepository.findById(id);
        return ret;


    }
}
