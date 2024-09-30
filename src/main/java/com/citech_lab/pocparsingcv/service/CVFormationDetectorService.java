package com.citech_lab.pocparsingcv.service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CVFormationDetectorService {
    public static final String INSTITUTION = "Institution";
    public static final String DATE = "Date";
    public static final String DIPLOME = "Diplôme";


    public static Map<String, List<String>> analyzeTextFormation(NameFinderME nameFinder, String text) {
        // Nettoyage du texte pour enlever les nouvelles lignes et les espaces superflus
        text = text.replaceAll("[\\r\\n]+", " ").trim();
        System.out.println("Analyzing: " + text);

        // Map pour stocker les résultats
        Map<String, List<String>> results = new HashMap<>();
        results.put(INSTITUTION, new ArrayList<>());
        results.put(DATE, new ArrayList<>());
        results.put(DIPLOME, new ArrayList<>());

        // Division du texte en tokens (mots)
        String[] tokens = text.split(" ");

        // Trouver les entités nommées dans les tokens
        Span[] spans = nameFinder.find(tokens);

        // Détection manuelle des institutions, des dates et des diplômes
        detectAdditionalEntities(text, results);
        detectDiplomaEntities(text, results);

        return results;
    }

    // Méthode pour détecter manuellement les institutions et les dates
    private static void detectAdditionalEntities(String text, Map<String, List<String>> results) {
        // Regex pour détecter les institutions (français et anglais)
        Pattern institutionPattern = Pattern.compile("(Université|Organisation|Company|Institute|Corporation|University)\\s+[A-Za-z\\s]+");
        Matcher institutionMatcher = institutionPattern.matcher(text);
        while (institutionMatcher.find()) {
            results.get(INSTITUTION).add(institutionMatcher.group().trim());
        }

        // Regex pour détecter les dates
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{4}");
        Matcher dateMatcher = datePattern.matcher(text);
        while (dateMatcher.find()) {
            results.get(DATE).add(dateMatcher.group());
        }
    }

    // Méthode pour détecter les diplômes
    private static void detectDiplomaEntities(String text, Map<String, List<String>> results) {
        // Regex pour détecter des diplômes (français et anglais)
        Pattern diplomaPattern = Pattern.compile("(Master|Licence|Baccalauréat|Bachelor)\\s+[^\\d-]+");
        Matcher diplomaMatcher = diplomaPattern.matcher(text);
        while (diplomaMatcher.find()) {
            // Ajouter le diplôme trouvé à la map
            results.get(DIPLOME).add(diplomaMatcher.group().trim());
        }
    }
}
