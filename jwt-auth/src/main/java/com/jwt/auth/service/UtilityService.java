package com.jwt.auth.service;

public interface UtilityService {
    public boolean initialRoleAndPrivilege();
    public String normalMessageMapping(String code, String msgCode, String reason);

}
