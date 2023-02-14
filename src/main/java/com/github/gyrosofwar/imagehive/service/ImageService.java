package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.github.f4b6a3.ulid.Ulid;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.multipart.CompletedPart;
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
  private final Path imageBasePath = Path.of("images");
  private final TikaConfig tikaConfig;
  private final ObjectMapper objectMapper;

  public ImageService(DSLContext dsl, TikaConfig tikaConfig, ObjectMapper objectMapper) {
    this.dsl = dsl;
    this.tikaConfig = tikaConfig;
    this.objectMapper = objectMapper;
  }

  public Image getByUuid(UUID uuid) {
    return dsl.selectFrom(IMAGE).where(IMAGE.ID.eq(uuid)).fetchOneInto(Image.class);
  }

  public ImageDTO toDto(Image image) {
    return new ImageDTO(
      image.height(),
      image.width(),
      image.createdOn(),
      Arrays.asList(image.tags())
    );
  }

  public Path getImagePath(UUID imageId, String extension) {
    return imageBasePath.resolve(imageId.toString() + "." + extension);
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

  private JSONB getMetadata(Path path) throws ImageProcessingException, IOException {
    Map<String, String> result = new HashMap<>();
    var metadata = ImageMetadataReader.readMetadata(path.toFile());
    for (var directory : metadata.getDirectories()) {
      for (var tag : directory.getTags()) {
        result.put(tag.getTagName(), tag.getDescription());
      }
    }

    var string = objectMapper.writeValueAsString(result);
    return JSONB.jsonb(string);
  }

  @Transactional
  public ImageDTO create(CompletedFileUpload file, long userId)
    throws IOException, ImageProcessingException {
    var id = Ulid.fast().toUuid();
    log.info("generated ID {} for upload {}", id, file.getName());
    var tempFile = Files.createTempFile(id.toString(), "tmp");
    try (var outputStream = Files.newOutputStream(tempFile)) {
      file.getInputStream().transferTo(outputStream);
    }

    var extension = getExtension(tempFile.toFile(), file.getContentType(), file.getFilename());
    log.info("determined extension {} for filename {}", extension, file.getFilename());
    var destinationPath = getImagePath(id, extension);
    Files.move(tempFile, destinationPath);
    log.info("moved temp file {} to {}", tempFile, destinationPath);
    var metadata = getMetadata(destinationPath);
    var bufferedImage = ImageIO.read(destinationPath.toFile());
    var image = new Image(
      id,
      OffsetDateTime.now(),
      userId,
      bufferedImage.getHeight(),
      bufferedImage.getWidth(),
      // TODO
      null,
      metadata,
      // TODO
      new String[] {}
    );
    dsl.newRecord(IMAGE, image).insert();
    return toDto(image);
  }

  public List<ImageDTO> listImages() {
    return dsl
      .selectFrom(IMAGE)
      .orderBy(IMAGE.CREATED_ON.desc())
      .fetchInto(Image.class)
      .stream()
      .map(this::toDto)
      .toList();
  }
}
