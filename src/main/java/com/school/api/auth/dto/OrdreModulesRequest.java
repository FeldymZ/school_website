package com.school.api.auth.dto;

import java.util.List;

/** Ids des modules d'une section, dans le nouvel ordre voulu */
public record OrdreModulesRequest(List<Long> ids) {
}