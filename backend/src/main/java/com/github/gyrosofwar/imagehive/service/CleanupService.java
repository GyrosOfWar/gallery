package com.github.gyrosofwar.imagehive.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.io.IOException;
import me.desair.tus.server.TusFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(notEnv = "test")
public class CleanupService {

  private static final Logger log = LoggerFactory.getLogger(CleanupService.class);

  private final TusFileUploadService fileUploadService;

  public CleanupService(TusFileUploadService fileUploadService) {
    this.fileUploadService = fileUploadService;
  }

  @Scheduled(fixedRate = "10m")
  public void cleanupUploadedFiles() throws IOException {
    log.info("cleaning up uploaded files");
    fileUploadService.cleanup();
  }
}
