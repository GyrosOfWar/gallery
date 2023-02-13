package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.USER;

import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.User;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.RandomStringUtils;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@Singleton
public class UserService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private final DSLContext dsl;
  private final PasswordEncoder passwordEncoder;

  public UserService(DSLContext dsl, PasswordEncoder passwordEncoder) {
    this.dsl = dsl;
    this.passwordEncoder = passwordEncoder;
  }

  public int getUserCount() {
    return dsl.fetchCount(DSL.selectFrom(USER));
  }

  public void create(UserCreateDTO userCreate) {
    String hashedPassword;
    if (userCreate.generatePassword()) {
      String random = RandomStringUtils.random(12);
      //TODO: this is only for development purposes, remove logging of password at some point and replace with mail notification
      log.info("Randomized password \"{}\" for user \"{}\"", random, userCreate.username());
      hashedPassword = passwordEncoder.encode(random);
    } else {
      hashedPassword = passwordEncoder.encode(userCreate.password());
    }
    dsl
      .insertInto(USER)
      .set(USER.USERNAME, userCreate.username())
      .set(USER.EMAIL, userCreate.email())
      .set(USER.PASSWORD_HASH, hashedPassword)
      .set(USER.ADMIN, userCreate.admin())
      .set(USER.CREATED_ON, OffsetDateTime.now())
      .execute();
  }

  public User getByNameOrEmail(String query) {
    try (SelectWhereStep<?> selectFrom = dsl.selectFrom(USER)) {
      return selectFrom
        .where(USER.USERNAME.eq(query).or(USER.EMAIL.eq(query)))
        .fetchOneInto(User.class);
    }
  }
}
