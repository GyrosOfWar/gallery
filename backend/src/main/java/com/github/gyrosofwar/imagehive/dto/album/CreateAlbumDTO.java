package com.github.gyrosofwar.imagehive.dto.album;

import java.util.List;
import java.util.UUID;

public record CreateAlbumDTO(String name, String description, List<String> tags) {}
