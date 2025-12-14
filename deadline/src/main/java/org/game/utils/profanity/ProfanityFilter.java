package org.game.utils.profanity;

import java.nio.file.Path;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ProfanityFilter {
    public static final Set<String> BANNED_WORDS = ProfanityLoader.loadAll(Path.of("assets/profanity"));

    private static final Set<String> BANNED_NORMALIZED = normalizeAll();// for normalized detection

    private static final Pattern directPattern = buildDirectPattern();

    private static final Pattern TOKEN = Pattern.compile("\\p{L}+(?:['’]\\p{L}+)?|\\p{N}+");
    private ProfanityFilter(){}

    public static String censorMessage(String message) {
        String censored = directPattern.matcher(message)
                .replaceAll(m -> "*".repeat(m.group().length()));

        return censorTokensByNormalization(censored);
    }

    public static String normalizeForMatch(String s) {
        String lower = s.toLowerCase();

        // Normalize unicode forms (full-width chars, compatibility forms, etc.)
        String nfkc = Normalizer.normalize(lower, Normalizer.Form.NFKC);

        // Strip diacritics (ą -> a, ó -> o)
        String noMarks = Normalizer.normalize(nfkc, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return noMarks.replaceAll("[^\\p{L}\\p{N}]+", "");
    }

    public static boolean containsProfanity(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }

        if (directPattern.matcher(str).find()) {
            return true;
        }

        Matcher m = TOKEN.matcher(str);
        while (m.find()) {
            String token = m.group();
            String norm = normalizeForMatch(token);

            if (norm.length() >= 2 && BANNED_NORMALIZED.contains(norm)) {
                return true;
            }
        }

        return false;
    }

    private static String censorTokensByNormalization(String message) {
        Matcher m = TOKEN.matcher(message);
        StringBuilder sb = new StringBuilder(message.length());

        while (m.find()) {
            String token = m.group();
            String norm = normalizeForMatch(token);

            if (norm.length() >= 2 && BANNED_NORMALIZED.contains(norm)) {
                m.appendReplacement(sb, Matcher.quoteReplacement("*".repeat(token.length())));
            } else {
                m.appendReplacement(sb, Matcher.quoteReplacement(token));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }


    private static Set<String> normalizeAll() {
        Set<String> normalized = HashSet.newHashSet(BANNED_WORDS.size());
        for (String word : BANNED_WORDS) {
            normalized.add(normalizeForMatch(word));
        }
        return normalized;
    }


    private static Pattern buildDirectPattern() {
        String regex = BANNED_WORDS.stream()
                .map(Pattern::quote) // IMPORTANT: escape regex metacharacters
                .collect(Collectors.joining("|", "\\b(", ")\\b"));

        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }
}
