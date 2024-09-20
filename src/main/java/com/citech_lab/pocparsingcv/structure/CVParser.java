package com.citech_lab.pocparsingcv.structure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CVParser {

    public static void main(String[] args) {
        // Texte de votre CV
        String cvText = "(Fares DRAOUI Consultant confirmé Salesforce)Draouifares@gmail.com 06 19 12 68 62\n" +
                "COMPÉTENCES TECHNIQUES\n\nSales Cloud Service Cloud Marketing Reporting\n\n" +
                "EXPÉRIENCES PROFESSIONNELLES\n" +
                "S-Tech Cabinet de conseil et services informatiques Juin 2023 à aujourd’hui\n" +
                "Mission 2 : Date : Fame Estates (Cabinet Immobilier à Malte) Janvier 2024 à aujourd’hui\n" +
                "- Rôle : Consultant Salesforce Réalisation : Audit & Implémentation Salescloud, Pardot, Booker25\n" +
                "- Animation des ateliers d’expression du besoin\n" +
                "Mission 1 : Date : Knight Frank (Agence Immobilière de Luxe) Juin 2023 à Novembre 2023\n" +
                "- Rôle : Consultant Fonctionnel Salesforce Réalisation : Mise en place d’un CRM Salesforce\n" +
                "Ohana Conseil Cabinet de conseil en transformation digitale Janvier 2022 à Mai 2023\n" +
                "- Rôle : Business Analyst Singular\n" +
                "CustomerValue Consulting Avril 2019 à Juillet 2021\n" +
                "- Rôle : Lead BA Salesforce\n" +
                "Mission 3 : Date : CCMI 38 (Promotion Immobilière) Octobre 2020 à Décembre 2020\n";

        // Impression du texte brut pour vérifier son format
        System.out.println("CV Text:\n" + cvText);

        // Expression régulière pour détecter le titre de la section d'expérience professionnelle
        Pattern titlePattern = Pattern.compile("(?i)exp[é]riences?\\s*professionnelle[s]?", Pattern.DOTALL);
        Matcher titleMatcher = titlePattern.matcher(cvText);

        if (titleMatcher.find()) {
            int start = titleMatcher.end();
            String remainingText = cvText.substring(start).trim();

            System.out.println("Professional Experience Section Found!");

            // Expression régulière pour détecter les dates d'emploi
            Pattern datePattern = Pattern.compile(
                    "\\b(?:January|February|March|April|May|June|July|August|September|October|November|December|" +
                            "Janvier|Février|Mars|Avril|Mai|Juin|Juillet|Août|Septembre|Octobre|Novembre|Décembre)\\s\\d{4} (à|-) " +
                            "(?:Présent|Aujourd'hui|Actuel|\\w+ \\d{4})\\b", Pattern.CASE_INSENSITIVE);
            Matcher dateMatcher = datePattern.matcher(remainingText);

            System.out.println("Employment Dates Found:");
            // Extraction des dates d'emploi
            while (dateMatcher.find()) {
                System.out.println(dateMatcher.group());
            }

            // Expression régulière pour détecter les descriptions de poste
            Pattern descriptionPattern = Pattern.compile("(-|•)\\s.*", Pattern.DOTALL);
            Matcher descriptionMatcher = descriptionPattern.matcher(remainingText);

            System.out.println("\nExperience Descriptions Found:\n");
            // Extraction des descriptions de poste
            while (descriptionMatcher.find()) {
                System.out.println(descriptionMatcher.group());
            }
        } else {
            System.out.println("No professional experience section found.");
        }
    }
}
