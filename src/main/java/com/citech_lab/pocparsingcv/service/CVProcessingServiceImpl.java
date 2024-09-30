package com.citech_lab.pocparsingcv.service;

import com.citech_lab.pocparsingcv.extractor.CVTextExtractor;
import com.citech_lab.pocparsingcv.repository.ModelRepositoryLoader;

import com.citech_lab.pocparsingcv.structure.SkillStructureBuilder;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Service
public class CVProcessingServiceImpl implements CVProcessingService {

    private static final String FORMATION = "Formation";
    private static final String DONNES_PERSONNELLES = "Donnees personnelles";
    private static final String EXPERIENCE_PROFFESSIONNELLES = "Experiences professionnelles";
    private static final String PDF = "pdf";
    private static final String DOCX = "docx";
    private static final String DOC = "doc";
    private static final String PPT = "ppt";
    private static final String PPTX = "pptx";
    private static final String ODT = "odt";


    private final SkillStructureBuilder skillStructureBuilder;
    private final ModelRepositoryLoader skillRepositoryLoader;

    public CVProcessingServiceImpl(SkillStructureBuilder skillStructureBuilder, ModelRepositoryLoader skillRepositoryLoader) {
        this.skillStructureBuilder = skillStructureBuilder;
        this.skillRepositoryLoader = skillRepositoryLoader;
    }

    @Override
    public Map<String, Map<String, List<String>>> processCV(MultipartFile cv) throws Exception {

        // Obtenir le texte extrait du fichier
        String fileContent = extractTextFromCVType(cv);

        //extractProfessionalExperience(fileContent);
        Map<String, List<String>> contactInfo = CVContactInfoDetectorService.detectContactInfo(fileContent);
        Map<String, List<String>> professionalExperience = CVProfessionalExperienceService.detectProfessionalExperience(fileContent);


        // Charger le référentiel des compétences
        Map<String, Map<String, List<String>>> skillsMap = skillRepositoryLoader.loadModel();

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

        // Chargement du modèle NER depuis le dossier resources
        InputStream modelIn = CVProcessingServiceImpl.class.getResourceAsStream("/en-ner-organization.bin");

        if (modelIn == null) {
            throw new FileNotFoundException("Le modèle NER est introuvable.");
        }

        // Création du modèle NER à partir du fichier
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
        NameFinderME nameFinder = new NameFinderME(model);

        // Analyser le texte sans tirets
        Map<String, List<String>> results = CVFormationDetectorService.analyzeTextFormation(nameFinder, fileContent);

        // Analyser le texte avec tirets
        // Afficher les résultats
        System.out.println("Résultats de la catégorie 'formation': " + results);
        // Fermeture du flux de modèle
        modelIn.close();
        foundSkills.get(FORMATION).putAll(results);
        foundSkills.get(DONNES_PERSONNELLES).putAll(contactInfo);
        foundSkills.get(EXPERIENCE_PROFFESSIONNELLES).putAll(professionalExperience);
        // Retourner la structure des compétences trouvées regroupées par catégories
        return foundSkills;
    }

    @Override
    public String extractTextFromCVType(MultipartFile cv) throws Exception {


        String fileType = getCVExtension(Objects.requireNonNull(cv.getOriginalFilename()));

        String text = null;

        // Déterminer le type de fichier et extraire le texte en conséquence
        if (PDF.equalsIgnoreCase(fileType)) {
            text = CVTextExtractor.extractTextFromPDF(cv.getInputStream());
        } else if (DOCX.equalsIgnoreCase(fileType) || DOC.equalsIgnoreCase(fileType)) {
            text = CVTextExtractor.extractTextFromDOCX(cv.getInputStream());
        } else if (PPT.equalsIgnoreCase(fileType) || PPTX.equalsIgnoreCase(fileType)) {
            text = CVTextExtractor.extractTextFromPPT(cv.getInputStream());
        } else if (ODT.equalsIgnoreCase(fileType)) {
            text = CVTextExtractor.extractTextFromODT(cv.getInputStream());
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


}
