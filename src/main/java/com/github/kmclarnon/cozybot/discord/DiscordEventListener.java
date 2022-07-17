package com.github.kmclarnon.cozybot.discord;

import com.github.kmclarnon.cozybot.utils.BotConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DiscordEventListener extends ListenerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(DiscordEventListener.class);
  private final DiscordClient client;
  private final BotConfig config;

  @Inject
  public DiscordEventListener(DiscordClient client, BotConfig config) {
    this.client = client;
    this.config = config;
  }

  @Override
  public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
    if (event.getMember().getUser().isBot()) {
      return;
    }

    Optional<MessageChannel> landingChannel = client.findGuildChannelByName(
      event.getGuild(),
      config.getLandingChannel()
    );
    if (!landingChannel.isPresent()) {
      LOG.error("Failed to find {}!", config.getLandingChannel());
      return;
    }

    String welcomeMessage =
      "Welcome to The Cozy Corner <@%s>!\nPlease check out the %s channel for important starting info, " +
      "and introduce yourself in %s!";

    landingChannel
      .get()
      .sendMessage(
        String.format(
          welcomeMessage,
          event.getMember().getUser().getId(),
          getChannelLink(event.getGuild(), "readme"),
          getChannelLink(event.getGuild(), "introductions")
        )
      )
      .queue();
  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    super.onMessageReceived(event);
  }

  private String getChannelLink(Guild guild, String channelName) {
    Optional<MessageChannel> readme = client.findGuildChannelByName(guild, channelName);
    if (readme.isPresent()) {
      return String.format("<#%s>", readme.get().getId());
    } else {
      LOG.warn(
        "Failed to find {} channel to link, falling back on simple text",
        channelName
      );
      return String.format("#%s", channelName);
    }
  }
}
