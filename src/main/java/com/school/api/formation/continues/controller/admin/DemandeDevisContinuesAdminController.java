package com.school.api.formation.continues.controller.admin;

import com.school.api.auth.audit.AuditLog;
import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.StatutDemande;
import com.school.api.formation.continues.service.DemandeDevisContinuesAdminService;
import com.school.api.formation.continues.service.DevisContinuesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/demandes-devis")
@RequiredArgsConstructor
public class DemandeDevisContinuesAdminController {

    private final DemandeDevisContinuesAdminService service;
    private final DevisContinuesService devisService;

    /* ================= DEMANDES ================= */

    @AuditLog(action = "CONSULTATION_DEMANDES_DEVIS")
    @GetMapping
    public Page<DemandeDevisAdminDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) StatutDemande statut
    ) {
        return service.getAll(page, size, statut);
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return service.stats();
    }

    @AuditLog(action = "COMPTAGE_DEMANDES_NON_TRAITEES")
    @GetMapping("/count-non-traitees")
    public Long countNonTraitees() {
        return service.countNonTraitees();
    }

    @AuditLog(action = "CONSULTATION_DEMANDE_DEVIS", target = "#id.toString()")
    @GetMapping("/{id}")
    public DemandeDevisAdminDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @AuditLog(action = "REPONSE_DEMANDE_DEVIS", target = "#id.toString()", failureAction = "REPONSE_DEMANDE_DEVIS_ECHEC")
    @PostMapping("/{id}/repondre")
    public ResponseEntity<?> repondre(@PathVariable Long id, @ModelAttribute RepondreDemandeDTO dto) {
        service.repondre(id, dto);
        return ResponseEntity.ok(Map.of("success", true, "message", "Réponse envoyée"));
    }

    @AuditLog(action = "CONSULTATION_REPONSES_DEMANDE", target = "#id.toString()")
    @GetMapping("/{id}/reponses")
    public List<DemandeDevisReponseDTO> getReponses(@PathVariable Long id) {
        return service.getReponses(id);
    }

    @AuditLog(action = "CLOTURE_DEMANDE_DEVIS", target = "#id.toString()", failureAction = "CLOTURE_DEMANDE_ECHEC")
    @PostMapping("/{id}/cloturer")
    public ResponseEntity<?> cloturer(@PathVariable Long id) {
        service.cloturer(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Demande clôturée"));
    }

    @AuditLog(action = "SUPPRESSION_DEMANDE_DEVIS", target = "#id.toString()", failureAction = "SUPPRESSION_DEMANDE_ECHEC")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Supprimé"));
    }

    /* ================= DEVIS ================= */

    /** Valide (génère ou régénère) le devis : réduction + validateur */
    @AuditLog(action = "VALIDATION_DEVIS", target = "#id.toString()", failureAction = "VALIDATION_DEVIS_ECHEC")
    @PostMapping("/{id}/devis")
    public DevisDTO validerDevis(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) GenererDevisRequest request,
            Principal principal
    ) {
        return devisService.generer(
                id,
                request != null ? request : new GenererDevisRequest(),
                principal != null ? principal.getName() : "ADMIN"
        );
    }

    @AuditLog(action = "CONSULTATION_DEVIS", target = "#id.toString()")
    @GetMapping("/{id}/devis")
    public ResponseEntity<DevisDTO> getDevis(@PathVariable Long id) {
        return devisService.getByDemande(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @AuditLog(action = "TELECHARGEMENT_DEVIS", target = "#id.toString()")
    @GetMapping("/{id}/devis/pdf")
    public ResponseEntity<byte[]> telechargerDevis(@PathVariable Long id) {
        DevisContinuesService.DevisPdf pdf = devisService.pdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(pdf.nomFichier()).build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.contenu());
    }

    @AuditLog(action = "ENVOI_DEVIS", target = "#id.toString()", failureAction = "ENVOI_DEVIS_ECHEC")
    @PostMapping("/{id}/devis/envoyer")
    public ResponseEntity<?> envoyerDevis(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) EnvoyerDevisRequest request
    ) {
        service.envoyerDevis(id, request != null ? request.getMessage() : null);
        return ResponseEntity.ok(Map.of("success", true, "message", "Devis envoyé au client"));
    }
}