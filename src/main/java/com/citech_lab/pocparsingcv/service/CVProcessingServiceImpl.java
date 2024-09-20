package com.citech_lab.pocparsingcv.service;

import com.citech_lab.pocparsingcv.extractor.CVTextExtractor;
import com.citech_lab.pocparsingcv.repository.SkillRepositoryLoader;

import com.citech_lab.pocparsingcv.structure.SkillStructureBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CVProcessingServiceImpl implements CVProcessingService {
    private final CVTextExtractor cvTextExtractor;
    private final SkillStructureBuilder skillStructureBuilder;
    private final SkillRepositoryLoader skillRepositoryLoader;

    public CVProcessingServiceImpl(CVTextExtractor cvTextExtractor, SkillStructureBuilder skillStructureBuilder, SkillRepositoryLoader skillRepositoryLoader) {
        this.cvTextExtractor = cvTextExtractor;
        this.skillStructureBuilder = skillStructureBuilder;
        this.skillRepositoryLoader = skillRepositoryLoader;
    }

    @Override
    public Map<String, Map<String, List<String>>> processCV(MultipartFile cv) throws Exception {
        // Obtenir le texte extrait du fichier
        String fileContent = extractTextFromCVType(cv);

        extractProfessionalExperience(fileContent);
        extractContactInfo(fileContent);


        // Charger le référentiel des compétences
        Map<String, Map<String, List<String>>> skillsMap = skillRepositoryLoader.loadSkills();

        // Créer une structure vide avec la même forme que le référentiel des compétences
        Map<String, Map<String, List<String>>> foundSkills = skillStructureBuilder.createEmptySkillStructure(skillsMap);

        // Traiter le contenu du CV pour extraire les compétences
        for (Map.Entry<String, Map<String, List<String>>> mainCategory : skillsMap.entrySet()) {
            String category = mainCategory.getKey();
            Map<String, List<String>> subCategories = mainCategory.getValue();

            for (Map.Entry<String, List<String>> subCategoryEntry : subCategories.entrySet()) {
                String subCategory = subCategoryEntry.getKey();
                List<String> skills = subCategoryEntry.getValue();

                // Vérifier si la ligne du CV contient une compétence du référentiel
                for (String skill : skills) {
                    if (fileContent.toLowerCase().contains(skill.toLowerCase())) {
                        // Ajouter la compétence trouvée dans la bonne sous-catégorie
                        foundSkills.get(category).get(subCategory).add(skill);
                    }
                }
            }
        }

        // Retourner la structure des compétences trouvées regroupées par catégories
        return foundSkills;
    }

    @Override
    public String extractTextFromCVType(MultipartFile cv) throws Exception {


        String fileType = getCVExtension(Objects.requireNonNull(cv.getOriginalFilename()));

        String text = null;

        // Déterminer le type de fichier et extraire le texte en conséquence
        if ("pdf".equalsIgnoreCase(fileType)) {
            text = cvTextExtractor.extractTextFromPDF(cv.getInputStream());
        } else if ("docx".equalsIgnoreCase(fileType) || "doc".equalsIgnoreCase(fileType)) {
            text = cvTextExtractor.extractTextFromDOCX(cv.getInputStream());
        } else if ("ppt".equalsIgnoreCase(fileType) || "pptx".equalsIgnoreCase(fileType)) {
            text = cvTextExtractor.extractTextFromPPT(cv.getInputStream());
        } else if ("odt".equalsIgnoreCase(fileType)) {
            text = cvTextExtractor.extractTextFromODT(cv.getInputStream());
        } else {
            throw new UnsupportedOperationException("Unsupported file type: " + fileType);
        }
        System.out.println("textAffiche: " + text);

        return text;
    }


    private String getCVExtension(String filename) {

        int lastDotIndex = filename.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : filename.substring(lastDotIndex + 1).toLowerCase();
    }

    private void extractProfessionalExperience(String cvText) {

        Pattern titlePattern = Pattern.compile("(?i)(professional experience|work experience|experience|expériences professionnelles|parcours professionnel|expérience)", Pattern.DOTALL);
        Matcher titleMatcher = titlePattern.matcher(cvText);

        if (titleMatcher.find()) {
            int start = titleMatcher.end();
            String remainingText = cvText.substring(start).trim();

            // Expression régulière pour détecter les dates d'emploi (anglais et français)
            Pattern datePattern = Pattern.compile(
                    "\\b(?:January|February|March|April|May|June|July|August|September|October|November|December|" +
                            "Janvier|Février|Mars|Avril|Mai|Juin|Juillet|Août|Septembre|Octobre|Novembre|Décembre)\\s\\d{4} " +
                            "(?:- (?:Present|Actuel|Aujourd'hui|\\w+ \\d{4}))?\\b", Pattern.CASE_INSENSITIVE);
            Matcher dateMatcher = datePattern.matcher(remainingText);

            System.out.println("Employment Dates Found:");
            // Extraction des dates d'emploi
            while (dateMatcher.find()) {
                System.out.println(" - " + dateMatcher.group().trim());
            }

            // Expression régulière pour détecter les descriptions de poste (en anglais et français)
            Pattern descriptionPattern = Pattern.compile("(?<=Réalisation :|Rôle :|Mission :)[\\s\\S]*?(?=Environnement :|$)", Pattern.DOTALL);
            Matcher descriptionMatcher = descriptionPattern.matcher(remainingText);

            System.out.println("\nExperience Descriptions Found:\n");
            // Extraction des descriptions de poste
            while (descriptionMatcher.find()) {
                System.out.println(" - " + descriptionMatcher.group().trim());
            }
        } else {
            System.out.println("No professional experience section found.");
        }
    }

    private void extractContactInfo(String cvText) {
        // Détection de l'email
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        Matcher emailMatcher = emailPattern.matcher(cvText);
        if (emailMatcher.find()) {
            System.out.println("Email Found: " + emailMatcher.group());
        } else {
            System.out.println("No email found.");
        }

        // Détection du numéro de téléphone
        Pattern phonePattern = Pattern.compile("(0[1-9][0-9]{8}|\\+?[0-9]{1,3}[ -]?\\(?[0-9]{1,4}\\)?[ -]?[0-9]{1,4}[ -]?[0-9]{1,9})");
        Matcher phoneMatcher = phonePattern.matcher(cvText);
        if (phoneMatcher.find()) {
            System.out.println("Phone Number Found: " + phoneMatcher.group());
        } else {
            System.out.println("No phone number found.");
        }

        // Détection du nom
        Pattern namePattern = Pattern.compile("^[A-Z][a-z]+\\s[A-Z][a-z]+", Pattern.MULTILINE);
        Matcher nameMatcher = namePattern.matcher(cvText);
        if (nameMatcher.find()) {
            System.out.println("Name Found: " + nameMatcher.group());
        } else {
            System.out.println("No name found.");
        }
    }


}
