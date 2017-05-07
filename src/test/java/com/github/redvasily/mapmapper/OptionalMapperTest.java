package com.github.redvasily.mapmapper;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalMapperTest {
  private MapMapper mapper() {
    MapMapper mapper = new MapMapper();
    mapper.registerModule(new IntegerModule());
    mapper.registerModule(new StringModule());
    mapper.registerModule(new OptionalModule());
    return mapper;
  }

  @Test
  public void testToMapPresent() {
    ImmutableRecordWithOptional record = ImmutableRecordWithOptional.builder()
        .id(1)
        .name("whatever")
        .build();
    Map<String, Object> map = mapper().toMap(record);
    assertThat(map).containsOnlyKeys("id", "name");
    assertThat(map.get("id")).isEqualTo("1");
    assertThat(map.get("name")).isEqualTo("whatever");
  }

  @Test
  public void testToMapAbsent() {
    ImmutableRecordWithOptional record = ImmutableRecordWithOptional.builder()
        .id(1)
        .build();
    Map<String, Object> map = mapper().toMap(record);
    assertThat(map).containsOnlyKeys("id");
    assertThat(map.get("id")).isEqualTo("1");
  }

  @Test
  public void testFromMapPresent() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", "1");
    map.put("name", "whatever");
    ImmutableRecordWithOptional deserialized = mapper()
        .fromMap(map, ImmutableRecordWithOptional.class);
    ImmutableRecordWithOptional expected = ImmutableRecordWithOptional.builder()
        .id(1)
        .name("whatever")
        .build();
    assertThat(deserialized).isEqualTo(expected);
  }

  @Test
  public void testFromMapAbsent() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", "1");
    ImmutableRecordWithOptional deserialized = mapper()
        .fromMap(map, ImmutableRecordWithOptional.class);
    ImmutableRecordWithOptional expected = ImmutableRecordWithOptional.builder()
        .id(1)
        .build();
    assertThat(deserialized).isEqualTo(expected);
  }
}
