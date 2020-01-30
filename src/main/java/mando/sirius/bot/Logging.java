package mando.sirius.bot;

import com.eu.habbo.Emulator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;

public class Logging {
    public static void error(Exception e) {//Todo: make this
        Emulator.getLogging().handleException(e);
        if (Constants.LOG_CHANNEL_ID > 0L) {
            TextChannel channel = Sirius.getJda().getTextChannelById(Constants.LOG_CHANNEL_ID);
            if (channel == null)
                return;

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Error")
                    .appendDescription(e.getLocalizedMessage())
                    .setTimestamp(Instant.now());
            channel.sendMessage(builder.build()).queue();
        }
    }
}
