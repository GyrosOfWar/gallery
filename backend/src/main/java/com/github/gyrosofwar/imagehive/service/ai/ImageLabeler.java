package com.github.gyrosofwar.imagehive.service.ai;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.apache.tika.mime.MimeTypeException;

public interface ImageLabeler {
  String DEFAULT_TAGGING_QUESTION = "Create single-word tags for this image as JSON array.";

  String DEFAULT_DESCRIPTION_QUESTION = "Describe this image";

  Set<String> getTags(BufferedInputStream inputStream) throws IOException;

  String getDescription(BufferedInputStream inputStream) throws IOException, MimeTypeException;

  default String getDescription(Path path) throws IOException, MimeTypeException {
    try (var inputStream = new BufferedInputStream(Files.newInputStream(path))) {
      return getDescription(inputStream);
    }
  }

  default Set<String> getTags(Path path) throws IOException {
    try (var inputStream = new BufferedInputStream(Files.newInputStream(path))) {
      return getTags(inputStream);
    }
  }
}
