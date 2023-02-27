package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.dto.ImageMetadata;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Pageable;
import io.micronaut.serde.ObjectMapper;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ImageService {

  private static final Logger log = LoggerFactory.getLogger(ImageService.class);
  private static final List<String> IGNORED_EXIF_DIRECTORIES = List.of("ICC Profile");

  private static final Argument<Map<String, String>> STRING_STRING_MAP = Argument.mapOf(
    String.class,
    String.class
  );
  private static final Argument<Map<String, Map<String, String>>> METADATA_SHAPE = Argument.mapOf(
    Argument.STRING,
    STRING_STRING_MAP
  );

  private final DSLContext dsl;
  private final TikaConfig tikaConfig;
  private final ObjectMapper objectMapper;
  private final MediaService mediaService;

  public ImageService(
    DSLContext dsl,
    TikaConfig tikaConfig,
    ObjectMapper objectMapper,
    MediaService mediaService
  ) {
    this.dsl = dsl;
    this.tikaConfig = tikaConfig;
    this.objectMapper = objectMapper;
    this.mediaService = mediaService;
  }

  public Image getByUuid(UUID uuid) {
    return dsl.selectFrom(IMAGE).where(IMAGE.ID.eq(uuid)).fetchOneInto(Image.class);
  }

  private Map<String, Map<String, String>> parseMetadata(JSONB jsonb) {
    try {
      return objectMapper.readValue(jsonb.data(), METADATA_SHAPE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ImageDTO toDto(Image image) {
    return new ImageDTO(
      image.id(),
      image.title(),
      image.description(),
      image.height(),
      image.width(),
      image.createdOn(),
      image.capturedOn(),
      Arrays.asList(image.tags()),
      FilenameUtils.getExtension(Path.of(image.filePath()).getFileName().toString()),
      ImageMetadata.from(parseMetadata(image.metadata()))
    );
  }

  public List<ImageDTO> toDtoList(List<Image> images) {
    List<ImageDTO> dtos = new ArrayList<>();
    images.forEach(img -> dtos.add(toDto(img)));
    return dtos;
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

  private OffsetDateTime toOffsetDate(Date date) {
    return OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
  }

  @Transactional
  public ImageDTO create(NewImage newImage) throws IOException, ImageProcessingException {
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
      newImage.description(),
      OffsetDateTime.now(),
      metadata.captureDate(),
      userId,
      bufferedImage.getWidth(),
      bufferedImage.getHeight(),
      metadata.latitude(),
      metadata.longitude(),
      metadataJson,
      newImage.tags().toArray(new String[0]),
      destinationPath.toString()
    );
    dsl.newRecord(IMAGE, image).insert();
    log.info("inserted new image {}", image);
    return toDto(image);
  }

  public List<ImageDTO> listImages(@Nullable String query, Pageable pageable, long userId) {
    var where = IMAGE.OWNER_ID.eq(userId);
    if (StringUtils.isNotBlank(query)) {
      where =
        where.and(DSL.condition("ts_vec @@ plainto_tsquery('english', {0})", DSL.inline(query)));
    }

    return dsl
      .selectFrom(IMAGE)
      .where(where)
      .orderBy(IMAGE.CAPTURED_ON.desc().nullsLast(), IMAGE.CREATED_ON.desc())
      .offset(pageable.getOffset())
      .limit(pageable.getSize())
      .fetchInto(Image.class)
      .stream()
      .map(this::toDto)
      .toList();
  }

  private record ParsedMetadata(
    Double latitude,
    Double longitude,
    OffsetDateTime captureDate,
    Map<String, Map<String, String>> metadata
  ) {}
}
