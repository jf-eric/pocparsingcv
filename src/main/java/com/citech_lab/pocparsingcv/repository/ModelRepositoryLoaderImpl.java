package com.citech_lab.pocparsingcv.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ModelRepositoryLoaderImpl implements ModelRepositoryLoader {
    private static final String MODEL_REFERENCE_JSON_FILE = "ModelReference.json";

    @Override
    public Map<String, Map<String, List<String>>> loadModel() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = new ClassPathResource(MODEL_REFERENCE_JSON_FILE);
        ModelRepository repository = mapper.readValue(resource.getFile(), ModelRepository.class);
        return repository.getCompetences();
    }
}

