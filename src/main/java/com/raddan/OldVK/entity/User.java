package com.raddan.OldVK.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Table(name = "users")
@Entity
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userID;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private LocalDate registerAt;

    @Column(name = "account_enable")
    private boolean enabled;

    @Column(name = "credentials_expired")
    private boolean credentialsNonExpired;

    @Column(name = "account_expired")
    private boolean accountNonExpired;

    @Column(name = "account_locked")
    private boolean locked;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friendship> friendships = new HashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return isEnabled() == user.isEnabled()
                && isCredentialsNonExpired() == user.isCredentialsNonExpired()
                && isAccountNonExpired() == user.isAccountNonExpired()
                && isLocked() == user.isLocked()
                && Objects.equals(getUserID(), user.getUserID())
                && Objects.equals(getUsername(), user.getUsername())
                && Objects.equals(getPassword(), user.getPassword())
                && Objects.equals(getRoles(), user.getRoles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getUserID(),
                getUsername(),
                getPassword(),
                isEnabled(),
                isCredentialsNonExpired(),
                isAccountNonExpired(),
                isLocked(),
                getRoles()
        );
    }


}
