package com.github.gyrosofwar.imagehive.dto.image;

public record LocationInfo(
  String country,
  String city,
  String district,
  String locality,
  String street
) {}
