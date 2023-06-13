package com.jwt.auth.repository;

import com.jwt.auth.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("TokenRepository")
public interface TokenRepository extends CrudRepository<Token, Long> {

    Token findByTokenValue(String findByTokenValue);

}
