package com.citech_lab.pocparsingcv.structure;

import opennlp.tools.namefind.*;
import opennlp.tools.util.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CVEntityExtractor {

    public static void main(String[] args) throws Exception {
        // Chargement du modèle NER depuis le dossier resources
        InputStream modelIn = CVEntityExtractor.class.getResourceAsStream("/en-ner-organization.bin");

        if (modelIn == null) {
            throw new FileNotFoundException("Le modèle NER est introuvable.");
        }

        // Création du modèle NER à partir du fichier
        TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
        NameFinderME nameFinder = new NameFinderME(model);

        // Exemple de texte à analyser (avec et sans tirets)
        String text = " 2018-2020 Master in Software Engineering Université de Madagascar ";
        String textWithHyphens = "Master in Software Engineering - Organisation Mondiale de la Santé - 2018-2020";

        // Analyser le texte sans tirets
        Map<String, List<String>> results = analyzeText(nameFinder, text);

        // Analyser le texte avec tirets
        results.putAll(analyzeText(nameFinder, textWithHyphens));

        // Fermeture du flux de modèle
        modelIn.close();

        // Afficher les résultats
        System.out.println("Résultats de la catégorie 'formation': " + results);
    }

    // Méthode pour analyser un texte donné
    private static Map<String, List<String>> analyzeText(NameFinderME nameFinder, String text) {
        System.out.println("Analyzing: " + text);

        // Map pour stocker les résultats
        Map<String, List<String>> results = new HashMap<>();
        results.put("formation", new ArrayList<>());

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
            // Ajouter l'institution trouvée à la map
            results.get("formation").add("Institution : " + institutionMatcher.group().trim());
        }

        // Regex pour détecter les dates
        Pattern datePattern = Pattern.compile("\\d{4}-\\d{4}");
        Matcher dateMatcher = datePattern.matcher(text);
        while (dateMatcher.find()) {
            // Ajouter la date trouvée à la map
            results.get("formation").add("Date : " + dateMatcher.group());
        }
    }

    // Méthode pour détecter les diplômes
    private static void detectDiplomaEntities(String text, Map<String, List<String>> results) {
        // Regex pour détecter des diplômes (français et anglais)
        Pattern diplomaPattern = Pattern.compile("(Master|Licence|Baccalauréat|Bachelor)\\s+[^\\d-]+");
        Matcher diplomaMatcher = diplomaPattern.matcher(text);
        while (diplomaMatcher.find()) {
            // Ajouter le diplôme trouvé à la map
            results.get("formation").add("Diplôme : " + diplomaMatcher.group().trim());
        }
    }
}
