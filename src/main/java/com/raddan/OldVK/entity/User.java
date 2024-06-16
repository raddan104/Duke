package com.raddan.OldVK.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@Data
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String avatar;
    @Column
    private String roles;
    @Column(name = "registered_at", nullable = false)
    private LocalDate registeredAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    public User() {
        this.registeredAt = LocalDate.now();
    }
}
