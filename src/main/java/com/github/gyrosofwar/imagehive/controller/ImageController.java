package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import java.util.UUID;
import javax.transaction.Transactional;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;

@Controller("/api/images")
public class ImageController {

  private final DSLContext dsl;

  public ImageController(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Get(produces = MediaType.APPLICATION_JSON, uri = "/{uuid}")
  @Transactional
  public Image getImage(@PathVariable UUID uuid) {
    try (SelectWhereStep<?> from = dsl.selectFrom(IMAGE)) {
      return from.where(IMAGE.ID.eq(uuid)).fetchOneInto(Image.class);
    }
  }
}
