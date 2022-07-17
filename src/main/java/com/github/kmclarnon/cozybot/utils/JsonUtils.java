package com.github.kmclarnon.cozybot.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.nio.file.Path;

public class JsonUtils {
  public static final ObjectMapper MAPPER = createMapper();

  public static String toJson(Object object) {
    try {
      return MAPPER.writeValueAsString(object);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T fromJson(Path path, Class<T> type) {
    try {
      return MAPPER.readValue(path.toFile(), type);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static ObjectMapper createMapper() {
    ObjectMapper mapper = new ObjectMapper();

    mapper.registerModule(new Jdk8Module());

    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, false);
    mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return mapper;
  }
}
