package mando.sirius.bot.structures;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Scheduler;
import mando.sirius.bot.Constants;
import mando.sirius.bot.Sirius;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;

public class CounterUpdateScheduler extends Scheduler {
    public CounterUpdateScheduler() {
        super(Sirius.getConfig().getInt("sirius.counter.update.interval"));
    }

    @Override
    public void run() {
        Guild guild = Sirius.getJda().getGuilds().get(0);
        if (guild != null) {
            GuildChannel channel = guild.getVoiceChannelById(Constants.COUNTER_CHANNEL);
            if (channel != null) {
                try {
                    channel.getManager().setName(Constants.COUNTER_CHANNEL_NAME.replace("%users%",
                            String.valueOf(Emulator.getGameEnvironment().getHabboManager().getOnlineCount()))).queue();
                } catch (Exception e) {
                    Emulator.getLogging().handleException(e);
                }
            }
        }
    }
}