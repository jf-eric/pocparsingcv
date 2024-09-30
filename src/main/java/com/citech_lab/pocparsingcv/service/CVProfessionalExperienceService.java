package com.citech_lab.pocparsingcv.service;

import java.util.*;
import java.util.regex.*;

public class CVProfessionalExperienceService {


    private static final String DATES = "Dates";
    private static final String DESCRIPTIONS = "Descriptions";
    private static final String TOUS_LES_TEXTES_PERTINENTS = "Tous les textes pertinents";

    public static Map<String, List<String>> detectProfessionalExperience(String cvText) {
        Map<String, List<String>> experienceInfo = new HashMap<>();

        // Expression régulière pour détecter la section d'expérience professionnelle
        Pattern titlePattern = Pattern.compile("(?i)(professional experience|work experience|expérience professionnelle|historique professionnel|postes précédents)", Pattern.DOTALL);
        Matcher titleMatcher = titlePattern.matcher(cvText);

        if (titleMatcher.find()) {
            int start = titleMatcher.end();
            String remainingText = cvText.substring(start).trim();

            // Extraction des dates d'emploi
            Pattern datePattern = Pattern.compile(
                    "\\b(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?|" +
                            "Jan(?:v)?|Févr|Mars|Avr|Mai|Juin|Juil|Août|Sept|Oct|Nov|Déc)\\s\\d{4}(?:\\s*-\\s*(?:Present|Current|Today|\\w+\\s\\d{4}|Présent|Actuel|Aujourd'hui))?\\b",
                    Pattern.CASE_INSENSITIVE);
            Matcher dateMatcher = datePattern.matcher(remainingText);

            List<String> employmentDates = new ArrayList<>();
            while (dateMatcher.find()) {
                employmentDates.add(dateMatcher.group().trim());
            }
            experienceInfo.put(DATES, employmentDates);

            // Extraction des descriptions de poste
            Pattern descriptionPattern = Pattern.compile("(?<=Achievement :|Role :|Mission :|Réalisation :|Rôle :)[\\s\\S]*?(?=Environment :|Environnement :|$)", Pattern.DOTALL);
            Matcher descriptionMatcher = descriptionPattern.matcher(remainingText);

            List<String> jobDescriptions = new ArrayList<>();
            while (descriptionMatcher.find()) {
                jobDescriptions.add(descriptionMatcher.group().trim());
            }
            experienceInfo.put(DESCRIPTIONS, jobDescriptions);

        } else {
            // Si aucune section d'expérience professionnelle n'est trouvée, récupérer tout le texte pertinent
            List<String> allRelevantText = extractAllRelevantText(cvText);
            experienceInfo.put(TOUS_LES_TEXTES_PERTINENTS, allRelevantText);
        }

        return experienceInfo;
    }

    private static List<String> extractAllRelevantText(String cvText) {
        List<String> relevantText = new ArrayList<>();

        // Expression régulière pour détecter et exclure les coordonnées personnelles
        Pattern personalInfoPattern = Pattern.compile("(?i)(nom|adresse|téléphone|email|date de naissance|date of birth|birthdate|contact|linkedin)", Pattern.DOTALL);
        Matcher personalInfoMatcher = personalInfoPattern.matcher(cvText);
        StringBuilder filteredText = new StringBuilder(cvText);

        // Supprimer les coordonnées personnelles du texte
        while (personalInfoMatcher.find()) {
            int start = personalInfoMatcher.start();
            int end = personalInfoMatcher.end();
            filteredText.delete(start, end);
        }

        // Expression régulière pour détecter et exclure les compétences techniques
        Pattern skillsPattern = Pattern.compile("(?i)(compétences|skills)", Pattern.DOTALL);
        Matcher skillsMatcher = skillsPattern.matcher(filteredText);

        if (skillsMatcher.find()) {
            int start = skillsMatcher.start();
            String remainingText = filteredText.substring(start).trim();

            // Supprimer les compétences techniques du texte
            filteredText.delete(start, filteredText.length());
        }

        // Nettoyer les retours à la ligne et les espaces indésirables
        String cleanedText = filteredText.toString()
                .replaceAll("\\r?\\n", " ")  // Remplacer les retours à la ligne par un espace
                .replaceAll("\\s+", " ")      // Remplacer les espaces multiples par un seul espace
                .trim();                      // Supprimer les espaces en début et fin

        // Ajouter le texte restant à la liste des textes pertinents
        if (!cleanedText.isEmpty()) {
            relevantText.add(cleanedText);
        }

        return relevantText;


    }
}
