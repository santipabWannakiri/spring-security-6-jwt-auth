package com.jwt.auth.repository;


import com.jwt.auth.model.Privilege;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("PrivilegeRepository")
public interface PrivilegeRepository extends CrudRepository<Privilege, Long> {


}
