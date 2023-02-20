package com.github.gyrosofwar.imagehive.service;

import com.github.f4b6a3.ulid.Ulid;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MediaService {

  private static final Logger log = LoggerFactory.getLogger(MediaService.class);

  @Value("${imagehive.image-base-path}")
  private String basePath;

  // exposed for testing
  // builds a path like <base-path/<user-id>/<two-hex-chars>/uuid.jpg
  Path getImagePath(Ulid ulid, @Nullable String extension, long userId) {
    var randomPart = ulid.getRandom();
    var subFolder = DatatypeConverter
      .printHexBinary(randomPart)
      .substring(0, 2);
    var folder = Path.of(basePath, String.valueOf(userId), subFolder);

    var fileName = ulid.toString();
    if (StringUtils.isNotBlank(extension)) {
      if (!extension.startsWith(".")) {
        extension = "." + extension;
      }

      fileName += extension;
    }
    return folder.resolve(fileName);
  }

  public Path persistImage(
    Path tempFilePath,
    Ulid ulid,
    String extension,
    long userId
  ) throws IOException {
    var path = getImagePath(ulid, extension, userId);
    log.info("moving file {} -> {}", tempFilePath, path);
    if (!Files.isDirectory(path.getParent())) {
      Files.createDirectories(path.getParent());
    }

    Files.move(tempFilePath, path);

    return path;
  }

  public @Nullable InputStream getImageBytes(
    Ulid ulid,
    @Nullable String extension,
    Long userId
  ) throws IOException {
    if (userId == null) {
      return null;
    }

    var path = getImagePath(ulid, extension, userId);
    if (extension == null) {
      path = findFileInFolder(path);
    }

    if (path == null || !Files.isRegularFile(path)) {
      return null;
    }

    return Files.newInputStream(path);
  }

  private Path findFileInFolder(Path prefix) throws IOException {
    try (var stream = Files.list(prefix.getParent())) {
      return stream
        .filter(path -> path.getFileName().startsWith(prefix.getFileName()))
        .findFirst()
        .orElse(null);
    }
  }
}
