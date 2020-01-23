package mando.sirius.bot.structures;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import net.dv8tion.jda.api.entities.User;

public class SiriusUser {
    private User user;
    private HabboInfo habboInfo;

    public SiriusUser(User user, HabboInfo habboInfo) {
        this.user = user;
        this.habboInfo = habboInfo;
    }

    public User getUser() {
        return user;
    }

    public HabboInfo getHabboInfo() {
        return habboInfo;
    }
}
