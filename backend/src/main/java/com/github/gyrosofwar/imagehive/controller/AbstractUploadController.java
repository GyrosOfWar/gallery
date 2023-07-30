package com.github.gyrosofwar.imagehive.controller;

import io.micronaut.security.authentication.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUploadController {

  private static final Logger log = LoggerFactory.getLogger(UploadController.class);

  protected abstract void handleUploadedFile(
    InputStream inputStream,
    UploadInfo uploadInfo,
    Authentication authentication
  ) throws Exception;

  protected abstract TusFileUploadService uploadService();

  protected final void handleTusUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, TusException {
    var fileUploadService = uploadService();
    fileUploadService.process(request, response);

    var uploadUri = request.getRequestURI();
    try {
      var uploadInfo = fileUploadService.getUploadInfo(uploadUri);
      if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
        var inputStream = fileUploadService.getUploadedBytes(uploadUri);
        handleUploadedFile(inputStream, uploadInfo, authentication);
      }
    } catch (Exception e) {
      log.error("encountered upload error", e);
    } finally {
      fileUploadService.deleteUpload(uploadUri);
    }
  }
}
