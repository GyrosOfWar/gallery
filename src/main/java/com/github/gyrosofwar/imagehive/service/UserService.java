package com.github.gyrosofwar.imagehive.service;

import static com.github.gyrosofwar.imagehive.sql.Tables.USER;

import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import com.github.gyrosofwar.imagehive.service.mail.Email;
import com.github.gyrosofwar.imagehive.service.mail.EmailService;
import com.github.gyrosofwar.imagehive.sql.tables.pojos.User;
import com.github.gyrosofwar.imagehive.sql.tables.records.UserRecord;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import javax.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.jooq.DSLContext;
import org.jooq.DeleteUsingStep;
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
  private final EmailService emailService;

  public UserService(
    DSLContext dsl,
    PasswordEncoder passwordEncoder,
    EmailService emailService
  ) {
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
      throw new IllegalArgumentException(
        "User with the given information already exists"
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
    try (DeleteUsingStep<UserRecord> delete = dsl.delete(USER)) {
      return delete.where(USER.ID.eq(id)).execute();
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
    try (SelectWhereStep<?> selectFrom = dsl.selectFrom(USER)) {
      return selectFrom.where(USER.ID.eq(id)).fetchOneInto(User.class);
    }
  }

  @Transactional
  public User getByNameOrEmail(String identifier) {
    try (SelectWhereStep<?> selectFrom = dsl.selectFrom(USER)) {
      return selectFrom
        .where(USER.USERNAME.eq(identifier).or(USER.EMAIL.eq(identifier)))
        .fetchOneInto(User.class);
    }
  }
}
