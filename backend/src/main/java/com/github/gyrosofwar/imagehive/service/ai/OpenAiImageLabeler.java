package com.github.gyrosofwar.imagehive.service.ai;

import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@Singleton
@Requires("${imagehive.ai.enabled}")
public class OllamaImageLabeler implements ImageLabeler {
    @Override
    public Set<String> getTags(Path path) throws IOException {
        return Set.of();
    }

    @Override
    public String getDescription(Path path) throws IOException {
        return "";
    }
}
