package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboInfo;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class UsersManager {
    private Map<Long, SiriusUser> users = new HashMap<>();
    private Map<Integer, Long> usersFromHabbo = new HashMap<>();

    public UsersManager() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            try (ResultSet set = statement.executeQuery("SELECT * FROM `sirius_users`")) {
                while (set.next()) {
                    User user = Sirius.getJda().getUserById(set.getLong("user_id"));
                    HabboInfo habboInfo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(set.getInt("habbo_id"));
                    if (user == null || habboInfo == null)
                        continue;

                    SiriusUser siriusUser = new SiriusUser(user, habboInfo);
                    this.addUser(user.getIdLong(), siriusUser);
                }
            } catch (SQLException e) {
                Emulator.getLogging().logSQLException(e);
            }
        } catch (SQLException e) {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public Map<Long, SiriusUser> getUsers() {
        return users;
    }

    public Map<Integer, Long> getUsersFromHabbo() {
        return usersFromHabbo;
    }

    public SiriusUser getUserFromHabbo(@Nonnull int id) {
        if (usersFromHabbo.containsKey(id)) {
            return getUser(usersFromHabbo.get(id));
        }
        return null;
    }

    public SiriusUser getUser(@Nonnull long id) {
        if (users.containsKey(id))
            return users.get(id);
        return null;
    }

    public boolean userExists(long id) {
        return users.containsKey(id);
    }

    public boolean userFromHabboExists(int id) {
        return usersFromHabbo.containsKey(id);
    }

    public void addUser(long id, @Nonnull SiriusUser user) {
        this.users.put(id, user);
        this.usersFromHabbo.put(user.getHabboInfo().getId(), id);
    }
}