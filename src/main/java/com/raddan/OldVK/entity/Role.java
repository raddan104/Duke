package com.raddan.OldVK.entity;

import com.raddan.OldVK.enums.RoleEnum;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "roles")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private RoleEnum roleEnum;

    @ManyToOne
    @JoinColumn(
            name = "user",
            nullable = false,
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "role_user_fk")
    )
    private User user;

    public void setRoleEnum(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getID() {
        return ID;
    }

    public RoleEnum getRoleEnum() {
        return roleEnum;
    }

    public User getUser() {
        return user;
    }

    public Role(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

}
