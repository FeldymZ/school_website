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

  // 🆕 à ajouter dans User.java, aux côtés des champs existants (email, password, role, enabled, menuAccess...)

  private String nom;

  private String prenom;

  private String photoUrl; // URL de la photo de profil, ex: "https://cdn.exemple.com/avatars/123.jpg"

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Builder.Default
  @Column(nullable = false)
  private Boolean enabled = true;

  // 🆕 Clés de permissions de menu — pertinent uniquement pour role == ADMIN
  @Builder.Default
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
          name = "user_menu_access",
          joinColumns = @JoinColumn(name = "user_id")
  )
  @Column(name = "menu_key", nullable = false)
  private Set<String> menuAccess = new HashSet<>();
}