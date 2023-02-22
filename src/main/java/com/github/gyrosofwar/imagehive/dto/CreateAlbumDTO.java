package com.github.gyrosofwar.imagehive.dto;

import java.util.List;
import java.util.UUID;

public record CreateAlbumDTO(String name, long ownerId, List<String> tags, List<UUID> imageIds) {}
