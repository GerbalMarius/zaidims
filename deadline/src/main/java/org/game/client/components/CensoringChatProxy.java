package org.game.client.components;

import lombok.extern.slf4j.Slf4j;
import org.game.utils.ProfanityLoader;

import java.nio.file.Path;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class CensoringChatProxy implements ChatService {
    private final RealChatService realService;

    private static final Set<String> BANNED_WORDS = ProfanityLoader.loadAll(Path.of("assets/profanity"));

    private static final Set<String> BANNED_NORMALIZED = normalizeAll();// for normalized detection

    private static final Pattern directPattern = buildDirectPattern();

    private static final Pattern TOKEN = Pattern.compile("\\p{L}+(?:['’]\\p{L}+)?|\\p{N}+");

    public CensoringChatProxy(RealChatService realService) {
        this.realService = realService;
    }

    private static Pattern buildDirectPattern() {
        String regex = BANNED_WORDS.stream()
                .map(Pattern::quote) // IMPORTANT: escape regex metacharacters
                .collect(Collectors.joining("|", "\\b(", ")\\b"));

        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    @Override
    public void sendMessage(String message) {
        realService.sendMessage(censorMessage(message));
    }

    @Override
    public void addMessage(String playerName, String message) {
        realService.addMessage(playerName, censorMessage(message));
    }

    private String censorMessage(String message) {
        String censored = directPattern.matcher(message)
                .replaceAll(m -> "*".repeat(m.group().length()));

        return censorTokensByNormalization(censored);
    }

    private String censorTokensByNormalization(String message) {
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

    private static String normalizeForMatch(String s) {
        String lower = s.toLowerCase();

        // Normalize unicode forms (full-width chars, compatibility forms, etc.)
        String nfkc = Normalizer.normalize(lower, Normalizer.Form.NFKC);

        // Strip diacritics (ą -> a, ó -> o)
        String noMarks = Normalizer.normalize(nfkc, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return noMarks.replaceAll("[^\\p{L}\\p{N}]+", "");
    }
}
