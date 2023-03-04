package com.github.gyrosofwar.imagehive.service.image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public interface ImageTagger {
  Set<String> getTags(InputStream inputStream) throws IOException;

  default Set<String> getTags(Path imagePath) throws IOException {
    try (var inputStream = Files.newInputStream(imagePath)) {
      return getTags(inputStream);
    }
  }
}
