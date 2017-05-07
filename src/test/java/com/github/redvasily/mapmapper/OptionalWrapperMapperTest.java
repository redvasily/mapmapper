package com.github.redvasily.mapmapper;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalWrapperMapperTest {
  private MapMapper mapper() {
    MapMapper mapper = new MapMapper();
    mapper.registerModule(new IntegerModule());
    mapper.registerModule(new StringModule());
    mapper.registerModule(new OptionalModule());
    mapper.registerModule(new WrapperModule());
    return mapper;
  }

  @Test
  public void testToMapPresent() {
    ImmutableRecordWithOptionalWrapper record = ImmutableRecordWithOptionalWrapper.builder()
        .id(new WrappedInt(1))
        .name("SOME NAME")
        .build();
    Map<String, Object> map = mapper().toMap(record);
    assertThat(map).containsOnlyKeys("id", "name");
    assertThat(map.get("id")).isEqualTo("1");
    assertThat(map.get("name")).isEqualTo("SOME NAME");
  }

  @Test
  public void testToMapAbsent() {
    ImmutableRecordWithOptionalWrapper record = ImmutableRecordWithOptionalWrapper.builder()
        .name("SOME NAME")
        .build();
    Map<String, Object> map = mapper().toMap(record);
    assertThat(map).containsOnlyKeys("name");
    assertThat(map.get("name")).isEqualTo("SOME NAME");
  }

  @Test
  public void testFromMapPresent() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", "1");
    map.put("name", "SOME NAME");
    ImmutableRecordWithOptionalWrapper deserialized = mapper()
        .fromMap(map, ImmutableRecordWithOptionalWrapper.class);
    ImmutableRecordWithOptionalWrapper expected = ImmutableRecordWithOptionalWrapper.builder()
        .id(new WrappedInt(1))
        .name("SOME NAME")
        .build();
    assertThat(deserialized).isEqualTo(expected);
  }

  @Test
  public void testFromMapAbsent() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "SOME NAME");
    ImmutableRecordWithOptionalWrapper deserialized = mapper()
        .fromMap(map, ImmutableRecordWithOptionalWrapper.class);
    ImmutableRecordWithOptionalWrapper expected = ImmutableRecordWithOptionalWrapper.builder()
        .name("SOME NAME")
        .build();
    assertThat(deserialized).isEqualTo(expected);
  }
}
