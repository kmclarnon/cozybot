package com.github.kmclarnon.cozybot.discord;

import java.io.Closeable;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface DiscordClient extends Closeable {
  Optional<MessageChannel> findGuildChannelByName(Guild guild, String name);
}
