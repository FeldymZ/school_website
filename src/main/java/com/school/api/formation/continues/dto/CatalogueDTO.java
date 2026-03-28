package com.school.api.formation.continues.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CatalogueDTO {

    private List<CategorieDTO> categories;
}