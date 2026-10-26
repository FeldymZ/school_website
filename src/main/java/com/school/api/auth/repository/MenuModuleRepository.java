package com.school.api.auth.repository;

import com.school.api.auth.entity.MenuModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuModuleRepository extends JpaRepository<MenuModule, Long> {

    List<MenuModule> findAllByOrderByOrdreAscIdAsc();

    List<MenuModule> findByActifTrueOrderByOrdreAscIdAsc();

    boolean existsByChemin(String chemin);

    boolean existsByCheminAndIdNot(String chemin, Long id);

    long countByCle(String cle);

    @Query("select coalesce(max(m.ordre), 0) from MenuModule m")
    Integer maxOrdre();

    @Query("select distinct m.cle from MenuModule m where m.cle is not null")
    List<String> toutesLesCles();
}