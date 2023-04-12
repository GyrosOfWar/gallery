package com.github.gyrosofwar.imagehive.service.importer;

import java.io.IOException;
import java.nio.file.Path;

public interface Importer {
    void importBatch(Path uploadedFile, long userId) throws IOException;

    record ImportInformation(
        int addedImages,
        int errors
    ) {

    }
}
