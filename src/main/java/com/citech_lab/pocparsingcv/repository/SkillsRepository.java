package com.citech_lab.pocparsingcv.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkillsRepository {
    private final Map<String, Map<String, List<String>>> skills = new LinkedHashMap<>();

    public Map<String, Map<String, List<String>>> getCompetences() {
        return skills;
    }
}
