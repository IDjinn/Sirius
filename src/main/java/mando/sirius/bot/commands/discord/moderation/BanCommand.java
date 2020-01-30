package mando.sirius.bot.commands.discord.moderation;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.users.Habbo;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nullable;

public class BanCommand extends Command {
    public BanCommand() {
        super("ban", "description", new String[]{"BAN_MEMBERS"}, new String[]{}, false);
    }

    @Override
    public boolean execute(Message message, @Nullable SiriusUser selfUser, String... args) {
        if (message.getMentionedMembers().isEmpty()) {
            message.getChannel().sendMessage("You need put the member to ban!").queue();
            return false;
        } else {
            try {
                Member member = message.getMentionedMembers().get(0);
                member.ban(7).queue();
                boolean banidoNoHotel = false;
                if (Sirius.getUsersManager().userExists(member.getIdLong())) {
                    SiriusUser user = Sirius.getUsersManager().getUser(member.getIdLong());
                    if (user.getHabboInfo().isOnline()) {
                        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(user.getHabboInfo().getId());
                        if (habbo != null)
                            habbo.disconnect();
                        ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().createOfflineUserBan(user.getHabboInfo().getId(),
                                -1, Integer.MAX_VALUE - Emulator.getIntUnixTimestamp(), "Banned in discord", ModToolBanType.SUPER);
                        Emulator.getThreading().run(ban);
                        banidoNoHotel = true;
                    }
                }
                message.getChannel().sendMessage("Sucessoo, " + banidoNoHotel).queue();
            } catch (Exception e) {
                Emulator.getLogging().handleException(e);
            }
        }
        return true;
    }
}
