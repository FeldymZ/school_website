package com.school.api.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  private String nom;

  private String prenom;

  @Column(name = "photo", columnDefinition = "bytea")
  private byte[] photo;

  private String photoContentType; // ex: "image/jpeg", "image/png"

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Builder.Default
  @Column(nullable = false)
  private Boolean enabled = true;

  // Clés de permissions de menu — pertinent uniquement pour role == ADMIN
  @Builder.Default
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
          name = "user_menu_access",
          joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "menu_key", nullable = false)
  private Set<String> menuAccess = new HashSet<>();
}