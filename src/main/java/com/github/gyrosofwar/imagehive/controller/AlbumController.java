package com.github.gyrosofwar.imagehive.controller;

import com.github.gyrosofwar.imagehive.dto.AlbumDTO;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;

@Controller
public class AlbumController {

  @Get
  public List<AlbumDTO> listAlbums() {
    throw new NotImplementedException();
  }
}
