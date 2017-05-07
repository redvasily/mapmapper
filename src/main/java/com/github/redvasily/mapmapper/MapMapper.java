package com.github.redvasily.mapmapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class MapMapper {

  private final List<TypeInfoModule> modules = new ArrayList<>();

  private static boolean isFieldMethod(Class class_, Method method) {
    String name = method.getName();
    try {
      Field backingField = class_.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      return false;
    }
    return method.getParameterCount() == 0;
  }

  public void registerModule(TypeInfoModule module) {
    modules.add(module);
  }

  private Optional<ImmutableFieldInfo> getExtraField(Class class_) {
    Method[] methods = class_.getMethods();
    List<Method> fieldMethods = Arrays.stream(methods)
        .filter(method -> isFieldMethod(class_, method))
        .collect(toList());
    List<Method> mapMethods = fieldMethods.stream()
        .filter(method -> Map.class.isAssignableFrom(method.getReturnType()))
        .collect(toList());
    if (mapMethods.size() == 0) {
      return Optional.empty();
    }
    if (mapMethods.size() > 1) {
      throw new RuntimeException("More than one candidate for an 'extra' field");
    }
    Method method = mapMethods.get(0);
    String fieldName = method.getName();
    Field field;
    try {
      field = class_.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    return Optional.of(ImmutableFieldInfo.builder()
        .name(fieldName)
        .field(field)
        .method(method)
        .typeInfo(new ObjectTypeInfo())
        .build());
  }

  List<ImmutableFieldInfo> getFields(Class class_) {
    Method[] methods = class_.getMethods();
    List<Method> fieldMethods = Arrays.stream(methods)
        .filter(method -> isFieldMethod(class_, method))
        .collect(toList());
    Map<String, Field> fields = Arrays.stream(class_.getDeclaredFields())
        .collect(Collectors.toMap(field -> field.getName(), field -> field));
    return fieldMethods.stream()
        .map(method -> {
          String fieldName = method.getName();
          Field field;
          try {
            field = class_.getDeclaredField(fieldName);
          } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
          }
          Class methodClass = method.getReturnType();
          Class backingFieldClass = field.getType();
          return getTypeInfo(class_, methodClass, backingFieldClass, method.getName())
              .map(typeInfo ->
                  ImmutableFieldInfo.builder()
                      .name(method.getName())
                      .field(fields.get(method.getName()))
                      .typeInfo(typeInfo)
                      .method(method)
                      .build());
        })
        .filter(o -> o.isPresent())
        .map(o -> o.get())
        .collect(toList());
  }

  ImmutableBuilderInfo getBuilderInfo(Class class_) {
    Class builderClass = Arrays.stream(class_.getDeclaredClasses())
        .filter(innerClass -> innerClass.getSimpleName().equals("Builder"))
        .findFirst()
        .get();

    Map<String, Field> fields = Arrays.stream(builderClass.getDeclaredFields())
        .peek(field -> field.setAccessible(true))
        .collect(Collectors.toMap(
            field -> field.getName(),
            field -> field));

    Method buildMethod;
    Constructor constructor;
    Field initBits;
    try {
      buildMethod = builderClass.getMethod("build");
      constructor = builderClass.getDeclaredConstructor();
      initBits = builderClass.getDeclaredField("initBits");
    } catch (NoSuchMethodException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }

    constructor.setAccessible(true);
    initBits.setAccessible(true);
    return ImmutableBuilderInfo.builder()
        .builderClass(builderClass)
        .fields(fields)
        .buildMethod(buildMethod)
        .constructor(constructor)
        .initBits(initBits)
        .build();

//        return ImmutableBuilderInfo.builder()
//                .constructor();
  }

  public Optional<TypeInfo> getTypeInfo(Class containerClass, Class methodClass,
                                        Class backingFieldClass, String fieldName) {
    return modules.stream()
        .flatMap(module -> {
//                    Method method;
//                    try {
//                        method = containerClass.getDeclaredMethod(fieldName);
//                    } catch (NoSuchMethodException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Class methodClass = method.getReturnType();
///                    Field field;
//                    try {
//                        field = containerClass.getDeclaredField(fieldName);
//                    } catch (NoSuchFieldException e) {
//                        throw new RuntimeException(e);
//                    }
//                    Class fieldClass = field.getType();
          return module.getTypeInfo(this, containerClass, methodClass,
              backingFieldClass, fieldName)
              .map(Stream::of).orElse(Stream.empty());
        })
        .findFirst();
  }

  public Map<String, Object> toMap(Object object) {
    List<ImmutableFieldInfo> fields = getFields(object.getClass());
    Map<String, Object> map = new HashMap<>();
    for (ImmutableFieldInfo field : fields) {
      Object value;
      try {
        value = field.typeInfo().getSimpleValue(
            field.method().invoke(object));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException();
      }
      if (value != null) {
        map.put(field.name(), value.toString());
      }
    }
    Optional<ImmutableFieldInfo> extraField = getExtraField(object.getClass());
    extraField.ifPresent(ef -> {
      Map extra;
      try {
        extra = (Map) ef.method().invoke(object);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
      map.putAll(extra);
    });
    return map;
  }

  public <T> T fromMap(Map<String, Object> map, Class<T> class_) {
    List<ImmutableFieldInfo> fields = getFields(class_);
    Map<String, ImmutableFieldInfo> fieldsMap = fields.stream()
        .collect(Collectors.toMap(
            field -> field.name(),
            field -> field));
    Optional<ImmutableFieldInfo> extraField = getExtraField(class_);
    int nbFields = fields.size() + extraField.map(f -> 1).orElse(0);
    List<Constructor<?>> constructors = Arrays.asList(class_.getDeclaredConstructors());
    Constructor<T> constructor = (Constructor<T>) Arrays.stream(class_.getDeclaredConstructors())
        .filter(cons -> cons.getParameterCount() == nbFields)
        .findFirst()
        .get();

    constructor.setAccessible(true);

    getBuilderInfo(class_);

    ImmutableBuilderInfo builderInfo = getBuilderInfo(class_);

    Object builder;
    try {
      builder = builderInfo.constructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    Map<String, Object> extra = new HashMap<>();
    map.forEach((key, value) -> {
      ImmutableFieldInfo fieldInfo = fieldsMap.get(key);
      if (fieldInfo == null) {
        extra.put(key, value);
      } else {
        Object fieldValue = fieldInfo.typeInfo().buildFrom(value);
        try {
          builderInfo.fields().get(fieldInfo.name()).set(builder, fieldValue);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    });
    extraField.ifPresent(field -> {
      try {
        builderInfo.fields().get(field.name()).set(builder, extra);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });

    Object object;
    try {
      builderInfo.initBits().set(builder, 0);
      object = builderInfo.buildMethod().invoke(builder);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

    return (T) object;
  }
}