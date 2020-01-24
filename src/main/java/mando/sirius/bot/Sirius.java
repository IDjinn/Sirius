package mando.sirius.bot;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import mando.sirius.bot.commands.hotel.DiscordAuthCommand;
import mando.sirius.bot.managers.*;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Sirius extends HabboPlugin implements EventListener {
    private static JDA jda;
    private static ConfigurationManager configurationManager;
    private static TextsManager textsManager;
    private static EventManager eventManager;
    private static CommandManager commandManager;
    private static UsersManager usersManager;
    private static AuthManager authManager;
    private static CounterManager counterManager;

    @Override
    public void onEnable() {
        Emulator.getPluginManager().registerEvents(this, this);
        if (Emulator.isReady)
            this.init();
    }

    public static ConfigurationManager getConfig() {
        return configurationManager;
    }

    public static TextsManager getTexts() {
        return textsManager;
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

    private boolean registerPermission(String name, String options, String defaultValue, String afterColumn) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE  `permissions` ADD  `" + name + "` ENUM(  " + options + " ) NOT NULL DEFAULT  '" + defaultValue + "' AFTER `" + afterColumn + "`")) {
                statement.execute();
                return true;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    private void prepareTextsAndSettings() {
        getConfig().register("sirius.auth.channel.id", "0");
        getConfig().register("sirius.authenticated.role.id", "0");
        getConfig().register("sirius.counter.channel.id", "0");
        getTexts().register("sirius.bot.auth.sucess", "Sucess! You're authenticated now.");
        getTexts().register("sirius.keys.cmd_discord_auth", "discord;login_discord");
        getTexts().register("sirius.description.cmd_discord_auth", "Authenticate your discord account.");
        getTexts().register("sirius.cmd_discord_auth.account.exists", "You already have an discord account authenticated");
        getTexts().register("sirius.cmd_discord_auth.sucess", "Sucess! Your authentication token is %token%, join in our discord and use it for verify your account.");
        if (this.registerPermission("cmd_discord_auth", "'0','1'", "1", "cmd_disconnect"))
            Emulator.getGameEnvironment().getPermissionsManager().reload();
        CommandHandler.addCommand(new DiscordAuthCommand());
    }

    @Override
    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }

    private void initConstants() {
        Constants.TOKEN = getConfig().getValue("sirius.bot.token");
        Constants.AUTH_CHANNEL = Long.parseLong(getConfig().getValue("sirius.auth.channel.id"));
        Constants.AUTH_ROLE = Long.parseLong(getConfig().getValue("sirius.authenticated.role.id"));
        //Constants.COUNTER_CHANNEL = Long.parseLong(getConfig().getValue("sirius.counter.channel.id"));
    }

    public static JDA getJda() {
        return jda;
    }

    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent event) {
        this.init();
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
            Sirius.getJda().awaitReady();
            // Await jda ready to load users

            usersManager = new UsersManager();
            authManager = new AuthManager();
            commandManager = new CommandManager();
            counterManager = new CounterManager();
            Emulator.getLogging().logStart("[Sirius B0T] Ready");
        } catch (LoginException | InterruptedException e) {
            Emulator.getLogging().handleException(e);
        }
    }

    public void login() throws LoginException {
        jda = new JDABuilder(AccountType.BOT).addEventListeners(eventManager).setToken(Constants.TOKEN).build();
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

    public static CounterManager getCounterManager() {
        return counterManager;
    }
}