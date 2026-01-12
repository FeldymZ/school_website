package com.school.api.common.storage;

import com.school.api.banner.entity.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

  private static final String BASE_DIR = "uploads";

  /* ============================
     🖼️ BANNERS
     ============================ */

  public String storeBannerMedia(MultipartFile file, MediaType type) {

    validateFile(file);

    String folder = type == MediaType.IMAGE
      ? "banners/images"
      : "banners/videos";

    return store(file, folder);
  }

  /* ============================
     🎓 FORMATIONS INITIALES
     ============================ */

  public String storeFormationCover(MultipartFile file) {
    validateImage(file);
    return store(file, "formations/initiale/covers");
  }

  public String storeFormationGalleryImage(MultipartFile file) {
    validateImage(file);
    return store(file, "formations/initiale/gallery");
  }

  public String storeFormationPdf(MultipartFile file) {
    validatePdf(file);
    return store(file, "formations/initiale/pdfs");
  }

  /* ============================
     📰 ACTUALITÉS (AJOUTÉ)
     ============================ */

  /** Image principale (cover) d’une actualité */
  public String storeActualiteCover(MultipartFile file) {
    validateImage(file);
    return store(file, "actualites/covers");
  }

  /** Images galerie d’une actualité */
  public String storeActualiteGalleryImage(MultipartFile file) {
    validateImage(file);
    return store(file, "actualites/gallery");
  }

  /* ============================
     ⚙️ CORE COMMUN
     ============================ */

  private String store(MultipartFile file, String subDir) {

    try {
      String extension = getExtension(file.getOriginalFilename());
      String filename = UUID.randomUUID() + "." + extension;

      Path path = Path.of(BASE_DIR, subDir, filename);
      Files.createDirectories(path.getParent());
      Files.write(path, file.getBytes());

      // URL publique
      return "/files/" + subDir + "/" + filename;

    } catch (IOException e) {
      throw new RuntimeException("Erreur lors du stockage du fichier", e);
    }
  }

  /* ============================
     🔍 VALIDATIONS
     ============================ */

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Fichier manquant");
    }
  }

  private void validateImage(MultipartFile file) {
    validateFile(file);
    if (file.getContentType() == null ||
      !file.getContentType().startsWith("image/")) {
      throw new IllegalArgumentException("Le fichier doit être une image");
    }
  }

  private void validatePdf(MultipartFile file) {
    validateFile(file);
    if (!"application/pdf".equals(file.getContentType())) {
      throw new IllegalArgumentException("Le fichier doit être un PDF");
    }
  }

  private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      throw new IllegalArgumentException("Nom de fichier invalide");
    }
    return filename.substring(filename.lastIndexOf('.') + 1);
  }

  /* ============================
   COMMENTAIRES
   ============================ */

  public String storeCommentaireAvatar(MultipartFile file) {
    validateImage(file);
    return store(file, "commentaires/authors");
  }

  /* ============================
   PARTENAIRES
   ============================ */

  public String storePartenaireLogo(MultipartFile file) {
    validateImage(file);
    return store(file, "partenaires/logos");
  }

  public String storeContactReplyAttachment(MultipartFile file) {

    validateFile(file);

    if (file.getSize() > 7_000_000) {
      throw new IllegalArgumentException("Fichier trop volumineux (7Mo max)");
    }

    return store(file, "contact/replies");
  }



}
