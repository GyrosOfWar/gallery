package com.github.gyrosofwar.imagehive.dto;

import java.time.OffsetDateTime;

public record ImageDTO(int height, int width, OffsetDateTime createdOn, String[] tags) {}
