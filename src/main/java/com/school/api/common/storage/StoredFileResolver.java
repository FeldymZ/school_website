package com.school.api.common.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Ouvre un fichier à partir de ce qui est stocké en base :
 *  - "classpath:reports/images/x.png"
 *  - "https://..."
 *  - chemin disque (absolu, ou relatif au dossier storage.upload-dir)
 *  - en dernier recours : app.public-url + chemin
 */
@Slf4j
@Component
public class StoredFileResolver {

    @Value("${storage.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.public-url:}")
    private String publicUrl;

    public InputStream open(String chemin) {
        if (chemin == null || chemin.isBlank()) return null;

        try {
            if (chemin.startsWith("classpath:")) {
                return new ClassPathResource(chemin.substring("classpath:".length())).getInputStream();
            }
            if (chemin.startsWith("http://") || chemin.startsWith("https://")) {
                return new URL(chemin).openStream();
            }
            for (Path p : candidats(chemin)) {
                if (Files.isRegularFile(p)) return Files.newInputStream(p);
            }
            if (publicUrl != null && !publicUrl.isBlank()) {
                return new URL(publicUrl + (chemin.startsWith("/") ? "" : "/") + chemin).openStream();
            }
        } catch (Exception e) {
            log.warn("Fichier introuvable pour le PDF : {}", chemin);
        }
        return null;
    }

    private List<Path> candidats(String chemin) {
        String rel = chemin.replace('\\', '/');
        while (rel.startsWith("/")) rel = rel.substring(1);

        String dossier = uploadDir.replace('\\', '/');
        while (dossier.endsWith("/")) dossier = dossier.substring(0, dossier.length() - 1);
        String sansPrefixe = rel.startsWith(dossier + "/") ? rel.substring(dossier.length() + 1) : rel;

        Path base = Path.of(uploadDir);
        List<Path> liste = new ArrayList<>();
        liste.add(Path.of(chemin));            // chemin absolu
        liste.add(Path.of(rel));               // ex : uploads/signatures/x.png
        liste.add(base.resolve(rel));          // uploads/ + signatures/x.png
        liste.add(base.resolve(sansPrefixe));  // évite uploads/uploads/...
        return liste;
    }
}