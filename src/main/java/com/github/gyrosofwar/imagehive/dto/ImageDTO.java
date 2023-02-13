package com.github.gyrosofwar.imagehive.dto;

import java.time.OffsetDateTime;

public class ImageDTO {
  public ImageDTO(int height, int width, OffsetDateTime createdOn, String[] tags) {
    this.height = height;
    this.width = width;
    this.createdOn = createdOn;
    this.tags = tags;
  }

  private int height;
  private int width;
  OffsetDateTime createdOn;
  String[]       tags;

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }



  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public OffsetDateTime getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(OffsetDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String[] getTags() {
    return tags;
  }

  public void setTags(String[] tags) {
    this.tags = tags;
  }
}
