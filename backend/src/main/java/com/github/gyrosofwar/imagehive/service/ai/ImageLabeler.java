package com.github.gyrosofwar.imagehive.service.ai;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface ImageLabeler {
    String DEFAULT_TAGGING_QUESTION = "Create single-word tags for this image as json array";

    String DEFAULT_DESCRIPTION_QUESTION = "Describe this image";

    Set<String> getTags(Path path) throws IOException;

    String getDescription(Path path) throws IOException;
}
