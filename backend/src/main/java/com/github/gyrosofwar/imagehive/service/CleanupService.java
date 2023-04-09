package com.github.gyrosofwar.imagehive.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import me.desair.tus.server.TusFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Requires(notEnv = "test")
public class CleanupService {

  private static final Logger log = LoggerFactory.getLogger(CleanupService.class);

  private final List<TusFileUploadService> fileUploadServices;

  public CleanupService(List<TusFileUploadService> fileUploadServices) {
    this.fileUploadServices = fileUploadServices;
  }

  @Scheduled(fixedRate = "10m")
  public void cleanupUploadedFiles() throws IOException {
    log.info("Cleaning up temporary image uploads");
    try {
      for (var service : fileUploadServices) {
        service.cleanup();
      }
    } catch (NoSuchFileException e) {
      // This happens if no files have been uploaded yet, but log it in debug so it won't get lost
      log.debug("There was an error cleaning up temporary image uploads", e);
    }
  }
}
