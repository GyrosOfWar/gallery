package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.gyrosofwar.imagehive.dto.AlbumDTO;
import com.github.gyrosofwar.imagehive.service.AlbumService;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;

@Controller("/api/albums")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class AlbumController {

  private static final int IMAGES_PER_ALBUM_PREVIEW = 3;

  private final AlbumService albumService;

  public AlbumController(AlbumService albumService) {
    this.albumService = albumService;
  }

  @Get
  public List<AlbumDTO> listAlbums(Pageable pageable, Authentication authentication) {
    var userId = getUserId(authentication);
    return albumService.listAlbums(pageable, IMAGES_PER_ALBUM_PREVIEW, userId);
  }

  @Get("{id}")
  public AlbumDTO getAlbum(@PathVariable long id, Authentication authentication) {
    var userId = getUserId(authentication);

    return albumService.getAlbumWithImages(id, userId);
  }
}
