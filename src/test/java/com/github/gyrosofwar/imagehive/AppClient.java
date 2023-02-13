package com.github.gyrosofwar.imagehive;

import com.github.gyrosofwar.imagehive.dto.UserCreateDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

@Client("/")
public interface AppClient {
  @Post("/login")
  BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

  @Post("/api/admin/user/create")
  HttpResponse<Void> adminCreateUser(
    @Body UserCreateDTO userCreateDTO,
    @Header String authorization
  );
}
