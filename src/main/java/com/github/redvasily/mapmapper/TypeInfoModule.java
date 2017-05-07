package com.github.redvasily.mapmapper;

import java.util.Optional;


public interface TypeInfoModule {
  /**
   * @param mapper            the mapper itself
   * @param recordClass       a record class that's being checked out
   * @param methodClass       a return class of the method returning this "record field"
   * @param backingFieldClass a class of the backing field
   * @param fieldName         a field name, just in case, probably not even necessary
   * @return
   */
  Optional<TypeInfo> getTypeInfo(
      MapMapper mapper,
      Class recordClass,
      Class methodClass,
      Class backingFieldClass,
      String fieldName);
}
