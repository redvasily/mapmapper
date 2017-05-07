package com.github.redvasily.mapmapper;

import org.immutables.value.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Value.Immutable
public interface FieldInfo {
  String name();
  Method method();
  Field field();
  TypeInfo typeInfo();
}
