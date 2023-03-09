package com.github.gyrosofwar.imagehive.dto.album;

import java.util.List;

public record CreateAlbumDTO(String name, String description, List<String> tags) {}
