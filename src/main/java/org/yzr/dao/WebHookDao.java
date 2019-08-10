package org.yzr.dao;

import org.springframework.data.repository.CrudRepository;
import org.yzr.model.WebHook;

public interface WebHookDao extends CrudRepository <WebHook, String > {

}
