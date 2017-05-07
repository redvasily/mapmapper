package com.github.redvasily.mapmapper;

public class IntegerTypeInfo implements TypeInfo {
  @Override
  public Object getSimpleValue(Object input) {
    if (input instanceof Integer) {
      return input;
    }
    throw new IllegalArgumentException();
  }

  @Override
  public Object buildFrom(Object input) {
    if (input instanceof String) {
      return Integer.valueOf((String) input);
    } else if (input instanceof Integer) {
      return input;
    }
    throw new IllegalArgumentException();
  }
}
