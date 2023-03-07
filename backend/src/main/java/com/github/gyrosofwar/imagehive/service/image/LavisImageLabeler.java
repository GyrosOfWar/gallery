package com.github.gyrosofwar.imagehive.service.image;

import io.micronaut.http.MediaType;
import io.micronaut.http.client.multipart.MultipartBody;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Singleton
public class LavisImageLabeler implements ImageLabeler {

  private final LavisServiceClient lavisServiceClient;

  public LavisImageLabeler(LavisServiceClient lavisServiceClient) {
    this.lavisServiceClient = lavisServiceClient;
  }

  @Override
  public Set<String> getTags(Path path) throws IOException {
    return Set.of();
  }

  @Override
  public String getDescription(Path path) throws IOException {
    try (var inputStream = Files.newInputStream(path)) {
      var contentLength = Files.size(path);
      var body = MultipartBody
        .builder()
        .addPart(
          "image",
          "image.jpeg",
          MediaType.APPLICATION_OCTET_STREAM_TYPE,
          inputStream,
          contentLength
        )
        .build();

      return lavisServiceClient.getCaption(body).caption();
    }
  }
}
