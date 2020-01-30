package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.commands.discord.PingCommand;
import mando.sirius.bot.commands.discord.habbo.OnlinesCommand;
import mando.sirius.bot.commands.discord.habbo.RoomsCommand;
import mando.sirius.bot.commands.discord.habbo.UserInfoCommand;
import mando.sirius.bot.commands.discord.moderation.BanCommand;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private Map<String, Command> commands = new HashMap<>();
    private Map<String, String> aliases = new HashMap<>();

    public CommandManager() {
        this.registerCommands();
    }

    public boolean executeCommand(@Nonnull Message message) {
        if (!message.getContentDisplay().startsWith(">"))
            return false;

        try {
            String[] split = message.getContentDisplay().substring(1).split("\\s+");
            String key = split[0];
            if (key.isEmpty())
                return false;

            Command cmd = commands.containsKey(key) ? commands.get(key) : aliases.containsKey(key) ? commands.get(aliases.get(key)) : null;
            if (cmd == null)
                return false;

            if (cmd.isNeedAuth() && !Sirius.getAuthManager().isAuthenticated(message.getAuthor().getIdLong()))
                return false;

            SiriusUser selfUser = Sirius.getUsersManager().getUser(message.getAuthor().getIdLong());
            return cmd.execute(message, selfUser, split);
        } catch (Exception e) {
            Emulator.getLogging().handleException(e);
            return false;
        }
    }

    public void registerCommands() {
        register(new UserInfoCommand());
        register(new OnlinesCommand());
        register(new RoomsCommand());
        register(new BanCommand());
        register(new PingCommand());
    }

    public void register(Command command) {
        commands.put(command.getName(), command);
        for (String aliase : command.getAliases()) {
            if (!aliase.isEmpty())
                aliases.put(aliase, command.getName());
        }
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }
    public Command getCommand(@Nonnull String name){
        return commands.get(name);
    }
}
