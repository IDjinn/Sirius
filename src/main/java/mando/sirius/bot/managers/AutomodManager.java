package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class AutomodManager {
    public AutomodManager() {
    }

    public void muteUser(SiriusUser user) {

    }

    public void banUser(SiriusUser user, int days, String reason) {
        Guild guild = Sirius.getJda().getGuilds().get(0);
        if (guild == null)
            return;

        try {
            Member member = guild.getMemberById(user.getUser().getIdLong());
            if (member == null)
                return;

            member.ban(days, reason).queue();
        } catch (Exception e) {
            Emulator.getLogging().handleException(e);
        }
    }
}