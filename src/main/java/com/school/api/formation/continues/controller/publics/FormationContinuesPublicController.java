package com.school.api.formation.continues.controller.publics;

import com.school.api.formation.continues.entity.FormationContinues;
import com.school.api.formation.continues.service.FormationContinuesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/formations")
@RequiredArgsConstructor
public class FormationContinuesPublicController {

    private final FormationContinuesService service;

    @GetMapping
    public Page<FormationContinues> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAllPublic(page, size);
    }

    @GetMapping("/{id}")
    public FormationContinues getById(@PathVariable Long id) {
        return service.getById(id);
    }
}