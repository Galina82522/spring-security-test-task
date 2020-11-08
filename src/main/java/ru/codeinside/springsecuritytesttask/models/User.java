package ru.codeinside.springsecuritytesttask.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = @Index(name = "IX_LOGIN", columnList = "login"))
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "login", nullable = false, unique = true, updatable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles_rel",
            joinColumns =
            @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns =
            @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    )
    private List<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<Note> notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_state", nullable = false)
    private AccessState accessState;
}
