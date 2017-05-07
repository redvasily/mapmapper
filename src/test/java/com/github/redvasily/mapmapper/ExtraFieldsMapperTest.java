package com.github.redvasily.mapmapper;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtraFieldsMapperTest {
  private MapMapper mapper() {
    MapMapper mapper = new MapMapper();
    mapper.registerModule(new IntegerModule());
    mapper.registerModule(new StringModule());
    mapper.registerModule(new WrapperModule());
    return mapper;
  }

  @Test
  public void testToMapSimple() {
    ImmutableRecordWithExtra record = ImmutableRecordWithExtra.builder()
        .id(1)
        .name("WHATEVER")
        .putExtra("foo", 1)
        .putExtra("bar", "baz")
        .build();
    Map<String, Object> map = mapper().toMap(record);
    assertThat(map).containsOnlyKeys("id", "name", "foo", "bar");
    assertThat(map.get("id")).isEqualTo("1");
    assertThat(map.get("name")).isEqualTo("WHATEVER");
    assertThat(map.get("foo")).isEqualTo(1);
    assertThat(map.get("bar")).isEqualTo("baz");
  }

  @Test
  public void testFromMapSimple() {
    Map<String, Object> map = new HashMap<>();
    map.put("id", 1);
    map.put("name", "WHATEVER");
    map.put("foo", 1);
    map.put("bar", "baz");

    ImmutableRecordWithExtra deserialized = mapper()
        .fromMap(map, ImmutableRecordWithExtra.class);

    ImmutableRecordWithExtra record = ImmutableRecordWithExtra.builder()
        .id(1)
        .name("WHATEVER")
        .putExtra("foo", 1)
        .putExtra("bar", "baz")
        .build();

    assertThat(deserialized).isEqualTo(record);
  }

}
