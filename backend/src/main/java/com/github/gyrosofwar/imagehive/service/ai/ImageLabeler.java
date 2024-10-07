package com.github.gyrosofwar.imagehive.service.ai;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public interface ImageLabeler {
  String DEFAULT_TAGGING_QUESTION = "Create single-word tags for this image as JSON array.";

  String DEFAULT_DESCRIPTION_QUESTION = "Describe this image";

  Set<String> getTags(InputStream inputStream) throws IOException;

  String getDescription(InputStream inputStream) throws IOException;

  default String getDescription(Path path) throws IOException {
    try (var inputStream = Files.newInputStream(path)) {
      return getDescription(inputStream);
    }
  }

  default Set<String> getTags(Path path) throws IOException {
    try (var inputStream = Files.newInputStream(path)) {
      return getTags(inputStream);
    }
  }
}
