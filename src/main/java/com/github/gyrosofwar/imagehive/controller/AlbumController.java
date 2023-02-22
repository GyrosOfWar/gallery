package com.github.gyrosofwar.imagehive.controller;

import com.github.gyrosofwar.imagehive.dto.AlbumDTO;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

@Controller
public class AlbumController {
  @Get
  public List<AlbumDTO> listAlbums() {
    throw new NotImplementedException();
  }
}
