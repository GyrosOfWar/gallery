package com.github.gyrosofwar.imagehive.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ImageDTO(int height, int width, OffsetDateTime createdOn, List<String> tags) {}
