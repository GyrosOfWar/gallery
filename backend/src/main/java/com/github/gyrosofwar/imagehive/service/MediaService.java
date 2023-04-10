package com.github.gyrosofwar.imagehive.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.configuration.ImageHiveConfiguration;
import com.github.gyrosofwar.imagehive.service.thumbnails.Thumbnailer;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MediaService {

  private static final Logger log = LoggerFactory.getLogger(MediaService.class);

  private final ImageHiveConfiguration configuration;
  private final Thumbnailer thumbnailer;

  public MediaService(ImageHiveConfiguration configuration, Thumbnailer thumbnailer) {
    this.configuration = configuration;
    this.thumbnailer = thumbnailer;
  }

  // exposed for testing
  // builds a path like <base-path/<user-id>/<two-hex-chars>/<uuid>.jpg
  Path getImagePath(Ulid ulid, @Nullable String extension, long userId) {
    var randomPart = ulid.getRandom();
    var subFolder = DatatypeConverter.printHexBinary(randomPart).substring(0, 2);
    var folder = Path.of(configuration.imageBasePath(), String.valueOf(userId), subFolder);

    var fileName = ulid.toString();
    if (StringUtils.isNotBlank(extension)) {
      if (!extension.startsWith(".")) {
        extension = "." + extension;
      }

      fileName += extension;
    }
    return folder.resolve(fileName);
  }

  public Path persistImage(Path tempFilePath, Ulid ulid, String extension, long userId)
    throws IOException {
    var path = getImagePath(ulid, extension, userId);
    log.debug("moving file {} -> {}", tempFilePath, path);
    if (!Files.isDirectory(path.getParent())) {
      Files.createDirectories(path.getParent());
    }

    Files.move(tempFilePath, path);

    return path;
  }

  public @Nullable ImageData getImageBytes(Ulid ulid, @Nullable String extension, Long userId)
    throws IOException {
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

    return ImageData.from(path);
  }

  public @Nullable ImageData getImageThumbnail(
    Ulid ulid,
    @Nullable String extension,
    int width,
    int height,
    Integer dpr,
    HttpHeaders headers,
    Long userId
  ) throws IOException {
    var originalImage = getImagePath(ulid, extension, userId);
    if (!Files.isRegularFile(originalImage)) {
      return null;
    }
    Thumbnailer.FileType fileType = Thumbnailer.FileType.JPEG;
    if (!headers.accept().isEmpty()) {
      var mediaType = headers.accept().get(0);
      switch (mediaType.toString()) {
        case "image/webp" -> fileType = Thumbnailer.FileType.WEBP;
        case "image/avif" -> fileType = Thumbnailer.FileType.AVIF;
      }
    }

    var widthHeader = headers.getInt("Width");
    if (widthHeader != null) {
      width = widthHeader;
    }

    var dprHeader = headers.getInt("DPR");
    if (dprHeader != null) {
      dpr = dprHeader;
    }

    return thumbnailer.getThumbnail(
      new Thumbnailer.Request(originalImage, width, height, fileType, false, dpr == null ? 1 : dpr)
    );
  }

  private Path findFileInFolder(Path prefix) throws IOException {
    try (var stream = Files.list(prefix.getParent())) {
      return stream
        .filter(path -> path.getFileName().startsWith(prefix.getFileName()))
        .findFirst()
        .orElse(null);
    }
  }

  public void delete(UUID uuid, Path filePath, long userId) throws IOException {
    var extension = FilenameUtils.getExtension(filePath.getFileName().toString());
    var basePath = getImagePath(Ulid.from(uuid), extension, userId).getParent();
    try (var stream = Files.list(basePath)) {
      var filesToDelete = stream.filter(f -> f.startsWith(uuid.toString())).toList();
      for (var path : filesToDelete) {
        log.error("deleting file {}", path);
        Files.delete(path);
      }
    }
  }
}
