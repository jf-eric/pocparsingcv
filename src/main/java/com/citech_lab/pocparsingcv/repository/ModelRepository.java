package com.citech_lab.pocparsingcv.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModelRepository {
    private final Map<String, Map<String, List<String>>> model = new LinkedHashMap<>();

    public Map<String, Map<String, List<String>>> getCompetences() {
        return model;
    }
}
