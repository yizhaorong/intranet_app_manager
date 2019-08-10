package org.yzr.service;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.yzr.dao.AppDao;
import org.yzr.dao.WebHookDao;
import org.yzr.model.App;
import org.yzr.model.WebHook;
import org.yzr.vo.WebHookViewModel;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebHookService {

    @Resource
    private WebHookDao webHookDao;
    @Resource
    private AppDao appDao;

    @Transactional
    public WebHook save(WebHookViewModel viewModel) {
        App app = appDao.findById(viewModel.getAppId()).get();
        if (app != null) {
            WebHook webHook = new WebHook();
            BeanUtils.copyProperties(viewModel, webHook);
            webHook.setApp(app);
            webHook.setType(WebHook.WEB_HOOK_TYPE_DING_DING);
            return webHookDao.save(webHook);
        }
        return null;
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

    @Transactional
    public List<WebHook> findByAppId(String appId) {
        App app = appDao.findById(appId).get();
        if (app != null) {
            List<WebHook> webHookList = app.getWebHookList();
            List<WebHook> webHooks = new ArrayList<>();
            for (WebHook webHook :
                    webHookList) {
                webHooks.add(webHook);
            }
            return webHooks;
        }
        return new ArrayList<>();
    }

    public void update(WebHookViewModel viewModel) {
        WebHook webHook = webHookDao.findById(viewModel.getId()).get();
        if (webHook != null) {
            webHook.setName(viewModel.getName());
            webHook.setUrl(viewModel.getUrl());
            webHookDao.save(webHook);
        }
    }
}
