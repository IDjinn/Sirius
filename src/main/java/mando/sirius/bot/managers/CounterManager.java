package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import mando.sirius.bot.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;

public class CounterManager {
    public CounterManager(){}
    public void updateGuildCounter(Guild guild){
        GuildChannel channel = guild.getTextChannelById(Constants.COUNTER_CHANNEL);
        if (channel != null)
            try {
                channel.getManager().setName(Constants.COUNTER_CHANNEL_NAME.replace("%users%",
                        String.valueOf(Emulator.getGameEnvironment().getHabboManager().getOnlineCount())));
            } catch (Exception e) {
                Emulator.getLogging().handleException(e);
            }
    }
}
