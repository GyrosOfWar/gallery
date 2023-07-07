package com.github.gyrosofwar.imagehive.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gyrosofwar.imagehive.dto.image.ImageDetailsDTO;
import com.github.gyrosofwar.imagehive.dto.image.ImageMetadata;
import com.github.gyrosofwar.imagehive.dto.image.LocationInfo;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import mil.nga.sf.geojson.FeatureCollection;
import org.apache.commons.io.FilenameUtils;
import org.jooq.JSONB;

@Singleton
public class ImageDetailsDTOConverter implements Converter<Image, ImageDetailsDTO> {

  private static final TypeReference<Map<String, Map<String, String>>> METADATA_SHAPE = new TypeReference<>() {};
  private final ObjectMapper objectMapper;

  public ImageDetailsDTOConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  static String getExtension(String path) {
    return FilenameUtils.getExtension(Path.of(path).getFileName().toString());
  }

  private Map<String, Map<String, String>> parseMetadata(JSONB jsonb) {
    try {
      return objectMapper.readValue(jsonb.data(), METADATA_SHAPE);
    } catch (IOException e) {
      // don't crash when we can't convert successfully
      return null;
    }
  }

  private LocationInfo parseGeoJson(JSONB jsonb) {
    try {
      if (jsonb == null) {
        return null;
      }

      var features = objectMapper.readValue(jsonb.data(), FeatureCollection.class);
      if (features.getFeatures().isEmpty()) {
        return null;
      } else {
        var properties = features.getFeatures().get(0).getProperties();
        var country = (String) properties.get("country");
        var city = (String) properties.get("city");
        var district = (String) properties.get("district");
        var locality = (String) properties.get("locality");
        var street = (String) properties.get("street");

        return new LocationInfo(country, city, district, locality, street);
      }
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public ImageDetailsDTO convert(Image image) {
    if (image == null) {
      return null;
    }

    return new ImageDetailsDTO(
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
      getExtension(image.filePath()),
      ImageMetadata.from(parseMetadata(image.metadata())),
      image.favorite(),
      parseGeoJson(image.geoJson())
    );
  }
}
