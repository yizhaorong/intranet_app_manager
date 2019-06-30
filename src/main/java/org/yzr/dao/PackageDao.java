package org.yzr.dao;

import org.springframework.data.repository.CrudRepository;
import org.yzr.model.Package;

public interface PackageDao extends CrudRepository <Package, String > {

}
