package org.top.librarymvcapp.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="user_t")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="login_t", nullable = false)
    private String login;

    @Column(name="password_t", nullable = false)
    private String password;    //захэшированный пароль

    @Column(name="role_t", nullable = false)
    private String role;
}
