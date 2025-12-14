package org.game.utils.profanity;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
final class ProfanityLoader {
    private ProfanityLoader(){}

     static Set<String> loadAll(Path path) {
        try (Stream<Path> paths = Files.walk(path)){
            return paths.filter(Files::isRegularFile)
                    .flatMap(ProfanityLoader::lines)
                    .map(String::trim)
                    .filter(str -> !str.isEmpty() && !str.startsWith("#"))
                    .collect(Collectors.toUnmodifiableSet());
        } catch (IOException ioex) {
            log.error("Error loading profanity list", ioex);
            return Set.of();
        }
    }

    private static Stream<String> lines(Path path) {
        try {
            return Files.lines(path, StandardCharsets.UTF_8);
        }catch (IOException ioex) {
            log.error("Error reading profanity list", ioex);
            return Stream.empty();
        }
    }
}
