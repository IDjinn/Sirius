package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboInfo;
import mando.sirius.bot.Constants;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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

    public void checkMemberAuth(Guild guild, Member member) {
        if (Sirius.getUsersManager().userExists(member.getIdLong())) {
            this.addAuthRole(guild, member);
            this.syncUsername(member, Sirius.getUsersManager().getUser(member.getIdLong()));
        }
    }

    public boolean authenticateUser(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != Constants.AUTH_CHANNEL || Sirius.getUsersManager().userExists(event.getAuthor().getIdLong()))
            return false;

        String token = event.getMessage().getContentDisplay();
        try {
            event.getMessage().delete().queue();
        } catch (Exception e) {
            Emulator.getLogging().handleException(e);
        }

        if (token.isEmpty() || !Emulator.isReady || !authPending.containsKey(token))
            return false;

        HabboInfo habboInfo = authPending.get(token);
        if (habboInfo == null)
            return false;

        authPending.remove(token);
        if (this.saveUser(habboInfo.getId(), event.getAuthor().getIdLong())) {
            SiriusUser self = new SiriusUser(event.getAuthor(), habboInfo);
            Sirius.getUsersManager().addUser(event.getAuthor().getIdLong(), self);
            this.addAuthRole(event.getGuild(), event.getMember());
            this.syncUsername(event.getMember(), self);
            event.getChannel().sendMessage(Sirius.getTexts().getValue("sirius.bot.auth.sucess").replace("%user%", event.getMember().getAsMention())).queue();
            return true;
        }
        return false;
    }

    private void addAuthRole(Guild guild, Member member) {
        Role role = guild.getRoleById(Constants.AUTH_ROLE);
        if (role != null && !member.getRoles().contains(role) && guild.getSelfMember().canInteract(role)) {
            try {
                guild.addRoleToMember(member.getIdLong(), role).queue();
            } catch (Exception e) {
                Emulator.getLogging().handleException(e);
            }
        }
    }

    private void syncUsername(Member member, SiriusUser self) {
        if (!Constants.SYNC_USERNAME_ENABLED || member == null || self == null)
            return;

        if (member.getEffectiveName().equals(self.getHabboInfo().getUsername()) || !member.getGuild().getSelfMember().canInteract(member))
            return;

        try {
            member.modifyNickname(self.getHabboInfo().getUsername()).queue();
        } catch (Exception e) {
            Emulator.getLogging().handleException(e);
        }
    }

    public boolean saveUser(int habboId, long userId) {
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