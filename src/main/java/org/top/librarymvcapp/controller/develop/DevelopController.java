package org.top.librarymvcapp.controller.develop;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.top.librarymvcapp.entity.User;
import org.top.librarymvcapp.rdb.repository.UserRepository;

@RestController
@RequestMapping("develop")
@RequiredArgsConstructor
public class DevelopController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // метод создания администратора (логин админ, пароль админ)
    @GetMapping("create-admin")
    public String createAdmin() {
        String login = "admin";
        String password = "admin";
        password = passwordEncoder.encode(password);
        String role = "ADMIN";
        if (userRepository.findByLogin(login).isPresent()) {
            return "администратор уже создан";
        }
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);
        return "администратор успешно создан";
    }

    // метод создания пользователя (логин user, пароль 123456)
    @GetMapping("create-user")
    public String createUser() {
        String login = "user";
        String password = "123456";
        password = passwordEncoder.encode(password);
        String role = "USER";
        if (userRepository.findByLogin(login).isPresent()) {
            return "пользователь уже создан";
        }
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);
        return "пользователь успешно создан";
    }
}
