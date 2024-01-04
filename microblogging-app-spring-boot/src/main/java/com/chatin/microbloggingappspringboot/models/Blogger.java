package com.chatin.microbloggingappspringboot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class Blogger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "blogger")
    List<Post> posts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_authority",
            joinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    Set<Authority> authorities;

}
