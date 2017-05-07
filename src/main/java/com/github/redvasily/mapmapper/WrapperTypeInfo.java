package com.github.redvasily.mapmapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class WrapperTypeInfo implements TypeInfo {

  private final static HashSet<String> STANDARD_METHODS = new HashSet<>(
      Arrays.asList("toString", "hashCode"));
  private final Class wrapperClass;
  private final TypeInfo wrappedTypeInfo;
  private final Method accessorMethod;
  private final Constructor constructor;

  public WrapperTypeInfo(MapMapper mapper, Class wrapperClass) {
    this.wrapperClass = wrapperClass;
//        this.wrappedTypeInfo = wrappedTypeInfo;
    accessorMethod = findAccessorMethod(wrapperClass)
        .orElseThrow(RuntimeException::new);
    List<Constructor> constructors = Arrays.asList(wrapperClass.getDeclaredConstructors());
    List<Constructor> singleArgConstructors = constructors.stream()
        .filter(c -> c.getParameterCount() == 1)
        .collect(toList());
    this.constructor = singleArgConstructors.get(0);

    Class wrappedClass = constructor.getParameterTypes()[0];
    wrappedTypeInfo = mapper.getTypeInfo(
        null, wrappedClass, wrappedClass, null)
        .orElseThrow(RuntimeException::new);

  }

  private Optional<Method> findAccessorMethod(Class wrapperClass) {
    if (wrapperClass != null && wrapperClass.equals(Object.class)) {
      return Optional.empty();
    }
    List<Method> methods = Arrays.asList(wrapperClass.getDeclaredMethods());
    List<Method> noArgMethods = methods.stream()
        .filter(method -> method.getParameterCount() == 0
            && !STANDARD_METHODS.contains(method.getName()))
        .collect(toList());
    if (noArgMethods.size() == 1) {
      return Optional.of(noArgMethods.get(0));
    }
    if (wrapperClass.getSuperclass() != null) {
      return findAccessorMethod(wrapperClass.getSuperclass());
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Object getSimpleValue(Object object) {
    try {
      return wrappedTypeInfo.getSimpleValue(accessorMethod.invoke(object));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object buildFrom(Object object) {
    Object wrapped = wrappedTypeInfo.buildFrom(object);
    try {
      return constructor.newInstance(wrapped);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
