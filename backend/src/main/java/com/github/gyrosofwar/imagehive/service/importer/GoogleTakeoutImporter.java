package com.github.gyrosofwar.imagehive.service.importer;

import com.github.gyrosofwar.imagehive.service.image.ImageCreationService;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GoogleTakeoutImporter {

  // TODO support webp, avif, heic, mp4
  private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(".jpeg", ".jpg", ".png");
  private static final Logger log = LoggerFactory.getLogger(GoogleTakeoutImporter.class);

  private final ImageCreationService imageCreationService;

  public GoogleTakeoutImporter(ImageCreationService imageCreationService) {
    this.imageCreationService = imageCreationService;
  }

  private static void unzip(Path zipFilePath, Path destDir) throws IOException {
    log.info("Unzipping archive {} to {}", zipFilePath, destDir);
    if (!Files.isDirectory(destDir)) {
      Files.createDirectories(destDir);
    }

    try (
      var inputStream = Files.newInputStream(zipFilePath);
      var zipInputStream = new ZipInputStream(inputStream)
    ) {
      ZipEntry entry;

      while ((entry = zipInputStream.getNextEntry()) != null) {
        var fileName = entry.getName();
        var newFile = destDir.resolve(fileName);
        Files.createDirectories(newFile.getParent());

        try (var outputStream = Files.newOutputStream(newFile)) {
          zipInputStream.transferTo(outputStream);
        }
      }

      zipInputStream.closeEntry();
    }
  }

  private boolean isSupportedFile(Path path) {
    if (path.getFileName() != null) {
      var fileName = path.getFileName().toString();
      return SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    } else {
      return false;
    }
  }

  public void importBatch(Path uploadedFile) throws IOException {
    var destinationDirectory = Files.createTempDirectory("google-takeout-import");
    unzip(uploadedFile, destinationDirectory);
    try (var stream = Files.walk(destinationDirectory)) {
      stream
        .filter(this::isSupportedFile)
        .forEach(path -> {
          // todo
          // var metadata = loadMetadataForImage(path);
          // imageService.create();
        });
    }
  }

  private TakeoutMetadata loadMetadataForImage(Path path) {
    return null;
  }

  record TakeoutMetadata() {}
}
