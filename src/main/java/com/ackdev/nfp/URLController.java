package com.ackdev.nfp;

import com.ackdev.nfp.model.Postcard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/rest")
class URLController {
    //private PostcardRepository repository = null;

    //public URLController() {
      //  repository = new PostcardRepository();
    //}

    @Autowired
    private static final Logger log = LoggerFactory.getLogger(URLController.class);

    private String getOwner(HttpServletRequest request) {
        String ret = "UNKNOWN";
        Principal p = request.getUserPrincipal();
        if (p != null) {
            log.debug("PrincipalUser : " + p.getName());
            ret = p.getName();
        } else {
            log.debug("PrincipalUser is null");
            log.debug("request.getRemoteUser : " + request.getRemoteUser());
        }
        return ret;

    }

    @GetMapping("/showByOwner")
    public Collection<Postcard> showByOwner(HttpServletRequest request) {
        String owner = getOwner(request);
        return null; //repository.findByOwner(owner);
    }

    @GetMapping("/showDeletedByOwner")
    public Collection<Postcard> showDeletedByOwner(HttpServletRequest request) {
        String owner = getOwner(request);
        return null;//repository.findDeletedByOwner(owner);
    }

    @GetMapping("/restore/{id}")
    public void restore(@PathVariable String id, HttpServletRequest request) throws IOException {
        String owner = getOwner(request);
        //repository.restoreByShortName(id, owner);
    }



    @PostMapping(path = "/save", consumes = "application/json", produces = "application/json")
    public void save(@RequestBody Postcard shortenedURL, HttpServletRequest request) {
        String owner = getOwner(request);

        //shortenedURL.setOwner(owner);
       // repository.saveObj(shortenedURL);
    }
}
