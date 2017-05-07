package com.github.redvasily.mapmapper;

import java.util.Optional;

public class StringModule implements TypeInfoModule {
  @Override
  public Optional<TypeInfo> getTypeInfo(
      MapMapper mapper, Class containerClass, Class methodClass, Class backingFieldClass,
      String fieldName) {
    Class class_ = methodClass != null ? methodClass : backingFieldClass;
    if (class_ == String.class) {
      return Optional.of(new StringTypeInfo());
    }
    return Optional.empty();
  }
}
