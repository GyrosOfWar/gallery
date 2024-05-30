package com.github.gyrosofwar.imagehive.service.image;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;

@Requires(property = "imagehive.ai.enabled")
@Client("${imagehive.ai.url:http://localhost:5000}")
public interface AiServiceClient {
  @Post(value = "/generate/caption", produces = MediaType.MULTIPART_FORM_DATA)
  CaptionsResponse getCaption(@Body MultipartBody body);

  @Post(value = "/generate/tags", produces = MediaType.MULTIPART_FORM_DATA)
  TagsResponse getTags(@Body MultipartBody body);
}
