package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import jakarta.inject.Singleton;
import java.util.UUID;
import org.jooq.DSLContext;

@Singleton
public class ImageService {

  private final DSLContext dsl;

  public ImageService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public Image getByUuid(UUID uuid) {
    try (var from = dsl.selectFrom(IMAGE)) {
      return from.where(IMAGE.ID.eq(uuid)).fetchOneInto(Image.class);
    }
  }

  public ImageDTO toDto(Image image) {
    return new ImageDTO(image.height(), image.width(), image.createdOn(), image.tags());
  }
}
