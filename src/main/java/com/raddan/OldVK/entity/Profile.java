package com.raddan.OldVK.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profiles")
public class Profile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @OneToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private LocalDate dob;

    @Column(columnDefinition = "text")
    private String bio;

    @Column
    private LocalDateTime registerAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private Boolean isPrivate;

    public void setUser(User user) {
        this.user = user;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setRegisterAt(LocalDateTime registerAt) {
        this.registerAt = registerAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Long getID() {
        return ID;
    }

    public User getUser() {
        return user;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getBio() {
        return bio;
    }

    public LocalDateTime getRegisterAt() {
        return registerAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }
}
