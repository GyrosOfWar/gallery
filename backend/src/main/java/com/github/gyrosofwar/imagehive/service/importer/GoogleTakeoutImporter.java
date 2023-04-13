package com.github.gyrosofwar.imagehive.service.importer;

import com.drew.imaging.ImageProcessingException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gyrosofwar.imagehive.helper.ZipHelper;
import com.github.gyrosofwar.imagehive.service.image.ImageCreationService;
import com.github.gyrosofwar.imagehive.service.image.NewImage;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GoogleTakeoutImporter {

  private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
    ".jpeg",
    ".jpg",
    ".png",
    ".heic",
    ".webp",
    ".tiff",
    ".tif"
  );
  private static final Logger log = LoggerFactory.getLogger(GoogleTakeoutImporter.class);

  private final ImageCreationService imageCreationService;
  private final ObjectMapper objectMapper;

  public GoogleTakeoutImporter(
    ImageCreationService imageCreationService,
    ObjectMapper objectMapper
  ) {
    this.imageCreationService = imageCreationService;
    this.objectMapper = objectMapper;
  }

  public FinishedImport importBatch(List<Path> zipFiles, String importId, long userId)
    throws IOException {
    var destinationDirectory = Files.createTempDirectory("google-takeout-import");
    for (var zipFile : zipFiles) {
      ZipHelper.unzip(zipFile, destinationDirectory);
    }

    var errors = 0;
    var successfulImports = 0;
    try (var stream = Files.walk(destinationDirectory)) {
      var allFiles = stream.filter(this::isSupportedFile).toList();
      log.info("found {} supported files in folder", allFiles.size());
      int count = 0;
      for (var path : allFiles) {
        try {
          var metadata = loadMetadataForImage(path);
          if (metadata != null) {
            var newImage = newImage(path, userId, metadata);
            imageCreationService.create(newImage);
            Files.delete(path);
            successfulImports += 1;
          } else {
            errors += 1;
          }
        } catch (IOException | ImageProcessingException e) {
          log.error("failed to create image " + path, e);
          errors += 1;
        }
        count += 1;
        log.info("processed file {} / {}", count, allFiles.size());
      }
    }

    var info = new FinishedImport(successfulImports, errors);
    log.info("finished importing archive: {}", info);
    return info;
  }

  private boolean isSupportedFile(Path path) {
    if (path.getFileName() != null) {
      var fileName = path.getFileName().toString();
      return SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    } else {
      return false;
    }
  }

  private TakeoutMetadata loadMetadataForImage(Path path) {
    var fileName = path.getFileName().toString().replace("-edited", "");
    fileName += ".json";

    var jsonPath = path.resolveSibling(fileName);
    try {
      return objectMapper.readValue(jsonPath.toFile(), TakeoutMetadata.class);
    } catch (Exception e) {
      log.info("unable to read takeout metadata at" + jsonPath);
      log.debug("full exception:", e);
      return null;
    }
  }

  private NewImage newImage(Path path, long userId, TakeoutMetadata metadata) throws IOException {
    var inputStream = Files.newInputStream(path);
    return new NewImage(
      inputStream,
      userId,
      path.getFileName().toString(),
      null,
      metadata.title(),
      metadata.description(),
      List.of("takeout-import")
    );
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record TakeoutMetadata(
    String title,
    String description,
    Timestamp creationTime,
    Timestamp photoTakenTime,
    GeoData geoData,
    GeoData geoDataExif
  ) {}

  record Timestamp(String timestamp, String formatted) {}

  record GeoData(
    double latitude,
    double longitude,
    double altitude,
    double latitudeSpan,
    double longitudeSpan
  ) {}
}
