package com.school.api.auth.controller;

import com.school.api.auth.entity.MenuPermission;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/menu-permissions")
public class MenuPermissionController {

    @GetMapping
    public Set<String> getAll() {
        return MenuPermission.allKeys();
    }
}