package com.github.redvasily.mapmapper;

import java.util.Optional;

public class WrapperModule implements TypeInfoModule {
  @Override
  public Optional<TypeInfo> getTypeInfo(
      MapMapper mapper, Class containerClass, Class methodClass, Class backingFieldClass,
      String fieldName) {
    try {
      return Optional.of(new WrapperTypeInfo(mapper, backingFieldClass));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
