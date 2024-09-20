package com.citech_lab.pocparsingcv.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class SkillRepositoryLoaderImpl implements SkillRepositoryLoader {
    private static final String SKILLS_JSON_FILE = "SkillReference.json";

    @Override
    public Map<String, Map<String, List<String>>> loadSkills() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Resource resource = new ClassPathResource(SKILLS_JSON_FILE);
        SkillsRepository repository = mapper.readValue(resource.getFile(), SkillsRepository.class);
        return repository.getCompetences();
    }
}

