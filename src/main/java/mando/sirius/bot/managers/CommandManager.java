package mando.sirius.bot.managers;

import com.eu.habbo.Emulator;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.commands.discord.PingCommand;
import mando.sirius.bot.commands.discord.habbo.UserInfoCommand;
import mando.sirius.bot.structures.Command;
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
            if (!key.isEmpty() && commands.containsKey(key)) {
                Command cmd = this.getCommand(key);
                if (cmd.isNeedAuth() && !Sirius.getAuthManager().isAuthenticated(message.getAuthor().getIdLong()))
                    return false;
                return cmd.execute(message, split);
            }
            return false;
        } catch (Exception e) {
            Emulator.getLogging().logErrorLine(e);
            return false;
        }
    }

    public void registerCommands() {
        register(new UserInfoCommand());
        register(new PingCommand());
    }

    public void register(Command command) {
        commands.put(command.getName(), command);
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
