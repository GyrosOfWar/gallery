package com.github.gyrosofwar.imagehive.factory;

import com.github.gyrosofwar.imagehive.service.mail.EmailService;
import com.github.gyrosofwar.imagehive.service.mail.LoggingEmailService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import me.desair.tus.server.TusFileUploadService;
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

  @Bean
  public TusFileUploadService tusFileUploadService() {
    return new TusFileUploadService()
      .withStoragePath("temp-images")
      .withUploadURI("/api/images/upload");
  }
}
