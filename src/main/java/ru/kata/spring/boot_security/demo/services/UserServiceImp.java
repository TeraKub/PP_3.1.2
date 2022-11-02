package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Set<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toSet());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean saveUser(User user) {
        if (findByUsername(user.getUsername()) != null) {
            return false;
        }
//        user.setRoles(Collections.singleton(new Role(1, "ROLE_USER")));
        if (user.getId() != 0) {
            User userFromDb = userRepository.getById(user.getId());
            if (!userFromDb.getPassword().equals(user.getPassword())) {
                user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userRepository.saveAndFlush(user);
        return true;
    }

    public User getUserById(int id) {
        return userRepository.getById(id);
    }

    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }



}
