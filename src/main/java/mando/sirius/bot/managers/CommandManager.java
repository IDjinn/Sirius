package mando.sirius.bot.managers;

import mando.sirius.bot.commands.discord.PingCommand;
import mando.sirius.bot.structures.Command;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

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
        if(!message.getContentDisplay().startsWith(">"))
            return false;

        String[] split = message.getContentDisplay().substring(1).split("\\s+");
        String key = split[0];
        if (!key.isEmpty() && commands.containsKey(key))
            return getCommand(key).execute(message, split);
        return false;
    }

    public void registerCommands() {
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
