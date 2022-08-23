package com.bfa.kafka.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

@Slf4j
public final class ConfigDefUtils {
  public static ConfigDef generate(Class<?> clazz) {
    ConfigDef def = new ConfigDef();
    for (Field field : clazz.getDeclaredFields()) {
      String fieldName = field.getName();
      Type type = getTypeForField(field);
      boolean required = false;
      Importance importance = Importance.LOW;
      String document = "";
      if (field.isAnnotationPresent(Config.class)) {
        Config c = field.getAnnotation(Config.class);
        document = c.document();
        required = c.required();
        if (c.name().length() > 0)
          fieldName = c.name();
        importance = c.importance();
      }
      if (!required && !field.isAccessible()) field.setAccessible(true);
      def.define(fieldName,
          type,
          required? ConfigDef.NO_DEFAULT_VALUE : null, // field.get(config),
          importance, document);
    }
    return def;
  }

  public static <T> T assignProps(Map<String,String> props, Class<T> obj) {
    ObjectMapper mapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
        .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .registerModule(new JavaTimeModule());
    log.info("ppppppp");
    return mapper.convertValue(props,obj);
  }
  private static Type getTypeForField(Field field) {
    Class<?> clazz = field.getType();
    if (int.class.equals(clazz) || Integer.class.equals(clazz)) {
      return Type.INT;
    }
    if (long.class.equals(clazz) || Long.class.equals(clazz)) return Type.LONG;

    if (float.class.equals(clazz) || Float.class.equals(clazz)
      || double.class.equals(clazz) || Double.class.equals(clazz)
    ) {
      return Type.DOUBLE;
    }
    if (String.class.equals(clazz)) return Type.STRING;
    if (clazz.isArray() || clazz.isAssignableFrom(Collection.class)) return Type.LIST;
    if (boolean.class.equals(clazz) || Boolean.class.equals(clazz)) return Type.BOOLEAN;
    throw new RuntimeException(String.format("not support this type %s", clazz.getName()));
  }

  class Obj {
    private int anInt;
    protected int anotherInt;
    public int thirdInt;
    private int[] ints;
    protected List<Integer> integerList;
  }
  public static void main(String[] argv) throws Exception {
    Arrays.stream(Obj.class.getDeclaredFields()).forEach((field -> {
      System.out.println(field.getType().isArray());
    }));
  }
}
