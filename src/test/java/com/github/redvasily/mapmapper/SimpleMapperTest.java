package com.github.redvasily.mapmapper;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class SimpleMapperTest {

  private MapMapper mapper() {
    MapMapper mapper = new MapMapper();
    mapper.registerModule(new IntegerModule());
    mapper.registerModule(new StringModule());
    return mapper;
  }

  @Test
  public void testGetFields() {
    List<ImmutableFieldInfo> fields = mapper().getFields(ImmutableSimpleRecord.class);
    Assertions.assertThat(fields)
        .extracting("name")
        .containsExactlyInAnyOrder("id", "name");
    Map<String, ImmutableFieldInfo> fieldsByName = fields.stream()
        .collect(Collectors.toMap(field -> field.name(), field -> field));
    assertThat(fieldsByName.get("id").typeInfo()).isInstanceOf(IntegerTypeInfo.class);
    assertThat(fieldsByName.get("name").typeInfo()).isInstanceOf(StringTypeInfo.class);
  }

  @Test
  public void testToMap() {
    ImmutableSimpleRecord record = ImmutableSimpleRecord.builder()
        .id(1)
        .name("SOME NAME")
        .build();
    Map<String, Object> map = mapper().toMap(record);
    assertThat(map).containsOnlyKeys("id", "name");
    assertThat(map.get("id")).isEqualTo("1");
    assertThat(map.get("name")).isEqualTo("SOME NAME");
  }

  @Test
  public void testFromMap() {
    Map<String, Object> map = new TreeMap<>();
    map.put("id", "1");
    map.put("name", "SOME NAME");
    ImmutableSimpleRecord isr = mapper().fromMap(map, ImmutableSimpleRecord.class);
    assertThat(isr)
        .isEqualTo(ImmutableSimpleRecord.builder()
            .id(1)
            .name("SOME NAME")
            .build());
  }

}
