package org.yzr.service;


import org.springframework.stereotype.Service;
import org.yzr.dao.WebHookDao;
import org.yzr.model.WebHook;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
public class WebHookService {

    @Resource
    private WebHookDao webHookDao;

    @Transactional
    public WebHook save(WebHook webHook) {
        return this.webHookDao.save(webHook);
    }

    @Transactional
    public WebHook get(String id) {
        WebHook webHook = this.webHookDao.findById(id).get();
        return webHook;
    }

    @Transactional
    public void deleteById(String id) {
        WebHook webHook = this.webHookDao.findById(id).get();
        if (webHook != null) {
            this.webHookDao.deleteById(id);
        }

    }
}
