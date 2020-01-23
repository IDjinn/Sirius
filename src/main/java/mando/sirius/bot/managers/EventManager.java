package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class EventManager extends ListenerAdapter {
    public EventManager(){
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (event.getUser().isBot() || event.getUser().isFake())
            return;

        Sirius.getAuthManager().checkMemberAuth(event);
        Sirius.getCounterManager().updateGuildCounter(event.getGuild());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake())
            return;

        if (Sirius.getAuthManager().authenticateUser(event))
            return;

        if (Sirius.getCommandManager().executeCommand(event.getMessage()))
            return;
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        Emulator.getLogging().logStart("[Sirius B0T] Ready!");
    }
}
