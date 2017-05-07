package com.github.redvasily.mapmapper;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface RecordWithOptional {
  int id();

  Optional<String> name();
}
