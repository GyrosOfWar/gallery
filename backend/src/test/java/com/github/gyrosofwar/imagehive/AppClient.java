package com.github.gyrosofwar.imagehive;

import com.github.gyrosofwar.imagehive.dto.admin.UserCreateDTO;
import com.github.gyrosofwar.imagehive.dto.image.ImageDetailsDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import java.util.UUID;

@Client("/")
public interface AppClient {
  @Post("/api/login")
  BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

  @Post("/api/admin/user/create")
  HttpResponse<Void> adminCreateUser(
    @Body UserCreateDTO userCreateDTO,
    @Header String authorization
  );

  @Put(value = "/api/images", produces = MediaType.MULTIPART_FORM_DATA)
  HttpResponse<ImageDetailsDTO> uploadImage(
    @Body MultipartBody files,
    @Header String authorization
  );

  @Get("/api/media/{uuid}")
  HttpResponse<byte[]> getImageBytes(
    @PathVariable UUID uuid,
    @QueryValue String extension,
    @Header String authorization
  );
}
