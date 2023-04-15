package com.github.gyrosofwar.imagehive.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/api/batch")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class BatchActionController {

  @Post("/image/generate-descriptions")
  public void generateAllDescriptions() {}
}
