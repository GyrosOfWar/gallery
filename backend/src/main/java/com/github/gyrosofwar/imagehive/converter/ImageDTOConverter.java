package com.github.gyrosofwar.imagehive.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.dto.ImageMetadata;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.jooq.JSONB;

@Singleton
public class ImageDTOConverter implements Converter<Image, ImageDTO> {

  private static final TypeReference<Map<String, Map<String, String>>> METADATA_SHAPE = new TypeReference<>() {};
  private final ObjectMapper objectMapper;

  public ImageDTOConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private Map<String, Map<String, String>> parseMetadata(JSONB jsonb) {
    try {
      return objectMapper.readValue(jsonb.data(), METADATA_SHAPE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ImageDTO convert(Image image) {
    return new ImageDTO(
      image.id(),
      image.title(),
      image.description(),
      image.height(),
      image.width(),
      image.createdOn(),
      image.capturedOn(),
      image.gpsLatitude(),
      image.gpsLongitude(),
      Arrays.asList(image.tags()),
      FilenameUtils.getExtension(Path.of(image.filePath()).getFileName().toString()),
      ImageMetadata.from(parseMetadata(image.metadata()))
    );
  }
}
