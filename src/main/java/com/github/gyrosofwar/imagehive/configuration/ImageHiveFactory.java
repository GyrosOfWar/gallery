package com.github.gyrosofwar.imagehive.configuration;

import com.github.gyrosofwar.imagehive.service.mail.EmailService;
import com.github.gyrosofwar.imagehive.service.mail.LoggingEmailService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import org.apache.tika.config.TikaConfig;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Factory
public class ImageHiveFactory {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public TikaConfig tikaConfig() {
    return TikaConfig.getDefaultConfig();
  }

  @Bean
  public EmailService emailService() {
    return new LoggingEmailService();
  }
}
