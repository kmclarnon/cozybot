package com.github.kmclarnon.cozybot;

import com.github.kmclarnon.cozybot.discord.DiscordClient;
import com.github.kmclarnon.cozybot.discord.DiscordEventListener;
import com.github.kmclarnon.cozybot.discord.JdaDiscordClient;
import com.github.kmclarnon.cozybot.utils.BaseGuiceModule;
import com.github.kmclarnon.cozybot.utils.BotConfig;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CozyBotModule extends BaseGuiceModule {
  private static final Logger LOG = LoggerFactory.getLogger(CozyBotModule.class);

  @Override
  protected void configure() {
    Multibinder
      .newSetBinder(binder(), EventListener.class)
      .addBinding()
      .to(DiscordEventListener.class);

    bind(JdaDiscordClient.class).asEagerSingleton();
    bind(DiscordClient.class).to(JdaDiscordClient.class);
  }

  @Provides
  @Singleton
  public BotConfig providesConfig() {
    return BotConfig.current();
  }
}
