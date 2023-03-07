package com.github.gyrosofwar.imagehive.service.image;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface ImageLabeler {
  Set<String> getTags(Path path) throws IOException;

  String getDescription(Path path) throws IOException;
}
