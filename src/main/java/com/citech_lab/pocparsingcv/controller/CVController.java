package com.citech_lab.pocparsingcv.controller;

import com.citech_lab.pocparsingcv.service.CVProcessingServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/cv")
public class CVController {

    private final CVProcessingServiceImpl cVExtractionDataService;
    private final ObjectMapper objectMapper;

    public CVController(CVProcessingServiceImpl cVExtractionDataService, ObjectMapper objectMapper) {
        this.cVExtractionDataService = cVExtractionDataService;
        this.objectMapper = objectMapper;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadCV(@RequestParam("cv") MultipartFile cv) throws Exception {

        if (!cv.isEmpty()) {
            Map<String, Map<String, List<String>>> skills = cVExtractionDataService.processCV(cv);
            return new ResponseEntity<>(objectMapper.writeValueAsString(skills), HttpStatus.OK);

        }
        return new ResponseEntity<>("CV vide", HttpStatus.BAD_REQUEST);
    }
}
