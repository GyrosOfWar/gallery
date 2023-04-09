package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.drew.imaging.ImageProcessingException;
import com.github.gyrosofwar.imagehive.service.image.NewImage;
import io.micronaut.security.authentication.Authentication;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUploadController {

  private static final Logger log = LoggerFactory.getLogger(UploadController.class);

  private final TusFileUploadService fileUploadService;

  public AbstractUploadController(TusFileUploadService fileUploadService) {
    this.fileUploadService = fileUploadService;
  }

  protected abstract void handleUploadedFile(
    InputStream inputStream,
    UploadInfo uploadInfo,
    Authentication authentication
  ) throws Exception;

  protected final void handleTusUpload(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, TusException {
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
