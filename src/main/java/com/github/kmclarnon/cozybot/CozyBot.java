package com.github.kmclarnon.cozybot;

import com.github.kmclarnon.cozybot.discord.JdaDiscordClient;
import com.github.kmclarnon.cozybot.utils.CloseableInjector;
import com.github.kmclarnon.cozybot.utils.SingletonCloser;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CozyBot {
  private static final Logger LOG = LoggerFactory.getLogger(CozyBot.class);
  private final JdaDiscordClient discordClient;
  private boolean running;

  public static void main(String[] args) {
    try (
      CloseableInjector injector = SingletonCloser.createInjector(new CozyBotModule())
    ) {
      injector.getInstance(CozyBot.class).run();
    }
  }

  @Inject
  public CozyBot(JdaDiscordClient discordClient) {
    this.discordClient = discordClient;
  }

  public void run() {
    running = true;

    while (running) {
      Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
    }
  }
}
