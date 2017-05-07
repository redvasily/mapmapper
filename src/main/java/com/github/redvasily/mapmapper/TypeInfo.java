package com.github.redvasily.mapmapper;

public interface TypeInfo {
  Object getSimpleValue(Object object);
  Object buildFrom(Object object);
}
