package com.jwt.auth.serviceImp;

import com.jwt.auth.model.Privilege;
import com.jwt.auth.model.Role;
import com.jwt.auth.model.User;
import com.jwt.auth.repository.PrivilegeRepository;
import com.jwt.auth.repository.RoleRepository;
import com.jwt.auth.repository.UserRepository;
import com.jwt.auth.service.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

@Service
@Transactional //ACID
public class UtilityServiceImp implements UtilityService, CommandLineRunner {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean initialRoleAndPrivilege() {
        try {
            //Initial Privilege Read, Write, Delete
            Privilege readPrivilege = new Privilege();
            readPrivilege.setName("READ");
            readPrivilege = privilegeRepository.save(readPrivilege);

            Privilege writePrivilege = new Privilege();
            writePrivilege.setName("WRITE");
            writePrivilege = privilegeRepository.save(writePrivilege);

            Privilege deletePrivilege = new Privilege();
            deletePrivilege.setName("DELETE");
            deletePrivilege = privilegeRepository.save(deletePrivilege);

            //Initial Role
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setPrivileges(Set.of(readPrivilege, writePrivilege));
            adminRole = roleRepository.save(adminRole);

            Role superAdminRole = new Role();
            superAdminRole.setName("SUPER_ADMIN");
            superAdminRole.setPrivileges(Set.of(readPrivilege, writePrivilege, deletePrivilege));
            superAdminRole = roleRepository.save(superAdminRole);

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setPrivileges(Set.of(readPrivilege));
            userRole = roleRepository.save(userRole);
            System.out.println("Initial Role and Privilege Successfully");

            //Initial Account
            User superAccount = new User();
            superAccount.setUsername("super");
            superAccount.setEmail("superadmin@gmail.com");
            superAccount.setPassword(passwordEncoder.encode("zxcv123456"));
            superAccount.setRoles(Set.of(superAdminRole, adminRole));
            superAccount.setIsActive(true);
            userRepository.save(superAccount);

            User adminAccount = new User();
            adminAccount.setUsername("admin");
            adminAccount.setEmail("admin@gmail.com");
            adminAccount.setPassword(passwordEncoder.encode("zxcv123456"));
            adminAccount.setRoles(Set.of(adminRole));
            adminAccount.setIsActive(true);
            userRepository.save(adminAccount);

            User userAccount = new User();
            userAccount.setUsername("user1");
            userAccount.setEmail("user1@gmail.com");
            userAccount.setPassword(passwordEncoder.encode("zxcv123456")); //alternative without call Bean userAccount.setPassword(new BCryptPasswordEncoder().encode("zxcv"));
            userAccount.setRoles(Set.of(userRole));
            userAccount.setIsActive(true);
            userRepository.save(userAccount);

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public String normalMessageMapping(String code, String msgCode, String reason) {
        String response = "{\r\n"
                + "    \"code\": \"" + code + "\",\r\n"
                + "    \"msgCode\": \"" + msgCode + "\",\r\n"
                + "    \"reason\": \"" + reason + "\"\r\n"
                + "}";
        return response;
    }

    @Override
    public String jwtResponseMessageMapping(String code, String msgCode, String token, Date expire) {
        String response = "{\r\n"
                + "    \"code\": \"" + code + "\",\r\n"
                + "    \"msgCode\": \"" + msgCode + "\",\r\n"
                + "\"data\": {"
                + "\"token\": \"" + token + "\",\r\n"
                + "\"expiresIn\": \"" + expire + "\"\r\n"
                + "}"
                + "}";
        return response;
    }

    @Override
    public void run(String... args) throws Exception {
        Boolean isSuccess = initialRoleAndPrivilege();
    }
}
