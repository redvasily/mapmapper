package com.github.redvasily.mapmapper;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WrapperMapperTest {
  private MapMapper mapper() {
    MapMapper mapper = new MapMapper();
    mapper.registerModule(new IntegerModule());
    mapper.registerModule(new StringModule());
    mapper.registerModule(new WrapperModule());
    return mapper;
  }

  @Test
  public void testToMap() {
    ImmutableRecordWithWrapped record = ImmutableRecordWithWrapped.builder()
        .id(new WrappedInt(1))
        .name("SOME NAME")
        .build();
    Map<String, Object> map = mapper().toMap(record);
    assertThat(map).containsOnlyKeys("id", "name");
    assertThat(map.get("id")).isEqualTo("1");
    assertThat(map.get("name")).isEqualTo("SOME NAME");
  }

  @Test
  public void testFromMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", "1");
    map.put("name", "SOME NAME");
    ImmutableRecordWithWrapped deserialized = mapper()
        .fromMap(map, ImmutableRecordWithWrapped.class);
    ImmutableRecordWithWrapped expected = ImmutableRecordWithWrapped.builder()
        .id(new WrappedInt(1))
        .name("SOME NAME")
        .build();
    WrappedInt id = deserialized.id();
    assertThat(deserialized).isEqualTo(expected);
  }
}
