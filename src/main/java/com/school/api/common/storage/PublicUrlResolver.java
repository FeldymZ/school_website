package com.school.api.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PublicUrlResolver {

  @Value("${app.public-url}")
  private String publicUrl;

  /**
   * Transforme un chemin relatif (/files/...) en URL absolue
   */
  public String toAbsoluteUrl(String path) {

    if (path == null || path.isBlank()) {
      return null;
    }

    // Déjà une URL complète
    if (path.startsWith("http://") || path.startsWith("https://")) {
      return path;
    }

    // Normalisation
    if (!path.startsWith("/")) {
      path = "/" + path;
    }

    return publicUrl + path;
  }
}
