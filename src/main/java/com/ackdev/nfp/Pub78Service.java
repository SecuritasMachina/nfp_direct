package com.ackdev.nfp;

import com.ackdev.nfp.model.Filing;
import com.ackdev.nfp.model.Postcard;
import com.ackdev.nfp.model.Pub78;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("Pub78Service")
public class Pub78Service {
    @Autowired
    Pub78Repository pub78Repository = null;

    public List<Pub78> getPage(int pageNbr) {
        Pageable firstPageWithTwoElements = PageRequest.of(pageNbr, 50);
        Page<Pub78> all =
                pub78Repository.findAll(firstPageWithTwoElements);
        List<Pub78> myPage = all.getContent();
        return myPage;
    }

    public Pub78 findById(String id) {
        Pub78 ret =
                pub78Repository.findById(id);
        return ret;


    }
}
