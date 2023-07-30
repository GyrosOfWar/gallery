package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.USER;

import com.github.gyrosofwar.imagehive.dto.user.UserCreateDTO;
import com.github.gyrosofwar.imagehive.service.mail.Email;
import com.github.gyrosofwar.imagehive.service.mail.EmailService;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.User;
import com.github.gyrosofwar.imagehive.sql.tables.records.UserRecord;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jooq.DSLContext;
import org.jooq.DeleteUsingStep;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

@Singleton
public class UserService {

  private static final Logger log = LoggerFactory.getLogger(UserService.class);
  private final DSLContext dsl;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  public UserService(DSLContext dsl, PasswordEncoder passwordEncoder, EmailService emailService) {
    this.dsl = dsl;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  @Transactional
  public int getUserCount() {
    return dsl.fetchCount(DSL.selectFrom(USER));
  }

  /**
   *
   * @param userCreate DTO containing all the necessary info for user creation
   * @return The id of the newly created user
   */
  @Transactional
  public long create(UserCreateDTO userCreate) {
    // First, we check if a user with this username or email already exists
    if (
      getByNameOrEmail(userCreate.username()) != null ||
      getByNameOrEmail(userCreate.email()) != null
    ) {
      throw new IllegalArgumentException("User with the given information already exists");
    }
    // Load the default settings for new users
    JSONB defaultSettings;
    try (InputStream is = getClass().getResourceAsStream("/default_usersettings.json")) {
      assert is != null;
      defaultSettings = JSONB.jsonb(IOUtils.toString(is, StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new RuntimeException(
        "There was an error reading the default_settings.json for user creation",
        e
      );
    }
    // Prepare the password hash either by generating a random password or using the given password
    String password;
    if (userCreate.generatePassword()) {
      password = RandomStringUtils.randomAlphanumeric(12);
    } else {
      password = userCreate.password();
    }
    String hashedPassword = passwordEncoder.encode(password);
    // Insert a new user into the database
    int result = dsl
      .insertInto(USER)
      .set(USER.USERNAME, userCreate.username())
      .set(USER.EMAIL, userCreate.email())
      .set(USER.PASSWORD_HASH, hashedPassword)
      .set(USER.ADMIN, userCreate.admin())
      .set(USER.CREATED_ON, OffsetDateTime.now())
      .set(USER.USER_SETTINGS, defaultSettings)
      .execute();
    if (result != 1) {
      throw new UnknownError("There was an unknown problem creating the user");
    }
    // Send mail with password information
    emailService.send(
      new Email(
        userCreate.email(),
        null,
        null,
        "ImageHive",
        null,
        "Password: " + password,
        "test@example.com"
      )
    );

    return getByNameOrEmail(userCreate.username()).id();
  }

  @Transactional
  public int deleteById(long id) {
    try {
      return dsl.delete(USER).where(USER.ID.eq(id)).execute();
    } catch (Exception e) {
      log.error("Could not delete user with id " + id, e);
      return 0;
    }
  }

  @Transactional
  public int deleteByNameOrEmail(String identifier) {
    User user = getByNameOrEmail(identifier);
    return deleteById(user.id());
  }

  @Transactional
  public User getById(long id) {
    return dsl.selectFrom(USER).where(USER.ID.eq(id)).fetchOneInto(User.class);
  }

  @Transactional
  public User getByNameOrEmail(String identifier) {
    return dsl
      .selectFrom(USER)
      .where(USER.USERNAME.eq(identifier).or(USER.EMAIL.eq(identifier)))
      .fetchOneInto(User.class);
  }

  // admin-level method
  @Transactional
  public Page<User> listUsers(Pageable pageable) {
    var users = dsl
      .selectFrom(USER)
      .orderBy(USER.CREATED_ON.desc())
      .limit(pageable.getSize())
      .offset(pageable.getOffset())
      .fetchInto(User.class);
    var count = dsl.selectCount().from(USER).fetchOne().value1();

    return Page.of(users, pageable, count);
  }
}
