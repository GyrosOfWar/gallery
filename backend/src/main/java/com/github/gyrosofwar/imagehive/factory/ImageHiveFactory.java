package com.github.gyrosofwar.imagehive.factory;

import com.github.gyrosofwar.imagehive.service.mail.EmailService;
import com.github.gyrosofwar.imagehive.service.mail.LoggingEmailService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import me.desair.tus.server.TusFileUploadService;
import org.apache.tika.config.TikaConfig;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Factory
public class ImageHiveFactory {

  public static final String IMAGE_UPLOAD_SERVICE = "tusImageUploadService";
  public static final String ZIP_UPLOAD_SERVICE = "tusBatchUploadService";

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
  @Named(IMAGE_UPLOAD_SERVICE)
  public TusFileUploadService tusImageUploadService() {
    return new TusFileUploadService()
      .withStoragePath("temp-images")
      .withUploadURI("/api/images/upload");
  }

  @Bean
  @Named(ZIP_UPLOAD_SERVICE)
  public TusFileUploadService tusBatchUploadService() {
    return new TusFileUploadService()
      .withStoragePath("temp-images")
      .withUploadURI("/api/batch-import");
  }
}
