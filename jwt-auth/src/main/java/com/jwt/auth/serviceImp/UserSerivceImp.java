package com.jwt.auth.serviceImp;

import com.jwt.auth.model.Privilege;
import com.jwt.auth.model.Role;
import com.jwt.auth.model.User;
import com.jwt.auth.repository.RoleRepository;
import com.jwt.auth.repository.UserRepository;
import com.jwt.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserSerivceImp implements UserService, UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserSerivceImp(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findByUser(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }
        Set<Role> roles = user.getRoles();
        Set<Privilege> privileges = new HashSet<>();
        for (Role role : roles) {
            Set<Privilege> rolePrivileges = role.getPrivileges();
            privileges.addAll(rolePrivileges);
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        for (Privilege privilege : privileges) {
            grantedAuthorities.add(new SimpleGrantedAuthority(privilege.getName()));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }

    @Override
    public User createUser(User userInfo) {
        userInfo.setPassword(new BCryptPasswordEncoder().encode(userInfo.getPassword()));
        Role tempRole = roleRepository.findByName("USER");
        userInfo.setRoles(new HashSet<Role>(Set.of(tempRole)));
        userInfo.setIsActive(true);
        return userRepository.save(userInfo);
    }

}
