package com.school.api.formation.continues.service;

import com.school.api.common.mail.MailService;
import com.school.api.formation.continues.dto.InscriptionSessionDTO;
import com.school.api.formation.continues.dto.InscriptionSessionRequest;
import com.school.api.formation.continues.entity.InscriptionSessionFormation;
import com.school.api.formation.continues.entity.SessionFormationContinues;
import com.school.api.formation.continues.repository.InscriptionSessionFormationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static com.school.api.formation.continues.entity.InscriptionSessionFormation.*;

/**
 * Demandes de participation aux sessions de formation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InscriptionSessionService {

    private static final ZoneId LIBREVILLE = ZoneId.of("Africa/Libreville");
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$");
    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);
    private static final List<String> OCCUPE_UNE_PLACE = List.of(EN_ATTENTE, CONFIRMEE);

    private final InscriptionSessionFormationRepository repository;
    private final SessionFormationService sessionService;
    private final MailService mailService;

    /* =========================
       🌍 INSCRIPTION (site public)
       ========================= */
    @Transactional
    public void inscrire(Long sessionId, InscriptionSessionRequest r) {

        SessionFormationContinues session = sessionService.trouver(sessionId);

        if (!Boolean.TRUE.equals(session.getActive()) || !session.getFormation().isEnabled()) {
            throw new RuntimeException("Cette session n'est pas ouverte aux inscriptions");
        }
        if (session.getDateDebut().isBefore(SessionFormationService.aujourdhui())) {
            throw new RuntimeException("Les inscriptions à cette session sont closes");
        }

        /* ----- Contrôle des champs ----- */
        String nom = obligatoire(r.nom(), "le nom", 100);
        String prenom = obligatoire(r.prenom(), "le prénom", 100);
        String sexe = obligatoire(r.sexe(), "le sexe", 1).toUpperCase(Locale.ROOT);
        if (!sexe.equals("M") && !sexe.equals("F")) throw new RuntimeException("Sexe invalide");
        if (r.dateNaissance() == null) throw new RuntimeException("Indiquez la date de naissance");
        if (!r.dateNaissance().isBefore(LocalDate.now(LIBREVILLE).minusYears(10))) {
            throw new RuntimeException("Date de naissance invalide");
        }
        String lieuNaissance = obligatoire(r.lieuNaissance(), "le lieu de naissance", 120);
        String email = obligatoire(r.email(), "l'adresse email", 150).toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(email).matches()) throw new RuntimeException("Adresse email invalide");
        String telephone = obligatoire(r.telephone(), "le téléphone", 30);
        String lieuResidence = obligatoire(r.lieuResidence(), "le lieu de résidence", 150);
        if (r.enActivite() == null) throw new RuntimeException("Indiquez si vous êtes en activité");
        String profession = r.enActivite() ? obligatoire(r.profession(), "votre profession", 150) : null;

        /* ----- Doublon et places ----- */
        if (repository.existsBySession_IdAndEmailIgnoreCaseAndStatutIn(sessionId, email, OCCUPE_UNE_PLACE)) {
            throw new RuntimeException("Vous êtes déjà inscrit(e) à cette session avec cette adresse email");
        }
        Integer restantes = sessionService.placesRestantes(session);
        if (restantes != null && restantes <= 0) {
            throw new RuntimeException("Désolé, cette session est complète");
        }

        InscriptionSessionFormation i = InscriptionSessionFormation.builder()
                .session(session)
                .nom(nom.toUpperCase(Locale.ROOT))
                .prenom(prenom)
                .sexe(sexe)
                .dateNaissance(r.dateNaissance())
                .lieuNaissance(lieuNaissance)
                .email(email)
                .telephone(telephone)
                .lieuResidence(lieuResidence)
                .enActivite(r.enActivite())
                .profession(profession)
                .statut(EN_ATTENTE)
                .dateInscription(LocalDateTime.now(LIBREVILLE))
                .build();

        repository.save(i);

        envoyerMail(i, "recue");
    }

    /* =========================
       📄 BACK OFFICE
       ========================= */
    @Transactional(readOnly = true)
    public Page<InscriptionSessionDTO> lister(int page, int size, String statut, Long sessionId) {
        Pageable p = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "dateInscription"));
        String st = (statut == null || statut.isBlank()) ? null : statut.trim().toUpperCase(Locale.ROOT);

        Page<InscriptionSessionFormation> res;
        if (st != null && sessionId != null) res = repository.findByStatutAndSession_Id(st, sessionId, p);
        else if (st != null) res = repository.findByStatut(st, p);
        else if (sessionId != null) res = repository.findBySession_Id(sessionId, p);
        else res = repository.findAllBy(p);

        return res.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> stats() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("total", repository.count());
        m.put("enAttente", repository.countByStatut(EN_ATTENTE));
        m.put("confirmees", repository.countByStatut(CONFIRMEE));
        m.put("refusees", repository.countByStatut(REFUSEE));
        return m;
    }

    @Transactional(readOnly = true)
    public List<InscriptionSessionDTO> parSession(Long sessionId) {
        return repository.findBySession_IdOrderByNomAscPrenomAsc(sessionId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public InscriptionSessionDTO confirmer(Long id, String admin) {
        InscriptionSessionFormation i = trouver(id);
        if (CONFIRMEE.equals(i.getStatut())) throw new RuntimeException("Cette participation est déjà confirmée");

        /* Une demande refusée qu'on confirme reprend une place : il faut qu'il en reste */
        if (REFUSEE.equals(i.getStatut())) {
            Integer restantes = sessionService.placesRestantes(i.getSession());
            if (restantes != null && restantes <= 0) throw new RuntimeException("La session est complète");
        }

        i.setStatut(CONFIRMEE);
        i.setMotifRefus(null);
        i.setDateTraitement(LocalDateTime.now(LIBREVILLE));
        i.setTraitePar(admin);
        repository.save(i);

        envoyerMail(i, "confirmee");
        return toDTO(i);
    }

    @Transactional
    public InscriptionSessionDTO refuser(Long id, String motif, String admin) {
        InscriptionSessionFormation i = trouver(id);
        if (REFUSEE.equals(i.getStatut())) throw new RuntimeException("Cette participation est déjà refusée");

        String m = (motif == null || motif.isBlank()) ? null : motif.trim();
        if (m != null && m.length() > 1000) throw new RuntimeException("Motif trop long (1000 caractères maximum)");

        i.setStatut(REFUSEE);
        i.setMotifRefus(m);
        i.setDateTraitement(LocalDateTime.now(LIBREVILLE));
        i.setTraitePar(admin);
        repository.save(i);

        envoyerMail(i, "refusee");
        return toDTO(i);
    }

    @Transactional
    public void supprimer(Long id) {
        repository.delete(trouver(id));
    }

    /* =========================
       HELPERS
       ========================= */

    private InscriptionSessionFormation trouver(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Demande introuvable"));
    }

    private static String obligatoire(String v, String quoi, int max) {
        if (v == null || v.isBlank()) throw new RuntimeException("Indiquez " + quoi);
        String t = v.trim();
        if (t.length() > max) throw new RuntimeException("Trop long : " + quoi + " (" + max + " caractères maximum)");
        return t;
    }

    /** Mail au participant (ne bloque jamais l'action si le serveur mail est en panne) */
    private void envoyerMail(InscriptionSessionFormation i, String type) {
        try {
            SessionFormationContinues s = i.getSession();
            mailService.sendInscriptionSession(
                    type,
                    i.getEmail(),
                    i.getPrenom() + " " + i.getNom(),
                    s.getFormation().getLibelle(),
                    s.getDateDebut().format(DATE_FR),
                    s.getDateFin().format(DATE_FR),
                    s.getLieu(),
                    s.getHoraires(),
                    i.getMotifRefus()
            );
        } catch (Exception e) {
            log.error("❌ Mail d'inscription ({}) non envoyé à {}", type, i.getEmail(), e);
        }
    }

    private InscriptionSessionDTO toDTO(InscriptionSessionFormation i) {
        SessionFormationContinues s = i.getSession();
        return new InscriptionSessionDTO(
                i.getId(), s.getId(), s.getFormation().getLibelle(), s.getDateDebut(), s.getDateFin(), s.getLieu(),
                i.getNom(), i.getPrenom(), i.getSexe(), i.getDateNaissance(), i.getLieuNaissance(),
                i.getEmail(), i.getTelephone(), i.getLieuResidence(),
                Boolean.TRUE.equals(i.getEnActivite()), i.getProfession(),
                i.getStatut(), i.getMotifRefus(), i.getDateInscription(), i.getDateTraitement(), i.getTraitePar());
    }
}