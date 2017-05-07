package com.github.redvasily.mapmapper;

import org.immutables.value.Value;

@Value.Immutable
public interface RecordWithWrapped {
  WrappedInt id();

  String name();
}
