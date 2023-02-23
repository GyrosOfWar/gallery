package com.github.gyrosofwar.imagehive.controller;

import static com.github.gyrosofwar.imagehive.controller.ControllerHelper.getUserId;

import com.github.gyrosofwar.imagehive.dto.ImageDTO;
import com.github.gyrosofwar.imagehive.service.ImageService;
import com.github.gyrosofwar.imagehive.service.SearchService;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;
import javax.transaction.Transactional;

@Controller("/api/search")
@Secured({ SecurityRule.IS_AUTHENTICATED })
public class SearchController {

  private final SearchService searchService;
  private final ImageService imageService;

  public SearchController(SearchService searchService, ImageService imageService) {
    this.searchService = searchService;
    this.imageService = imageService;
  }

  @Get(produces = MediaType.APPLICATION_JSON, uri = "/{query}")
  @Transactional
  public List<ImageDTO> searchImages(
    @PathVariable String query,
    Pageable pageable,
    Authentication authentication
  ) {
    var userId = getUserId(authentication);
    if (userId == null) {
      return List.of();
    } else {
      return imageService.toDtoList(searchService.searchImages(query, pageable, userId));
    }
  }
}
