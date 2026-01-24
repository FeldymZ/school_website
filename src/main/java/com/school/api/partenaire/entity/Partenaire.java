package com.school.api.partenaire.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "partenaires",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = "displayOrder")
  }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partenaire {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String logoUrl;

  private String websiteUrl;

  @Column(nullable = false)
  private Integer displayOrder;

  @Column(nullable = false)
  private Boolean enabled;
}
