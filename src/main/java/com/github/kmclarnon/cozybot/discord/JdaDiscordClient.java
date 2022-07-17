package com.github.kmclarnon.cozybot.discord;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kmclarnon.cozybot.utils.BotConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Singleton
public class JdaDiscordClient implements DiscordClient {
  private static final Logger LOG = LoggerFactory.getLogger(JdaDiscordClient.class);

  private final JDA api;

  @Inject
  public JdaDiscordClient(BotConfig config, Set<EventListener> eventListeners) {
    try {
      LOG.info("Establishing JDA connection with Discord");
      JDABuilder builder = JDABuilder.createLight(config.getToken());
      builder.enableIntents(
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS
      );

      eventListeners.forEach(builder::addEventListeners);
      this.api = builder.build().awaitReady();
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize JDA", e);
    }
  }

  public SelfUser getSelf() {
    return api.getSelfUser();
  }

  @Override
  public Optional<MessageChannel> findGuildChannelByName(Guild guild, String name) {
    return guild
      .getChannels()
      .stream()
      .filter(
        c ->
          c.getType() == ChannelType.TEXT &&
          c.getName().equals(name) &&
          c instanceof MessageChannel
      )
      .map(c -> (MessageChannel) c)
      .findFirst();
  }

  @Override
  public void close() throws IOException {
    this.api.shutdown();
  }
}
