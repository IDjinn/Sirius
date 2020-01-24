package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboInfo;
import mando.sirius.bot.Constants;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AuthManager {
    private Map<String, HabboInfo> authPending = new HashMap<>();

    public AuthManager() {
    }

    public boolean isAuthenticated(long id) {
        return Sirius.getUsersManager().userExists(id);
    }

    public void checkMemberAuth(@Nonnull GuildMemberJoinEvent event) {
        if (Sirius.getUsersManager().userExists(event.getUser().getIdLong())) {
            addAuthRole(event.getGuild(), event.getUser());
        }
    }

    public boolean authenticateUser(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != Constants.AUTH_CHANNEL || Sirius.getUsersManager().userExists(event.getAuthor().getIdLong()))
            return false;

        String token = event.getMessage().getContentDisplay();
        if (token.isEmpty() || !Emulator.isReady || !authPending.containsKey(token))
            return false;

        HabboInfo habboInfo = authPending.get(token);
        if (habboInfo == null)
            return false;

        authPending.remove(token);
        if (this.saveUser(habboInfo.getId(), event.getAuthor().getIdLong())) {
            Sirius.getUsersManager().addUser(event.getAuthor().getIdLong(), new SiriusUser(event.getAuthor(), habboInfo));
            addAuthRole(event.getGuild(), event.getAuthor());
            event.getChannel().sendMessage(Emulator.getTexts().getValue("sirius.bot.auth.sucess")).queue();
            return true;
        }

        return false;
    }

    private void addAuthRole(@Nonnull Guild guild, @Nonnull User user) {
        Role role = guild.getRoleById(Constants.AUTH_ROLE);
        if (role != null) {
            guild.addRoleToMember(user.getIdLong(), role).queue();
        }
    }

    public boolean saveUser(@Nonnull int habboId, @Nonnull long userId) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO `sirius_users` VALUE (?,?)")) {
            statement.setInt(1, habboId);
            statement.setString(2, String.valueOf(userId));
            statement.execute();
            return true;
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
            return false;
        }
    }

    public void addPending(@Nonnull String token, @Nonnull HabboInfo habboInfo) {
        this.authPending.put(token, habboInfo);
    }
}
