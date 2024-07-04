package com.raddan.OldVK.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private boolean isMailConfirmed;

    @Column
    private boolean isEnabled;

    @Column
    private boolean isCredentialsNonExpired;

    @Column
    private boolean isAccountNonExpired;

    @Column
    private boolean isLocked;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public User() { }

    public void addRole(Role role) {
        this.roles.add(role);
        role.setUser(this);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this
                .roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleEnum().toString()))
                .collect(Collectors.toSet());
    }

    public Long getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isMailConfirmed() {
        return isMailConfirmed;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public Profile getProfile() {
        return profile;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMailConfirmed(boolean mailConfirmed) {
        isMailConfirmed = mailConfirmed;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
