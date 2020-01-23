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

public class Sirius extends HabboPlugin implements EventListener {
    private static JDABuilder builder;
    private static JDA jda;
    private static EventManager eventManager;
    private static CommandManager commandManager;
    private static UsersManager usersManager;
    private static AuthManager authManager;
    private static CounterManager counterManager;

    @Override
    public void onEnable() throws Exception {
        Emulator.getPluginManager().registerEvents(this, this);
        if (Emulator.isReady)
            this.init();
    }

    private void init() {
        try {
            this.prepareTextsAndSettings();
            this.initConstants();
            counterManager = new CounterManager();
            eventManager = new EventManager();
            this.login();
            commandManager = new CommandManager();
            authManager = new AuthManager();
            usersManager = new UsersManager();
            Emulator.getLogging().logStart("[Sirius B0T] Started Sirius discord bot");
        } catch (LoginException e) {
            Emulator.getLogging().handleException(e);
        }
    }

    private void prepareTextsAndSettings() {
        Emulator.getConfig().register("sirius.channel.captcha.id", "0");
        Emulator.getConfig().register("sirius.authenticated.role.id", "0");
        Emulator.getConfig().register("sirius.counter.channel.id", "0");
        Emulator.getTexts().register("sirius.bot.auth.sucess", "Sucess! You're authenticated now.");
        Emulator.getTexts().register("commands.keys.cmd_discord_auth", "discord;login_discord");
        Emulator.getTexts().register("commands.description.cmd_discord_auth", "Authenticate your discord account.");
        Emulator.getTexts().register("sirius.cmd_discord_auth.account.exists", "You already have an discord account authenticated");
        Emulator.getTexts().register("sirius.cmd_discord_auth.sucess", "Sucess! Your authentication token is %token%, join in our discord and use it for verify your account.");
        if (this.registerPermission("cmd_discord_auth", "'0','1'", "1", "cmd_disconnect"))
            Emulator.getGameEnvironment().getPermissionsManager().reload();
        CommandHandler.addCommand(new DiscordAuthCommand());
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

    private void initConstants(){
        Constants.TOKEN = Emulator.getConfig().getValue("sirius.bot.token");
        Constants.AUTH_CHANNEL = Long.parseLong(Emulator.getConfig().getValue("sirius.channel.auth.id"));
        Constants.AUTH_ROLE = Long.parseLong(Emulator.getConfig().getValue("sirius.authenticated.role.id"));
        Constants.COUNTER_CHANNEL = Long.parseLong(Emulator.getConfig().getValue("sirius.counter.channel.id"));
    }

    @EventHandler
    public void onEmulatorLoaded(EmulatorLoadedEvent event) {
        this.init();
    }

    @Override
    public void onDisable() throws Exception {
        Emulator.getLogging().logStart("[Sirius B0T] Stopped Sirius discord bot");
    }

    @Override
    public boolean hasPermission(Habbo habbo, String s) {
        return false;
    }

    public void login() throws LoginException {
        builder = new JDABuilder(AccountType.BOT).addEventListeners(eventManager).setToken(Constants.TOKEN);
        jda = builder.build();
    }

    public static JDA getJda() {
        return jda;
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