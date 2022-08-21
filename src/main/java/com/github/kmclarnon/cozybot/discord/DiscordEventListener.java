package com.github.kmclarnon.cozybot.discord;

import com.github.kmclarnon.cozybot.utils.BotConfig;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DiscordEventListener extends ListenerAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(DiscordEventListener.class);
  private static final UnicodeEmoji PIN_EMOJI = Emoji.fromUnicode("U+1f4cc");
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
  public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
    if (isPinEmoji(event.getEmoji())) {
      event
        .retrieveMessage()
        .submit()
        .whenComplete(
          (m, t) -> {
            if (t != null) {
              LOG.error("Failed to fetch message for id: {}", event.getMessageId(), t);
            } else if (!m.isPinned()) {
              LOG.debug("Pinning message: {}", m.getId());
              m
                .pin()
                .queue(
                  res ->
                    LOG.debug("Message {} pinned successfully", event.getMessageId()),
                  res -> LOG.error("Failed to pin message {}", event.getMessageId())
                );
            }
          }
        )
        .join();
    }
  }

  @Override
  public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
    if (isPinEmoji(event.getEmoji())) {
      event
        .retrieveMessage()
        .submit()
        .whenComplete(
          (m, t) -> {
            if (t != null) {
              LOG.error("Failed to fetch message for id: {}", event.getMessageId(), t);
            } else if (m.isPinned()) {
              MessageReaction pins = m.getReaction(PIN_EMOJI);
              if (pins != null && pins.getCount() > 0) {
                LOG.debug(
                  "Message {} has remaining pins, skipping unpin",
                  event.getMessageId()
                );
              } else {
                LOG.debug("Unpinning message {}", event.getMessageId());
                m
                  .unpin()
                  .queue(
                    res ->
                      LOG.debug("Message {} unpinned successfully", event.getMessageId()),
                    res -> LOG.error("Failed to unpin message {}", event.getMessageId())
                  );
              }
            }
          }
        )
        .join();
    }
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

  private boolean isPinEmoji(EmojiUnion emojiUnion) {
    return PIN_EMOJI.equals(emojiUnion);
  }
}
