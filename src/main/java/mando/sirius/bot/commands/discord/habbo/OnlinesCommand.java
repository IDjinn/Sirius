package mando.sirius.bot.commands.discord.habbo;

import com.eu.habbo.Emulator;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OnlinesCommand extends Command {
    public OnlinesCommand() {
        super("online", "a", new String[]{"onlines", "userson"}, new String[]{}, false);
    }

    @Override
    public boolean execute(@Nonnull Message message, @Nullable SiriusUser selfUser, String... args) {
        if (Emulator.getGameEnvironment().getHabboManager().getOnlineCount() > 0)
            message.getChannel().sendMessage(Sirius.getTexts().getValue("sirius.bot.cmd_onlines.sucess")
                    .replace("%players%", String.valueOf(Emulator.getGameEnvironment().getHabboManager().getOnlineCount()))).queue();
        else
            message.getChannel().sendMessage(Sirius.getTexts().getValue("sirius.bot.cmd_onlines.no_users")).queue();
        return true;
    }
}
