package com.github.kmclarnon.cozybot.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Suppliers;
import com.hubspot.immutables.style.HubSpotStyle;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HubSpotStyle
@JsonIgnoreProperties
@Value.Immutable
public abstract class AbstractBotConfig {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractBotConfig.class);
  // Configure mapper to validate that the config fields exist
  private static final ObjectMapper MAPPER = JsonUtils
    .MAPPER.copy()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

  private static final Supplier<BotConfig> CONFIG_SUPPLIER = Suppliers.memoize(
    AbstractBotConfig::loadConfig
  );

  public static BotConfig current() {
    return CONFIG_SUPPLIER.get();
  }

  @Value.Derived
  public String getToken() {
    return System.getProperty("cozy.bot.token");
  }

  public abstract String getLandingChannel();

  private static BotConfig loadConfig() {
    String configPath = System.getProperty("cozy.bot.config");
    if (configPath == null || configPath.isEmpty()) {
      configPath = System.getenv("COZY_BOT_CONFIG");
      if (configPath == null || configPath.isEmpty()) {
        throw new RuntimeException("No config specified!");
      }
    }

    Path config = Paths.get(configPath);
    LOG.info("Got config path: {}", config);
    if (!config.isAbsolute()) {
      config = Paths.get(System.getProperty("user.dir")).resolve(config);
      LOG.debug("Resolved config path: {}", config);
    }

    LOG.info("Loading config from: {}", config);
    try {
      return MAPPER.readValue(config.toFile(), BotConfig.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to read config: " + config, e);
    }
  }
}
