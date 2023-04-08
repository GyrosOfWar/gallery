package com.github.gyrosofwar.imagehive.service.geo;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import mil.nga.sf.geojson.FeatureCollection;

@Singleton
@Client("https://photon.komoot.io")
public interface PhotonApiClient {
  @Get("/reverse")
  FeatureCollection reverseGeoCode(
    @QueryValue("lat") double latitude,
    @QueryValue("lon") double longitude
  );
}
