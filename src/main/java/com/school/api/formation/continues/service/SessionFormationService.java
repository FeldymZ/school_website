package com.school.api.formation.continues.service;

import com.school.api.formation.continues.dto.*;
import com.school.api.formation.continues.entity.FormationContinues;
import com.school.api.formation.continues.entity.InscriptionSessionFormation;
import com.school.api.formation.continues.entity.SessionFormationContinues;
import com.school.api.formation.continues.mapper.FormationMapper;
import com.school.api.formation.continues.repository.FormationContinuesRepository;
import com.school.api.formation.continues.repository.InscriptionSessionFormationRepository;
import com.school.api.formation.continues.repository.SessionFormationContinuesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Agenda de formation : sessions (périodes) des formations continues.
 */
@Service
@RequiredArgsConstructor
public class SessionFormationService {

    private static final ZoneId LIBREVILLE = ZoneId.of("Africa/Libreville");
    private static final List<String> STATUTS_OCCUPANT_UNE_PLACE =
            List.of(InscriptionSessionFormation.EN_ATTENTE, InscriptionSessionFormation.CONFIRMEE);

    private final SessionFormationContinuesRepository sessionRepository;
    private final InscriptionSessionFormationRepository inscriptionRepository;
    private final FormationContinuesRepository formationRepository;
    private final FormationMapper formationMapper;

    /* =========================
       📄 BACK OFFICE
       ========================= */

    @Transactional(readOnly = true)
    public List<SessionFormationDTO> toutes() {
        return sessionRepository.findAllAvecFormation().stream().map(this::toDTO).toList();
    }

    /** Formations actives proposées dans le formulaire de session */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> formationsDisponibles() {
        return formationRepository.findAll().stream()
                .filter(FormationContinues::isEnabled)
                .sorted(Comparator.comparing(FormationContinues::getLibelle, String.CASE_INSENSITIVE_ORDER))
                .map(f -> Map.<String, Object>of("id", f.getId(), "libelle", f.getLibelle()))
                .toList();
    }

    @Transactional
    public SessionFormationDTO creer(SessionFormationRequest r) {
        SessionFormationContinues s = new SessionFormationContinues();
        s.setDateCreation(LocalDateTime.now(LIBREVILLE));
        appliquer(s, r);
        return toDTO(sessionRepository.save(s));
    }

    @Transactional
    public SessionFormationDTO modifier(Long id, SessionFormationRequest r) {
        SessionFormationContinues s = trouver(id);
        appliquer(s, r);

        /* On ne peut pas descendre sous le nombre de personnes déjà inscrites */
        if (s.getNombrePlaces() != null) {
            long occupees = inscriptionRepository.countBySession_IdAndStatutIn(s.getId(), STATUTS_OCCUPANT_UNE_PLACE);
            if (s.getNombrePlaces() < occupees) {
                throw new RuntimeException("Il y a déjà " + occupees
                        + " inscription(s) : le nombre de places ne peut pas être inférieur");
            }
        }
        return toDTO(sessionRepository.save(s));
    }

    @Transactional
    public SessionFormationDTO changerActive(Long id, boolean active) {
        SessionFormationContinues s = trouver(id);
        s.setActive(active);
        return toDTO(sessionRepository.save(s));
    }

    @Transactional
    public void supprimer(Long id) {
        SessionFormationContinues s = trouver(id);
        if (inscriptionRepository.countBySession_Id(id) > 0) {
            throw new RuntimeException(
                    "Cette session a des demandes de participation : masquez-la plutôt que de la supprimer");
        }
        sessionRepository.delete(s);
    }

    /* =========================
       🌍 SITE PUBLIC
       ========================= */

    /** Formations qui ont au moins une session ouverte, avec ces sessions */
    @Transactional(readOnly = true)
    public List<FormationSessionsPublicDTO> formationsAvecSessions() {
        Map<Long, List<SessionFormationContinues>> parFormation = new LinkedHashMap<>();
        Map<Long, FormationContinues> formations = new HashMap<>();

        for (SessionFormationContinues s : sessionRepository.findOuvertes(aujourdhui())) {
            parFormation.computeIfAbsent(s.getFormation().getId(), k -> new ArrayList<>()).add(s);
            formations.put(s.getFormation().getId(), s.getFormation());
        }

        return parFormation.entrySet().stream()
                .map(e -> new FormationSessionsPublicDTO(
                        formationMapper.toDTO(formations.get(e.getKey())),
                        e.getValue().stream().map(this::toPublicDTO).toList()))
                .toList();
    }

