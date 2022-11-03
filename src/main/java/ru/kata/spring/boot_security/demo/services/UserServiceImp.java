package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.List;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

//    @Override
//    @Transactional
//    // метод из UserDetailsService
//    // получает какое-то имя пользователя и по нему возвращает самого юзера
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Получаем из бд пользователя по пришедшему имени
//        User user = findByUsername(username);
//        if (user == null) { // Если такого пользователя нет - эксепшн
//            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
//        } // создаём и возвращаем нового юзера spring-security с именем, паролем и ролями нашего юзера
//        // такой вид юзера требует UserDetails
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(), user.getPassword(), user.getAuthorities());
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }
        return user;
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public boolean saveUser(User user) {
        if (findByUsername(user.getUsername()) != null && user.getId() == 0) {
            System.out.println(user.getId());
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

    @Override
    public User getUserById(int id) {
        return userRepository.getById(id);
    }

    @Override
    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
