package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.tables.User.USER;

import com.github.t9t.jooq.json.JsonbDSL;
import jakarta.inject.Singleton;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UserSettingsService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private final DSLContext dsl;

  public UserSettingsService(DSLContext dsl) {
    this.dsl = dsl;
  }

  public boolean isDisplayFavoritesAlbum(Long id) {
    return Boolean.TRUE.equals(
      dsl
        .select(JsonbDSL.extractPathText(USER.USER_SETTINGS, "display_favorites_album"))
        .from(USER)
        .fetchOneInto(Boolean.class)
    );
  }
}
