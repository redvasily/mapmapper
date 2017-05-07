package com.github.redvasily.mapmapper;

public class ObjectTypeInfo implements TypeInfo {
  @Override
  public Object getSimpleValue(Object object) {
    return object;
  }

  @Override
  public Object buildFrom(Object object) {
    return object;
  }
}
