package mando.sirius.bot;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.support.SupportUserBannedEvent;
import com.eu.habbo.plugin.events.users.UserExecuteCommandEvent;
import mando.sirius.bot.commands.hotel.DiscordAuthCommand;
import mando.sirius.bot.managers.*;
import mando.sirius.bot.structures.CounterUpdateScheduler;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

public class Sirius extends HabboPlugin implements EventListener {
    private static JDA jda;
    private static ConfigurationManager configurationManager;
    private static TextsManager textsManager;
    private static EventManager eventManager;
    private static CommandManager commandManager;
    private static UsersManager usersManager;
    private static AuthManager authManager;
    private static AutomodManager automodManager;

    @Override
    public void onEnable() {
        Emulator.getPluginManager().registerEvents(this, this);
        if (Emulator.isReady)
            this.init();
    }

    @Override
    public void onDisable() {
        try {
            getJda().shutdown();
            getConfig().saveToDatabase();
            Emulator.getLogging().logStart("[Sirius B0T] Stopped Sirius discord bot");
        } catch (Exception e) {
        }
    }

    public static JDA getJda() {
        return jda;
    }

    public static ConfigurationManager getConfig() {
        return configurationManager;
    }

    public static TextsManager getTexts() {
        return textsManager;
    }

    public static AutomodManager getAutomodManager() {
        return automodManager;
    }

