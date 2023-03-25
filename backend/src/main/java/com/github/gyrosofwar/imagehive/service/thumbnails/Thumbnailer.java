package com.github.gyrosofwar.imagehive.service.thumbnails;

import com.github.gyrosofwar.imagehive.service.ImageData;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface Thumbnailer {
  ImageData getThumbnail(Request request) throws IOException;

  record Request(
    Path imagePath,
    int width,
    int height,
    @Nullable FileType fileType,
    @Nullable Boolean crop,
    @Nullable Integer dpr,
    HttpHeaders headers
  ) {}

  enum FileType {
    WEBP,
    JPEG,
    AVIF,
  }
}
