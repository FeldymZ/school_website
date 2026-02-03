package com.school.api.actualite.migration;

import com.school.api.actualite.entity.Actualite;
import com.school.api.actualite.repository.ActualiteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("migration")
@RequiredArgsConstructor
public class ActualiteSlugMigrationRunner implements CommandLineRunner {

  private final ActualiteRepository repository;

  @Override
  public void run(String... args) {

    System.out.println("🚀 Migration JAVA des slugs (accents nettoyés)");

    repository.findAll().forEach(actualite -> {

      String slug = generateUniqueSlug(actualite.getTitle());

      actualite.setSlug(slug);
      repository.save(actualite);

      System.out.println(
        "✅ Actualité ID " + actualite.getId() + " → " + slug
      );
    });

    System.out.println("🏁 Migration des slugs TERMINÉE");
  }

  private String generateUniqueSlug(String title) {

    String base = StringUtils.stripAccents(title)
      .toLowerCase()
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("(^-|-$)", "");

    String slug = base;
    int i = 1;

    while (repository.existsBySlug(slug)) {
      slug = base + "-" + i++;
    }

    return slug;
  }
}
