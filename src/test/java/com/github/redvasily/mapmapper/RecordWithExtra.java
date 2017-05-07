package com.github.redvasily.mapmapper;

import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface RecordWithExtra {
  int id();

  String name();

  Map<String, Object> extra();
}
