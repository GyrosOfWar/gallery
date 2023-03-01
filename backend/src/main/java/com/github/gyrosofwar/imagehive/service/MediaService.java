package com.github.gyrosofwar.imagehive.service;

import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.configuration.ImageHiveConfiguration;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MediaService {

  private static final RenameStrategy RENAME_STRATEGY = new RenameStrategy();
  private static final Logger log = LoggerFactory.getLogger(MediaService.class);

  private final ImageHiveConfiguration configuration;

  public MediaService(ImageHiveConfiguration configuration) {
    this.configuration = configuration;
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
    log.info("moving file {} -> {}", tempFilePath, path);
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
    Long userId
  ) throws IOException {
    var originalImage = getImagePath(ulid, extension, userId);
    if (!Files.isRegularFile(originalImage)) {
      return null;
    }

    var fileName = Path.of(
      RENAME_STRATEGY.getName(originalImage.getFileName().toString(), width, height)
    );
    var existingFile = originalImage.getParent().resolve(fileName);
    if (Files.isRegularFile(existingFile)) {
      return ImageData.from(existingFile);
    }

    var image = Thumbnails
      .of(originalImage.toFile())
      .outputFormat("jpg")
      .size(width, height)
      .asFiles(RENAME_STRATEGY)
      .get(0);

    return ImageData.from(image.toPath());
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

  public record ImageData(
    InputStream inputStream,
    MediaType contentType,
    FileTime lastModified,
    long contentLength
  ) {
    public static ImageData from(Path path) throws IOException {
      var inputStream = Files.newInputStream(path);
      var attributes = Files.readAttributes(path, BasicFileAttributes.class);
      var contentLength = attributes.size();
      var lastModified = attributes.lastModifiedTime();
      var contentType = MediaType.forFilename(path.getFileName().toString());

      return new ImageData(inputStream, contentType, lastModified, contentLength);
    }
  }

  private static class RenameStrategy extends Rename {

    public String getName(String name, long width, long height) {
      return appendSuffix(name, String.format(".thumbnail-%s-%s", width, height));
    }

    @Override
    public String apply(String name, ThumbnailParameter param) {
      var width = Math.round(param.getSize().getWidth());
      var height = Math.round(param.getSize().getHeight());
      return getName(name, width, height);
    }
  }
}
