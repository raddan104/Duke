package com.raddan.OldVK.entity;

import com.raddan.OldVK.enums.Roles;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String avatar;
    @Enumerated(EnumType.STRING)
    @Column
    private Roles role;
}
