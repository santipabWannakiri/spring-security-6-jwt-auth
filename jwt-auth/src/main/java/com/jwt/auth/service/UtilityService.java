package com.jwt.auth.service;

import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface UtilityService {
    public boolean initialRoleAndPrivilege();
    public String normalMessageMapping(String code, String msgCode, String reason);

    public String jwtResponseMessageMapping(String code, String msgCode, String token, Date expire);

}
