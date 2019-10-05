package com.ackdev.nfp;

import com.ackdev.nfp.model.Postcard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("PostcardService")
public class PostcardService {
    @Autowired
    PostcardRepository postcardRepository = null;

    public List<Postcard> getPage(int pageNbr) {
        Pageable firstPageWithTwoElements = PageRequest.of(pageNbr, 50);
        Page<Postcard> all =
                postcardRepository.findAll(firstPageWithTwoElements);
        List<Postcard> myPage = all.getContent();
        return myPage;
    }

    public Postcard findById(String id) {
        Postcard ret =
                postcardRepository.findById(id);
        return ret;
    }

    public Postcard findByName(String id) {
        Postcard ret =
                postcardRepository.findByName(id);
        return ret;
    }
}
