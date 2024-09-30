package com.citech_lab.pocparsingcv.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CVContactInfoDetectorService {


    private static final String NOM = "Nom";
    private static final String PHONE = "Phone";
    private static final String EMAIL = "Email";

    public static Map<String, List<String>> detectContactInfo(String cvText) {
        // Map pour stocker les résultats par type (Email, Phone, Name)
        Map<String, List<String>> contactInfo = new HashMap<>();

        // listes pour chaque type d'information
        contactInfo.put(EMAIL, new ArrayList<>());
        contactInfo.put(PHONE, new ArrayList<>());
        contactInfo.put(NOM, new ArrayList<>());

        System.out.println("<---------------------------------------------------------->");

        // Détection de l'email
        // Regex pour capturer les adresses email :
        // - [a-zA-Z0-9._%+-]+ : caractères avant le "@" (lettres, chiffres et certains caractères spéciaux)
        // - @[a-zA-Z0-9.-]+ : le "@" suivi du domaine (lettres, chiffres, points et tirets)
        // - \\.[a-zA-Z]{2,6} : un point suivi de 2 à 6 lettres pour l'extension
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        Matcher emailMatcher = emailPattern.matcher(cvText);
        if (emailMatcher.find()) {
            String email = emailMatcher.group();
            System.out.println("Email Found: " + email);
            contactInfo.get(EMAIL).add(email); // Ajoute l'email à la liste
        } else {
            System.out.println("No email found.");
        }

        // Détection du numéro de téléphone
        // Regex pour capturer les numéros de téléphone :
        // - 0[1-9][0-9]{8} : un numéro français classique (10 chiffres)
        // - |\\+?[0-9]{1,3} : ou un format international avec indicatif (ex: +33)
        // - \\(?[0-9]{1,4}\\)? : un code de zone (1 à 4 chiffres, entre parenthèses ou non)
        // - [ -]? : un espace ou un tiret optionnel
        // - [0-9]{1,4} : 1 à 4 chiffres
        // - [ -]? : un espace ou un tiret optionnel
        // - [0-9]{1,9} : 1 à 9 chiffres
        Pattern phonePattern = Pattern.compile("(0[1-9][0-9]{8}|\\+?[0-9]{1,3}[ -]?\\(?[0-9]{1,4}\\)?[ -]?[0-9]{1,4}[ -]?[0-9]{1,9})");
        Matcher phoneMatcher = phonePattern.matcher(cvText);
        if (phoneMatcher.find()) {
            String phone = phoneMatcher.group();
            System.out.println("Phone Number Found: " + phone);
            contactInfo.get(PHONE).add(phone); // Ajoute le numéro de téléphone à la liste
        } else {
            System.out.println("No phone number found.");
        }

        // Détection du nom
        // Regex pour capturer les noms au format "Prénom Nom" :
        // - ^[A-Z][a-z]+ : commence par une majuscule suivie de lettres minuscules (Prénom)
        // - \\s : un espace
        // - [A-Z][a-z]+ : suivi d'une majuscule puis de lettres minuscules (Nom)
        Pattern namePattern = Pattern.compile("^[A-Z][a-z]+\\s[A-Z][a-z]+", Pattern.MULTILINE);
        Matcher nameMatcher = namePattern.matcher(cvText);
        if (nameMatcher.find()) {
            String name = nameMatcher.group();
            System.out.println("Name Found: " + name);
            contactInfo.get(NOM).add(name); // Ajoute le nom à la liste
        } else {
            System.out.println("No name found.");
        }

        System.out.println("<---------------------------------------------------------->");
        return contactInfo; // Retourne la Map avec les résultats
    }
}


