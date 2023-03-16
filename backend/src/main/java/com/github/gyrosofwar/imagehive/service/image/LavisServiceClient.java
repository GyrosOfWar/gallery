package com.github.gyrosofwar.imagehive.service.image;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;

@Requires("${imagehive.lavis.enabled}")
@Client("${imagehive.lavis.url:http://localhost:5000}")
public interface LavisServiceClient {
  @Post(value = "/caption", produces = MediaType.MULTIPART_FORM_DATA)
  Caption getCaption(@Body MultipartBody body);

  record Caption(String caption) {}
}
