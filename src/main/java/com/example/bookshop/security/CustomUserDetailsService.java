package com.example.bookshop.security;

import com.example.bookshop.dao.UserDao;
import com.example.bookshop.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Сервис для загрузки пользовательских данных для Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserDao userDao;

    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
        log.info("CustomUserDetailsService инициализирован");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Попытка загрузки пользователя по username: {}", username);

        return userDao.findByUsername(username)
                .map(this::buildUserDetails)
                .orElseThrow(() -> {
                    log.warn("Пользователь с username {} не найден", username);
                    return new UsernameNotFoundException("Пользователь не найден: " + username);
                });
    }

    private UserDetails buildUserDetails(User user) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase())
        );

        log.debug("Пользователь загружен: {}, роль: {}", user.getUsername(), user.getRole());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }
}
