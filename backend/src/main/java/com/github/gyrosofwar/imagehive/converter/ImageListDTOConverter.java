package com.github.gyrosofwar.imagehive.converter;

import com.github.gyrosofwar.imagehive.dto.image.ImageListDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import jakarta.inject.Singleton;

@Singleton
public class ImageListDTOConverter implements Converter<Image, ImageListDTO> {

  @Override
  public ImageListDTO convert(Image image) {
    return new ImageListDTO(
      image.id(),
      image.width(),
      image.height(),
      image.createdOn(),
      image.capturedOn(),
      ImageDetailsDTOConverter.getExtension(image.filePath()),
      image.favorite()
    );
  }
}
