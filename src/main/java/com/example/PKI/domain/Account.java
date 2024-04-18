package com.example.PKI.domain;

import com.example.PKI.domain.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
@SQLDelete(sql = "UPDATE accounts SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinTable(name = "account_role",
            joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @Column(name="deleted")
    private boolean deleted = Boolean.FALSE;

    public Account(Long id, String username, String password, Status status, List<Role> roles, boolean deleted) {
        this.id = id;
        this.username =username;
        this.password = password;
        this.status = status;
        this.roles = roles;
        this.deleted = deleted;
    }

    public Account(String username, String password, Status status, List<Role> roles, boolean deleted) {
        this.username =username;
        this.password = password;
        this.status = status;
        this.roles = roles;
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id  +
                ",username" + username +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", role=" + roles +
                ", deleted=" + deleted +
                '}';
    }
}
