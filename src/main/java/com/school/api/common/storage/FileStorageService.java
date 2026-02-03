package com.school.api.common.storage;

import com.school.api.banner.entity.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

  // 🔑 DOSSIER PARTAGÉ AVEC NGINX VIA DOCKER
  private static final String BASE_DIR = "/files";

  /* ============================
     LIMITES
     ============================ */

  private static final long MAX_IMAGE_SIZE = 10_000_000;   // 10 Mo
  private static final long MAX_VIDEO_SIZE = 30_000_000;  // 30 Mo
  private static final long MAX_PDF_SIZE   = 10_000_000;  // 10 Mo

  private static final Set<String> IMAGE_EXTENSIONS =
    Set.of("jpg", "jpeg", "png", "webp");

  private static final Set<String> VIDEO_EXTENSIONS =
    Set.of("mp4", "webm");

  /* ============================
     🖼️ BANNERS
     ============================ */

  public String storeBannerMedia(MultipartFile file, MediaType type) {

    validateFile(file);

    if (type == MediaType.IMAGE) {
      validateImage(file);
      validateSize(file, MAX_IMAGE_SIZE);
      return store(file, "banners/images");
    }

    if (type == MediaType.VIDEO) {
      validateVideo(file);
      validateSize(file, MAX_VIDEO_SIZE);
      return store(file, "banners/videos");
    }

    throw new IllegalArgumentException("Type média non supporté");
  }

  /* ============================
     🎓 FORMATIONS INITIALES
     ============================ */

  public String storeFormationCover(MultipartFile file) {
    validateImage(file);
    validateSize(file, MAX_IMAGE_SIZE);
    return store(file, "formations/initiale/covers");
  }

  public String storeFormationGalleryImage(MultipartFile file) {
    validateImage(file);
    validateSize(file, MAX_IMAGE_SIZE);
    return store(file, "formations/initiale/gallery");
  }

  public String storeFormationPdf(MultipartFile file) {
    validatePdf(file);
    validateSize(file, MAX_PDF_SIZE);
    return store(file, "formations/initiale/pdfs");
  }

  /* ============================
     📰 ACTUALITÉS
     ============================ */

  public String storeActualiteCover(MultipartFile file) {
    validateImage(file);
    validateSize(file, MAX_IMAGE_SIZE);
    return store(file, "actualites/covers");
  }

  public String storeActualiteGalleryImage(MultipartFile file) {
    validateImage(file);
    validateSize(file, MAX_IMAGE_SIZE);
    return store(file, "actualites/gallery");
  }

  /* ============================
     💬 COMMENTAIRES
     ============================ */

  public String storeCommentaireAvatar(MultipartFile file) {
    validateImage(file);
    validateSize(file, MAX_IMAGE_SIZE);
    return store(file, "commentaires/authors");
  }

  /* ============================
     🤝 PARTENAIRES
     ============================ */

  public String storePartenaireLogo(MultipartFile file) {
    validateImage(file);
    validateSize(file, MAX_IMAGE_SIZE);
    return store(file, "partenaires/logos");
  }

  /* ============================
     ✉️ CONTACT
     ============================ */

  public String storeContactReplyAttachment(MultipartFile file) {
    validateFile(file);
    validateSize(file, 7_000_000);
    return store(file, "contact/replies");
  }

  /* ============================
     🏭 VIE ÉTUDIANTE – VISITES ENTREPRISE
     ============================ */

  public String storeVisiteEntrepriseImage(MultipartFile file) {
    validateImage(file);
    validateSize(file, MAX_IMAGE_SIZE);
    return store(file, "vie-etudiante/visites-entreprise");
  }

  /* ============================
     🗑️ SUPPRESSION FICHIER (AJOUT)
     ============================ */

  public void delete(String publicUrl) {

    if (publicUrl == null || publicUrl.isBlank()) {
      return;
    }

    try {
      // Exemple publicUrl : /files/actualites/gallery/xxx.jpg
      if (!publicUrl.startsWith("/files/")) {
        return;
      }

      Path path = Path.of(publicUrl);
      Files.deleteIfExists(path);

    } catch (IOException e) {
      // ⚠️ On ne casse jamais le métier pour un fichier
      System.err.println(
        "❌ Impossible de supprimer le fichier : " + publicUrl
      );
    }
  }

  /* ============================
     ⚙️ CORE COMMUN
     ============================ */

  private String store(MultipartFile file, String subDir) {

    try {
      String extension = getExtension(file.getOriginalFilename());
      String filename = UUID.randomUUID() + "." + extension;

      Path directory = Path.of(BASE_DIR, subDir);
      Files.createDirectories(directory);

      Path target = directory.resolve(filename);
      file.transferTo(target.toFile());

      // 🌍 URL publique exposée par NGINX
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

    String ext = getExtension(file.getOriginalFilename());
    if (!IMAGE_EXTENSIONS.contains(ext)) {
      throw new IllegalArgumentException("Image non supportée");
    }
  }

  private void validateVideo(MultipartFile file) {
    validateFile(file);

    String ext = getExtension(file.getOriginalFilename());
    if (!VIDEO_EXTENSIONS.contains(ext)) {
      throw new IllegalArgumentException("Vidéo non supportée");
    }
  }

  private void validatePdf(MultipartFile file) {
    validateFile(file);

    String ext = getExtension(file.getOriginalFilename());
    if (!"pdf".equals(ext)) {
      throw new IllegalArgumentException("Le fichier doit être un PDF");
    }
  }

  private void validateSize(MultipartFile file, long max) {
    if (file.getSize() > max) {
      throw new IllegalArgumentException(
        "Fichier trop volumineux (max " + (max / 1_000_000) + " Mo)"
      );
    }
  }

  private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      throw new IllegalArgumentException("Nom de fichier invalide");
    }
    return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
  }
}
