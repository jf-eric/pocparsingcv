package com.citech_lab.pocparsingcv.repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SkillRepositoryLoader {
    Map<String, Map<String, List<String>>> loadSkills() throws IOException;
}
