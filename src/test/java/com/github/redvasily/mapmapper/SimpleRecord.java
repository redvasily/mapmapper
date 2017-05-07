package com.github.redvasily.mapmapper;


import org.immutables.value.Value;

@Value.Immutable
public interface SimpleRecord {
  int id();

  String name();
}
