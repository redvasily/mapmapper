package com.github.redvasily.mapmapper;

import java.util.Optional;

public class IntegerModule implements TypeInfoModule {
  @Override
  public Optional<TypeInfo> getTypeInfo(
      MapMapper mapper, Class containerClass, Class methodClass, Class backingFieldClass,
      String fieldName) {
    Class class_ = methodClass != null ? methodClass : backingFieldClass;
    if (class_ == Integer.class || class_.getTypeName().equals("int")) {
      return Optional.of(new IntegerTypeInfo());
    }
    return Optional.empty();
  }
}
