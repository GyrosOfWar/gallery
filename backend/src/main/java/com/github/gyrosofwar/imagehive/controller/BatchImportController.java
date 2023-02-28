package com.github.gyrosofwar.imagehive.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.authentication.Authentication;

@Controller("/api/batch-import")
public class BatchImportController {

  @Post("/google-takeout")
  public void importGoogleTakeout(Authentication authentication) {}
}
