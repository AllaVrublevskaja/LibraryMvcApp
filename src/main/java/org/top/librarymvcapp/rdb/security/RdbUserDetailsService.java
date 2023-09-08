package org.top.librarymvcapp.rdb.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.top.librarymvcapp.entity.User;
import org.top.librarymvcapp.rdb.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RdbUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. найти пользователя в БД
        Optional<User> user = userRepository.findByLogin(username);
        // 2. упаковать в объект UserDetails
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }
        RdbUserDetails userDetails = new RdbUserDetails();
        userDetails.setUser(user.get());
        return userDetails;
    }
}
