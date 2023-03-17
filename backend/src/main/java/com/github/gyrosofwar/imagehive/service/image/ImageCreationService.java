package com.github.gyrosofwar.imagehive.service.image;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.service.MediaService;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ImageCreationService {

  private static final Logger log = LoggerFactory.getLogger(ImageCreationService.class);
  private static final List<String> IGNORED_EXIF_DIRECTORIES = List.of("ICC Profile");

  private final MediaService mediaService;
  private final ObjectMapper objectMapper;
  private final TikaConfig tikaConfig;
  private final DSLContext dsl;

  public ImageCreationService(
    MediaService mediaService,
    ObjectMapper objectMapper,
    TikaConfig tikaConfig,
    DSLContext dsl
  ) {
    this.mediaService = mediaService;
    this.objectMapper = objectMapper;
    this.tikaConfig = tikaConfig;
    this.dsl = dsl;
  }

  private OffsetDateTime toOffsetDate(Date date) {
    return OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
  }

  private OffsetDateTime extractDate(com.drew.metadata.Metadata metadata) {
    var exifIfd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
    if (exifIfd0 != null) {
      var tagIds = List.of(
        ExifIFD0Directory.TAG_DATETIME,
        ExifIFD0Directory.TAG_DATETIME_DIGITIZED,
        ExifIFD0Directory.TAG_DATETIME_ORIGINAL
      );

      for (int tagId : tagIds) {
        var date = exifIfd0.getDate(tagId, TimeZone.getTimeZone(ZoneOffset.UTC));
        if (date != null) {
          return toOffsetDate(date);
        }
      }
    }

    var iptc = metadata.getFirstDirectoryOfType(IptcDirectory.class);
    if (iptc != null) {
      if (iptc.getDateCreated() != null) {
        return toOffsetDate(iptc.getDateCreated());
      }
      if (iptc.getDigitalDateCreated() != null) {
        return toOffsetDate(iptc.getDigitalDateCreated());
      }
    }

    return null;
  }

  private ParsedMetadata getMetadata(Path path) throws ImageProcessingException, IOException {
    Map<String, Map<String, String>> result = new HashMap<>();
    var metadata = ImageMetadataReader.readMetadata(path.toFile());
    for (var directory : metadata.getDirectories()) {
      if (IGNORED_EXIF_DIRECTORIES.contains(directory.getName())) {
        continue;
      }

      Map<String, String> values = new HashMap<>();
      for (var tag : directory.getTags()) {
        values.put(tag.getTagName(), tag.getDescription());
      }

      result.put(directory.getName(), values);
    }
    var gps = metadata.getDirectoriesOfType(GpsDirectory.class);
    Double lat = null;
    Double lon = null;

    if (!gps.isEmpty()) {
      var data = gps.iterator().next();
      if (data.getGeoLocation() != null) {
        lat = data.getGeoLocation().getLatitude();
        lon = data.getGeoLocation().getLongitude();
      }
    }

    OffsetDateTime capturedOn = extractDate(metadata);
    return new ParsedMetadata(lat, lon, capturedOn, result);
  }

  private String getExtension(File tempFile, @Nullable String contentTypeHint, String filename)
    throws ImageProcessingException {
    try (var inputStream = TikaInputStream.get(tempFile.toPath())) {
      var meta = new Metadata();
      meta.add(TikaCoreProperties.ORIGINAL_RESOURCE_NAME, filename);
      if (StringUtils.isNotBlank(contentTypeHint)) {
        meta.add(TikaCoreProperties.CONTENT_TYPE_HINT, contentTypeHint);
      }

      var detectedMime = tikaConfig.getDetector().detect(inputStream, meta);
      log.info(
        "detected mime type {} for mime type hint {} and file name {}",
        detectedMime,
        contentTypeHint,
        filename
      );
      return tikaConfig.getMimeRepository().forName(detectedMime.toString()).getExtension();
    } catch (IOException | MimeTypeException e) {
      throw new ImageProcessingException("Error determining extension", e);
    }
  }

  @Transactional
  public Image create(NewImage newImage) throws IOException, ImageProcessingException {
    var id = Ulid.fast();
    var userId = newImage.userId();
    log.info("generated ID {} for upload {}", id, newImage.fileName());
    var tempFile = Files.createTempFile(id.toString(), "tmp");

    try (
      var inputStream = newImage.inputStream();
      var outputStream = Files.newOutputStream(tempFile)
    ) {
      inputStream.transferTo(outputStream);
    }
    var tags = newImage.tags().toArray(new String[0]); // getTags(tempFile, newImage.tags());
    var description = newImage.description();

    var extension = getExtension(tempFile.toFile(), newImage.mimeType(), newImage.fileName());
    log.info("determined extension {} for filename {}", extension, newImage.fileName());
    var destinationPath = mediaService.persistImage(tempFile, id, extension, userId);

    log.info("moved temp file {} to {}", tempFile, destinationPath);
    var metadata = getMetadata(destinationPath);
    var metadataJson = JSONB.jsonb(objectMapper.writeValueAsString(metadata.metadata()));
    var bufferedImage = ImageIO.read(destinationPath.toFile());
    var image = new Image(
      id.toUuid(),
      newImage.title(),
      description,
      OffsetDateTime.now(),
      metadata.captureDate(),
      userId,
      bufferedImage.getWidth(),
      bufferedImage.getHeight(),
      metadata.latitude(),
      metadata.longitude(),
      metadataJson,
      tags,
      false,
      destinationPath.toString()
    );
    dsl.newRecord(IMAGE, image).insert();
    log.info("inserted new image {}", image);
    return image;
  }

  private record ParsedMetadata(
    Double latitude,
    Double longitude,
    OffsetDateTime captureDate,
    Map<String, Map<String, String>> metadata
  ) {}
}
