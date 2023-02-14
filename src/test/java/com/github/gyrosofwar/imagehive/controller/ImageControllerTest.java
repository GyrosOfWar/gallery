package com.github.gyrosofwar.imagehive.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.gyrosofwar.imagehive.BaseTest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ImageControllerTest extends BaseTest {

  @Inject
  @Client("/")
  HttpClient httpClient;

  private MultipartBody getImages() throws IOException {
    var builder = MultipartBody.builder();
    for (var path : List.of("/images/test-1.jpg", "/images/test-2.jpg", "/images/test-3.jpg")) {
      var fileName = Path.of(path).getFileName().toString();
      var inputStream = getClass().getResourceAsStream(path);
      var bytes = inputStream.readAllBytes();
      builder.addPart(fileName, fileName, MediaType.IMAGE_JPEG_TYPE, bytes);
    }
    return builder.build();
  }

  @Test
  @Disabled
  void testCreateImage() throws IOException {
    var body = getImages();
    var token = appClient.login(new UsernamePasswordCredentials(username, password));
    var header = String.format("Bearer %s", token.getAccessToken());
    var response = appClient.uploadImages(body, header);
    assertEquals(200, response.code());
  }
}
