package com.github.redvasily.mapmapper;

import java.util.Optional;

public class OptionalTypeInfo implements TypeInfo {

  private final TypeInfo fieldTypeInfo;

  public OptionalTypeInfo(MapMapper mapper, Class fieldClass) {
    Optional<TypeInfo> filedTypeInfo = mapper.getTypeInfo(
        null, null, fieldClass, null);
    this.fieldTypeInfo = filedTypeInfo.orElseThrow(RuntimeException::new);
  }

  @Override
  public Object getSimpleValue(Object object) {
    if (!(object instanceof Optional)) {
      throw new RuntimeException();
    }
    Object wrapped = ((Optional) object).orElse(null);
    if (wrapped == null)
      return null;
    return fieldTypeInfo.getSimpleValue(wrapped);
  }

  @Override
  public Object buildFrom(Object object) {
    return fieldTypeInfo.buildFrom(object);
  }
}
