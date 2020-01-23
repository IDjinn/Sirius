package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboInfo;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UsersManager {
    private Map<Long, SiriusUser> users = new HashMap<>();
    private Map<Integer, Long> usersFromHabbo = new HashMap<>();
    private static String CreateTableQuery = "CREATE TABLE IF NOT EXISTS`sirius_users` (\n" +
            "    `habbo_id` INT(11) NOT NULL,\n" +
            "    `user_id` VARCHAR(20) NOT NULL,\n" +
            "    PRIMARY KEY (`habbo_id`),\n" +
            "    UNIQUE INDEX `user_id` (`user_id`),\n" +
            "    CONSTRAINT `Habbo` FOREIGN KEY (`habbo_id`) REFERENCES `users` (`id`)\n" +
            ")";

    public UsersManager() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(CreateTableQuery);
            try (ResultSet set = statement.executeQuery("SELECT * FROM `sirius_users`")) {
                while (set.next()) {
                    User user = Sirius.getJda().getUserById(set.getString("user_id"));
                    HabboInfo habboInfo = Emulator.getGameEnvironment().getHabboManager().getHabboInfo(set.getInt("habbo_id"));
                    if (user == null || habboInfo == null)
                        continue;

                    SiriusUser siriusUser = new SiriusUser(user, habboInfo);
                    this.addUser(user.getIdLong(), siriusUser);
                    this.usersFromHabbo.put(habboInfo.getId(), user.getIdLong());
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

    public boolean userExists(long id){
        return users.containsKey(id);
    }
    public boolean userFromHabboExists(int id){
        return usersFromHabbo.containsKey(id);
    }

    public void addUser(@Nonnull long id, @Nonnull SiriusUser user) {
        this.users.put(id, user);
        this.usersFromHabbo.put(user.getHabboInfo().getId(), id);
    }
}