    @Override
    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }

    private void prepareTables() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(Constants.CREATE_USERS_TABLE_QUERY);
            statement.execute(Constants.CREATE_TEXTS_TABLE_QUERY);
            statement.execute(Constants.CREATE_SETTINGS_TABLE_QUERY);
        } catch (SQLException e) {
            Emulator.getLogging().logErrorLine(e);
        }
        ;
    }

    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent event) {
        this.init();
    }

    @EventHandler
    public void onUserExecutedCommand(UserExecuteCommandEvent event) {
        if (Constants.EVENT_LOG_CHANNEL <= 0L || event.command.permission.isEmpty() || !event.command.permission.contains("cmd_event"))
            return;

        TextChannel channel = getJda().getTextChannelById(Constants.EVENT_LOG_CHANNEL);
        if (channel == null)
            return;

        StringBuilder message = new StringBuilder();
        for (int i = 1; i < event.params.length; i++) {
            message.append(event.params[i]);
            message.append(" ");
        }

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Event running now!")
                .addField("Name", message.toString(), false)
                .addField("Staff", event.habbo.getHabboInfo().getUsername(), true)
                .addField("Room", event.habbo.getHabboInfo().getCurrentRoom().getName(), true)
                .setTimestamp(Instant.now());
        if (getUsersManager().userFromHabboExists(event.habbo.getHabboInfo().getId()))
            builder.addField("Mention", getUsersManager().getUserFromHabbo(event.habbo.getHabboInfo().getId()).getUser().getAsMention(), true);

        channel.sendMessage(builder.build()).queue();
    }

    @EventHandler
    public void onBanEvent(SupportUserBannedEvent event) {
        if (!Constants.SYNC_BAN_ENABLED || event.target == null)
            return;

        if (!Sirius.getUsersManager().userFromHabboExists(event.target.getHabboInfo().getId()))
            return;

        SiriusUser user = Sirius.getUsersManager().getUserFromHabbo(event.target.getHabboInfo().getId());
        Sirius.getAutomodManager().banUser(user, (int) Math.floor(event.ban.expireDate / 3600), event.ban.reason);
    }

    private void init() {
        try {
            this.prepareTables();
            configurationManager = new ConfigurationManager();
            textsManager = new TextsManager();
            this.prepareTextsAndSettings();
            this.initConstants();
            eventManager = new EventManager();
            this.login();
            // Await jda ready to load users
            Sirius.getJda().awaitReady();
            this.cleanGuilds();

            usersManager = new UsersManager();
            authManager = new AuthManager();
            commandManager = new CommandManager();
            automodManager = new AutomodManager();
            Emulator.getThreading().run(new CounterUpdateScheduler());
            Emulator.getLogging().logStart("[Sirius B0T] Ready");
        } catch (LoginException | InterruptedException e) {
            Emulator.getLogging().handleException(e);
        }
    }

    public void login() throws LoginException {
        jda = new JDABuilder(AccountType.BOT).addEventListeners(eventManager).setToken(Constants.TOKEN).build();
    }

    private void registerPermission(String name, String options, String defaultValue, String afterColumn) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE  `permissions` ADD  `" + name + "` ENUM(  " + options + " ) NOT NULL DEFAULT  '" + defaultValue + "' AFTER `" + afterColumn + "`")) {
                statement.execute();
            }
        } catch (SQLException e) {
        }
    }

    private void prepareTextsAndSettings() {
        getConfig().register("sirius.bot.sync.ban", "1");
        getConfig().register("sirius.bot.sync.username", "1");
        getConfig().register("sirius.auth.channel.id", "0");
        getConfig().register("sirius.current.guild.id", "0");
        getConfig().register("sirius.authenticated.role.id", "0");
        getConfig().register("sirius.counter.channel.id", "0");
        getConfig().register("sirius.counter.channel.name", "%users% habbos online now!");
        getConfig().register("sirius.counter.update.interval", "600");
        getConfig().register("sirius.bot.log.channel.id", "0");
        getConfig().register("sirius.event.log.channel.id", "0");
        getConfig().register("sirius.base.habbo.avatar.image", "https://127.0.0.1/%look%");
        getConfig().register("sirius.bot.ltd.icon", "https://127.0.0.1/swf/dcr/hof_furni/%icon&.png");
        getTexts().register("sirius.bot.cmd_onlines.no_users", "We haven't any player connected.");
        getTexts().register("sirius.bot.cmd_onlines.sucess", "Now we have '%players%' players connected.");
        getTexts().register("sirius.bot.cmd_rooms.no_rooms", "We haven't any room with users.");
        //getTexts().register("sirius.bot.cmd_rooms.sucess", "Now we have '%rooms%' rooms activeds.");
        getTexts().register("sirius.bot.cmd_userinfo.missing_member", "You're forgetting user mention to do this command.");
        getTexts().register("sirius.bot.generic.habbo.no_auth", "I don't find this habbo, maybe they aren't auth.");
        getTexts().register("sirius.bot.auth.sucess", "Sucess! You're authenticated now.");
        getTexts().register("sirius.keys.cmd_discord_auth", "discord;login_discord");
        getTexts().register("sirius.description.cmd_discord_auth", "Authenticate your discord account.");
        getTexts().register("sirius.cmd_discord_auth.account.exists", "You already have an discord account authenticated");
        getTexts().register("sirius.cmd_discord_auth.sucess", "Sucess! Your authentication token is %token%, join in our discord and use it for verify your account.");
        this.registerPermission("cmd_discord_auth", "'0','1'", "1", "cmd_disconnect");
        Emulator.getGameEnvironment().getPermissionsManager().reload();
        CommandHandler.addCommand(new DiscordAuthCommand());
    }

    private void initConstants() {
        Constants.TOKEN = getConfig().getValue("sirius.bot.token");
        Constants.SYNC_BAN_ENABLED = getConfig().getBoolean("sirius.bot.sync.ban", true);
        Constants.SYNC_USERNAME_ENABLED = getConfig().getBoolean("sirius.bot.sync.username", true);
        Constants.LOG_CHANNEL_ID = Long.parseLong(getConfig().getValue("sirius.event.log.channel.id"));
        Constants.CURRENT_GUILD_ID = Long.parseLong(getConfig().getValue("sirius.current.guild.id"));
        Constants.AUTH_CHANNEL = Long.parseLong(getConfig().getValue("sirius.auth.channel.id"));
        Constants.AUTH_ROLE = Long.parseLong(getConfig().getValue("sirius.authenticated.role.id"));
        Constants.COUNTER_CHANNEL = Long.parseLong(getConfig().getValue("sirius.counter.channel.id"));
        Constants.COUNTER_CHANNEL_NAME = getConfig().getValue("sirius.counter.channel.name");
        Constants.EVENT_LOG_CHANNEL = Long.parseLong(getConfig().getValue("sirius.event.log.channel.id"));
    }

    public static EventManager GetEventManager() {
        return eventManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static UsersManager getUsersManager() {
        return usersManager;
    }

    public static AuthManager getAuthManager() {
        return authManager;
    }

    public void cleanGuilds() {
        if (Constants.CURRENT_GUILD_ID > 0L && getJda().getGuilds().size() > 1) {
            getJda().getGuilds().forEach(Guild -> {
                if (Guild != null && Guild.getIdLong() != Constants.CURRENT_GUILD_ID) {
                    Emulator.getLogging().logErrorLine("[SIRIUS B0T] Leaving from guild '" + Guild.getIdLong() + "', it isn't in the database.");
                    Guild.leave().queue();
                }
            });
        } else {
            Guild guild = getJda().getGuilds().get(0);
            getConfig().update("sirius.current.guild.id", String.valueOf(guild.getIdLong()));
            this.initConstants();
        }
    }
}