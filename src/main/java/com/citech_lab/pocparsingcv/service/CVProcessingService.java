package com.citech_lab.pocparsingcv.service;


import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface CVProcessingService {
    Map<String, Map<String, List<String>>> processCV(MultipartFile cv) throws Exception;

    String extractTextFromCVType(MultipartFile cv) throws Exception;
}
