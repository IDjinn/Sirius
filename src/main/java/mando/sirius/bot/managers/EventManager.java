package mando.sirius.bot.managers;

import mando.sirius.bot.Sirius;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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
}
