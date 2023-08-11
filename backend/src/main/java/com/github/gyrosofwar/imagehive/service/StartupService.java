package com.github.gyrosofwar.imagehive.service;

import com.github.gyrosofwar.imagehive.configuration.ImageHiveConfiguration;
import com.github.gyrosofwar.imagehive.dto.admin.UserCreateDTO;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(notEnv = "test")
public class StartupService implements ApplicationEventListener<StartupEvent> {

  private static final Logger log = LoggerFactory.getLogger(StartupService.class);
  private final UserService userService;
  private final ImageHiveConfiguration configuration;

  public StartupService(UserService userService, ImageHiveConfiguration configuration) {
    this.userService = userService;
    this.configuration = configuration;
  }

  @Override
  public void onApplicationEvent(StartupEvent event) {
    try {
      createAdminUserOnNewInstall();
      createDirectories();
    } catch (Exception e) {
      log.error("failed running startup tasks:", e);
    }
  }

  private void createDirectories() throws IOException {
    Files.createDirectories(Path.of(configuration.imageBasePath()));
  }

  private void createAdminUserOnNewInstall() throws IOException {
    if (userService.getUserCount() == 0) {
      String random = RandomStringUtils.randomAlphanumeric(12);
      Files.writeString(Path.of("password.txt"), random);
      UserCreateDTO newAdmin = new UserCreateDTO("admin", "", random, true, false);
      userService.create(newAdmin);
      log.info("User \"admin\" with password \"{}\" has been created for an initial login", random);
    }
  }

  @Override
  public boolean supports(StartupEvent event) {
    return ApplicationEventListener.super.supports(event);
  }
}
