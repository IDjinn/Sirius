package mando.sirius.bot.commands.hotel;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import mando.sirius.bot.Sirius;

import java.util.Random;

public class DiscordAuthCommand extends Command {
    public DiscordAuthCommand() {
        super("cmd_discord_auth", Sirius.getTexts().getValue("commands.keys.cmd_discord_auth").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception {
        if(Sirius.getUsersManager().userFromHabboExists(gameClient.getHabbo().getHabboInfo().getId()))
        {
            gameClient.getHabbo().whisper(Sirius.getTexts().getValue("sirius.cmd_discord_auth.account.exists"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        String token = String.valueOf(new Random().nextInt(10000000));
        Sirius.getAuthManager().addPending(token, gameClient.getHabbo().getHabboInfo());
        gameClient.getHabbo().whisper(Sirius.getTexts().getValue("sirius.cmd_discord_auth.sucess").replace("%token%", token), RoomChatMessageBubbles.ALERT);
        return true;
    }
}
