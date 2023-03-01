package com.github.gyrosofwar.imagehive.converter;

public interface Converter<I, O> {
  O convert(I input);
}
