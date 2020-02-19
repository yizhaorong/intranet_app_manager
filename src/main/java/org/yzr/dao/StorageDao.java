package org.yzr.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.yzr.model.Storage;

public interface StorageDao extends CrudRepository<Storage, String> {

    @Query("select s from Storage s where s.key=:key")
    public Storage findByKey(@Param("key") String key);
}
