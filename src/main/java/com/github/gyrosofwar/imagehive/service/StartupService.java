package com.github.gyrosofwar.imagehive.service;

import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;

@Singleton
public class StartupService implements ApplicationEventListener<StartupEvent> {

  private static final Logger log = LoggerFactory.getLogger(StartupService.class);
  private final UserService userService;
  private final Environment environment;

  public StartupService(UserService userService, Environment environment) {
    this.userService = userService;
    this.environment = environment;
  }

  @Override
  @Transactional
  public void onApplicationEvent(StartupEvent event) {
    createAdminUserOnNewInstall();
  }

  private void createAdminUserOnNewInstall() {
    if (userService.getUserCount() == 0) {
      String random = RandomStringUtils.randomAlphanumeric(12);
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
