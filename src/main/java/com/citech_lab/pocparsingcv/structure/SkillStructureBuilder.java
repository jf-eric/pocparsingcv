package com.citech_lab.pocparsingcv.structure;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SkillStructureBuilder {
    // Méthode pour créer une structure vide basée sur le référentiel des compétences
    public Map<String, Map<String, List<String>>> createEmptySkillStructure(Map<String, Map<String, List<String>>> skillsMap) {
        Map<String, Map<String, List<String>>> emptyStructure = new LinkedHashMap<>();

        for (Map.Entry<String, Map<String, List<String>>> mainCategory : skillsMap.entrySet()) {
            String category = mainCategory.getKey();
            Map<String, List<String>> subCategories = mainCategory.getValue();

            Map<String, List<String>> emptySubCategories = new HashMap<>();
            for (String subCategory : subCategories.keySet()) {
                emptySubCategories.put(subCategory, new ArrayList<>());
            }

            emptyStructure.put(category, emptySubCategories);
        }
        return emptyStructure;
    }
}
