package com.github.redvasily.mapmapper;

public class StringTypeInfo implements TypeInfo {
  @Override
  public Object getSimpleValue(Object input) {
    if (!(input instanceof String)) {
      throw new IllegalArgumentException();
    }
    return input;
  }

  @Override
  public Object buildFrom(Object input) {
    if (!(input instanceof String)) {
      throw new IllegalArgumentException();
    }
    return input;
  }
}
