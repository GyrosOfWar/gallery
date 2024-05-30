package com.github.gyrosofwar.imagehive;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Imagehive", version = "0.1.0"))
public class ImageHiveApplication {

  public static void main(String[] args) {
    Micronaut.build(args).banner(false).start();
  }
}
