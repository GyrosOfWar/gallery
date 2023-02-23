package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.IMAGE;

import com.github.gyrosofwar.imagehive.sql.tables.pojos.Image;
import com.github.gyrosofwar.imagehive.sql.tables.records.ImageRecord;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SearchService {

  private static final Logger log = LoggerFactory.getLogger(SearchService.class);
  private final DSLContext dsl;

  public SearchService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<Image> searchImages(String query, Pageable pageable, long userId) {
    try (SelectWhereStep<ImageRecord> selectFrom = dsl.selectFrom(IMAGE)) {
      return selectFrom
        .where("ts_vec @@ plainto_tsquery('english', {0})", DSL.inline(query))
        .and(IMAGE.OWNER_ID.eq(userId))
        .offset(pageable.getOffset())
        .limit(pageable.getSize())
        .fetchInto(Image.class);
    } catch (Exception e) {
      log.error("There was an error searching for images", e);
    }
    return List.of();
  }
}
