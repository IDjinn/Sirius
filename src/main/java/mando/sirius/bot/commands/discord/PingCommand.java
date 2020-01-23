package mando.sirius.bot.commands.discord;

import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import net.dv8tion.jda.api.entities.Message;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping", "teste", new String[]{});
    }

    @Override
    public boolean execute(Message message, String... args) {
        long time = System.currentTimeMillis();
        message.getChannel().sendMessage("Calculating...").queue(
                (Message m) -> m.editMessageFormat("Pong: %dms\nHeartBeat: %dms", System.currentTimeMillis() - time,
                Sirius.getJDA().getGatewayPing()).queue());
        return true;
    }
}
