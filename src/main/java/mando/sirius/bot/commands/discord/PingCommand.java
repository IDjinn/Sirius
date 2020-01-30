package mando.sirius.bot.commands.discord;

import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nullable;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping", "teste", new String[]{}, new String[]{}, false);
    }

    @Override
    public boolean execute(Message message, @Nullable SiriusUser selfUser, String... args) {
        if (message.getAuthor().getId() == "661627884997181471")
            Sirius.getConfig().reload();

        long time = System.currentTimeMillis();
        message.getChannel().sendMessage("Calculating...").queue(
                (Message m) -> m.editMessageFormat("Pong: %dms\nHeartBeat: %dms", System.currentTimeMillis() - time,
                        Sirius.getJda().getGatewayPing()).queue());
        return true;
    }
}
