package com.github.redvasily.mapmapper;

import org.immutables.value.Value;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@Value.Immutable
public interface BuilderInfo {
  Class builderClass();
  Constructor constructor();
  Map<String, Field> fields();
  Method buildMethod();
  Field initBits();
}
