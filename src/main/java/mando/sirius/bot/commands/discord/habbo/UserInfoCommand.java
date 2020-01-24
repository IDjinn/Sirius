package mando.sirius.bot.commands.discord.habbo;

import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;

public class UserInfoCommand extends Command {
    public UserInfoCommand() {
        super("userinfo", "a", new String[]{}, false);
    }

    @Override
    public boolean execute(@Nonnull Message message, String... args) {
        if (args.length == 1) {
            //TODO missing user
            System.out.println("A");
            return false;
        } else if (!message.getMentionedMembers().isEmpty()) {
            Member mentionUser = message.getMentionedMembers().get(0);
            SiriusUser user = Sirius.getUsersManager().getUser(mentionUser.getIdLong());
            if (user == null) {
                //TODO user == null
                System.out.println("userasdead");
                return false;
            }

            message.getChannel().sendMessage(String.valueOf(user.getHabboInfo().getId())).queue();
        }
        return true;
    }
}
