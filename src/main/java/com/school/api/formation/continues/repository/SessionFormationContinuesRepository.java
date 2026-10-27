package com.school.api.formation.continues.repository;

import com.school.api.formation.continues.entity.SessionFormationContinues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SessionFormationContinuesRepository extends JpaRepository<SessionFormationContinues, Long> {

    @Query("select s from SessionFormationContinues s join fetch s.formation order by s.dateDebut desc, s.id desc")
    List<SessionFormationContinues> findAllAvecFormation();

    /** Sessions publiées, pas encore commencées, de formations actives (site public) */
    @Query("""
            select s from SessionFormationContinues s join fetch s.formation f
            where s.active = true and f.enabled = true and s.dateDebut >= :aujourdhui
            order by s.dateDebut asc, s.id asc
            """)
    List<SessionFormationContinues> findOuvertes(@Param("aujourdhui") LocalDate aujourdhui);

    @Query("""
            select s from SessionFormationContinues s join fetch s.formation f
            where f.slug = :slug and s.active = true and f.enabled = true and s.dateDebut >= :aujourdhui
            order by s.dateDebut asc, s.id asc
            """)
    List<SessionFormationContinues> findOuvertesParSlug(@Param("slug") String slug,
                                                        @Param("aujourdhui") LocalDate aujourdhui);

    boolean existsByFormation_Id(Long formationId);
}