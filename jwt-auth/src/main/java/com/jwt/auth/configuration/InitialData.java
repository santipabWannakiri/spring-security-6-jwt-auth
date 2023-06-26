package com.jwt.auth.configuration;

import com.jwt.auth.model.Privilege;
import com.jwt.auth.model.Role;
import com.jwt.auth.model.User;
import com.jwt.auth.repository.PrivilegeRepository;
import com.jwt.auth.repository.RoleRepository;
import com.jwt.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional //ACID
public class InitialData implements CommandLineRunner {
    private BCryptPasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private PrivilegeRepository privilegeRepository;
    private UserRepository userRepository;

    @Autowired
    public InitialData(BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Boolean isSuccess = initialRoleAndPrivilege();
    }

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
            System.out.println(e.getMessage());
            return false;
        }
    }
}
