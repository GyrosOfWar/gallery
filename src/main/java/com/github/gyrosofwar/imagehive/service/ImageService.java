package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.GpsDirectory;
import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.*;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypeException;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Singleton
public class ImageService {

  private static final Logger log = LoggerFactory.getLogger(ImageService.class);

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

  public ImageDTO toDto(Image image) {
    return new ImageDTO(
      image.id(),
      image.title(),
      image.height(),
      image.width(),
      image.createdOn(),
      Arrays.asList(image.tags()),
      FilenameUtils.getExtension(Path.of(image.filePath()).getFileName().toString())
    );
  }

  private String getExtension(File tempFile, Optional<MediaType> contentTypeHint, String filename)
    throws ImageProcessingException {
    try (var inputStream = TikaInputStream.get(tempFile.toPath())) {
      var meta = new Metadata();
      meta.add(TikaCoreProperties.ORIGINAL_RESOURCE_NAME, filename);
      contentTypeHint.ifPresent(mediaType ->
        meta.add(TikaCoreProperties.CONTENT_TYPE_HINT, mediaType.toString())
      );
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

    return new ParsedMetadata(lat, lon, result);
  }

  @Transactional
  public ImageDTO create(StreamingFileUpload file, long userId)
    throws IOException, ImageProcessingException {
    var id = Ulid.fast();
    log.info("generated ID {} for upload {}", id, file.getName());
    var tempFile = Files.createTempFile(id.toString(), "tmp");

    Mono.from(file.transferTo(tempFile.toFile())).block();

    var extension = getExtension(tempFile.toFile(), file.getContentType(), file.getFilename());
    log.info("determined extension {} for filename {}", extension, file.getFilename());
    var destinationPath = mediaService.persistImage(tempFile, id, extension, userId);

    log.info("moved temp file {} to {}", tempFile, destinationPath);
    var metadata = getMetadata(destinationPath);
    var metadataJson = JSONB.jsonb(objectMapper.writeValueAsString(metadata.metadata()));
    var bufferedImage = ImageIO.read(destinationPath.toFile());
    var image = new Image(
      id.toUuid(),
      // TODO title
      "",
      OffsetDateTime.now(),
      userId,
      bufferedImage.getWidth(),
      bufferedImage.getHeight(),
      metadata.latitude(),
      metadata.longitude(),
      metadataJson,
      // TODO tags
      new String[] {},
      destinationPath.toString()
    );
    dsl.newRecord(IMAGE, image).insert();
    return toDto(image);
  }

  public Page<ImageDTO> listImages(Pageable pageable, long userId) {
    var images = dsl
      .selectFrom(IMAGE)
      .where(IMAGE.OWNER_ID.eq(userId))
      .orderBy(IMAGE.CREATED_ON.desc())
      .fetchInto(Image.class)
      .stream()
      .map(this::toDto)
      .toList();

    var count = dsl.selectCount().from(IMAGE).where(IMAGE.OWNER_ID.eq(userId)).fetchOne().value1();
    return Page.of(images, pageable, count);
  }

  record ParsedMetadata(
    Double latitude,
    Double longitude,
    Map<String, Map<String, String>> metadata
  ) {}
}
