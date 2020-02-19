package org.yzr.service;


import org.springframework.stereotype.Service;
import org.yzr.dao.StorageDao;
import org.yzr.model.Storage;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
public class StorageService {
    @Resource
    private StorageDao storageDao;

    @Transactional
    public Storage save(Storage storage) {
        return this.storageDao.save(storage);
    }

    @Transactional
    public Storage findByKey(String key) {
        return this.storageDao.findByKey(key);
    }
}
