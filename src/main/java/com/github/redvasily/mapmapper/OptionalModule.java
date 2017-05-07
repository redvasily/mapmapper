package com.github.redvasily.mapmapper;

import java.util.Optional;

public class OptionalModule implements TypeInfoModule {
  @Override
  public Optional<TypeInfo> getTypeInfo(MapMapper mapper, Class recordClass, Class methodClass,
                                        Class backingFieldClass, String fieldName) {

    if (methodClass != null && methodClass.equals(Optional.class)) {
      return Optional.of(new OptionalTypeInfo(mapper, backingFieldClass));
    }

    return Optional.empty();
  }
}
