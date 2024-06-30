package com.raddan.OldVK.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "profiles")
@Getter @Setter
public class Profile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "text")
    private String bio;

    @Column
    private LocalDate dob;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

}
