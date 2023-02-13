package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.USER;

import com.github.gyrosofwar.imagehive.auth.BCryptPasswordEncoderService;
import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import org.apache.commons.lang3.RandomStringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UserService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private final DSLContext dsl;
  private final BCryptPasswordEncoderService encoderService;

  public UserService(DSLContext dsl, BCryptPasswordEncoderService encoderService) {
    this.dsl = dsl;
    this.encoderService = encoderService;
  }

  public int getUserCount() {
    return dsl.fetchCount(DSL.selectFrom(USER));
  }

  public void create(UserCreateDTO userCreate) {
    String hashedPassword;
    if (userCreate.isGeneratePassword()) {
      String random = RandomStringUtils.random(12);
      //TODO: this is only for development purposes, remove logging of password at some point and replace with mail notification
      log.info("Randomized password \"{}\" for user \"{}\"", random, userCreate.getUsername());
      hashedPassword = encoderService.encode(random);
    } else {
      hashedPassword = encoderService.encode(userCreate.getPassword());
    }
    dsl
      .insertInto(USER)
      .set(USER.USERNAME, userCreate.getUsername())
      .set(USER.EMAIL, userCreate.getEmail())
      .set(USER.PASSWORD_HASH, hashedPassword)
      .set(USER.ADMIN, userCreate.isAdmin())
      .set(USER.CREATED_ON, OffsetDateTime.now())
      .execute();
  }
}
