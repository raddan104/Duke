package com.raddan.OldVK.entity;

import com.raddan.OldVK.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Table(name = "roles")
@Entity
@NoArgsConstructor
@Getter @Setter
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", updatable = false, nullable = false)
    private Long roleID;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private RoleEnum roleEnum;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "role_user_fk")
    )
    private User user;

    public Role(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

}
