package com.github.gyrosofwar.imagehive.controller;

import com.github.gyrosofwar.imagehive.sql.Tables;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import org.jooq.DSLContext;

import javax.transaction.Transactional;
import java.util.UUID;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

@Controller("/api/images")
public class ImageController {
  private final DSLContext dsl;

  public ImageController(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Get(produces = MediaType.APPLICATION_JSON, uri = "/{uuid}")
  @Transactional
  public Image getImage(@PathVariable UUID uuid) {
    return dsl.selectFrom(IMAGE)
      .where(IMAGE.ID.eq(uuid))
      .fetchOneInto(Image.class);
  }
}