    /** Une formation (par son slug) et ses sessions ouvertes */
    @Transactional(readOnly = true)
    public FormationSessionsPublicDTO formationParSlug(String slug) {
        List<SessionFormationContinues> sessions = sessionRepository.findOuvertesParSlug(slug, aujourdhui());
        if (sessions.isEmpty()) {
            throw new RuntimeException("Aucune session ouverte pour cette formation");
        }
        return new FormationSessionsPublicDTO(
                formationMapper.toDTO(sessions.get(0).getFormation()),
                sessions.stream().map(this::toPublicDTO).toList());
    }

    /* =========================
       HELPERS
       ========================= */

    SessionFormationContinues trouver(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session introuvable"));
    }

    static LocalDate aujourdhui() {
        return LocalDate.now(LIBREVILLE);
    }

    /** null = illimité */
    Integer placesRestantes(SessionFormationContinues s) {
        if (s.getNombrePlaces() == null) return null;
        long occupees = inscriptionRepository.countBySession_IdAndStatutIn(s.getId(), STATUTS_OCCUPANT_UNE_PLACE);
        return (int) Math.max(0, s.getNombrePlaces() - occupees);
    }

    private void appliquer(SessionFormationContinues s, SessionFormationRequest r) {
        if (r.formationId() == null) throw new RuntimeException("Choisissez la formation");
        if (r.dateDebut() == null || r.dateFin() == null) throw new RuntimeException("Les dates de début et de fin sont obligatoires");
        if (r.dateFin().isBefore(r.dateDebut())) throw new RuntimeException("La date de fin doit être après la date de début");
        if (r.nombrePlaces() != null && r.nombrePlaces() < 1) throw new RuntimeException("Le nombre de places doit être d'au moins 1 (laissez vide pour illimité)");

        FormationContinues f = formationRepository.findById(r.formationId())
                .orElseThrow(() -> new RuntimeException("Formation introuvable"));

        s.setFormation(f);
        s.setDateDebut(r.dateDebut());
        s.setDateFin(r.dateFin());
        s.setLieu(texte(r.lieu(), 150));
        s.setHoraires(texte(r.horaires(), 150));
        s.setNombrePlaces(r.nombrePlaces());
        s.setActive(r.active() == null || r.active());
    }

    private static String texte(String v, int max) {
        if (v == null || v.isBlank()) return null;
        String t = v.trim();
        if (t.length() > max) throw new RuntimeException("Texte trop long (" + max + " caractères maximum)");
        return t;
    }

    private SessionFormationDTO toDTO(SessionFormationContinues s) {
        long inscrits = inscriptionRepository.countBySession_IdAndStatutIn(s.getId(), STATUTS_OCCUPANT_UNE_PLACE);
        long confirmees = inscriptionRepository.countBySession_IdAndStatutIn(s.getId(), List.of(InscriptionSessionFormation.CONFIRMEE));
        LocalDate auj = aujourdhui();
        String etat = auj.isBefore(s.getDateDebut()) ? "A_VENIR" : auj.isAfter(s.getDateFin()) ? "TERMINEE" : "EN_COURS";

        return new SessionFormationDTO(
                s.getId(),
                s.getFormation().getId(),
                s.getFormation().getLibelle(),
                s.getFormation().getSlug(),
                s.getDateDebut(),
                s.getDateFin(),
                s.getLieu(),
                s.getHoraires(),
                s.getNombrePlaces(),
                inscrits,
                confirmees,
                s.getNombrePlaces() == null ? null : (int) Math.max(0, s.getNombrePlaces() - inscrits),
                Boolean.TRUE.equals(s.getActive()),
                etat
        );
    }

    private SessionPublicDTO toPublicDTO(SessionFormationContinues s) {
        Integer restantes = placesRestantes(s);
        return new SessionPublicDTO(
                s.getId(), s.getDateDebut(), s.getDateFin(), s.getLieu(), s.getHoraires(),
                s.getNombrePlaces(), restantes, restantes != null && restantes == 0);
    }
}