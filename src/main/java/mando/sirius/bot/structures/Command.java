package mando.sirius.bot.structures;


import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nullable;

public abstract class Command {
    private String name;
    private String description;
    private String[] needPermissions;
    private String[] aliases;
    private boolean needAuth;

    public Command(String name, String description, String[] needPermissions, String[] aliases, boolean needAuth) {
        this.name = name;
        this.description = description;
        this.needPermissions = needPermissions;
        this.aliases = aliases;
        this.needAuth = needAuth;
    }

    public abstract boolean execute(Message message, @Nullable SiriusUser selfUser, String... args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getNeedPermissions() {
        return needPermissions;
    }

    public String[] getAliases() {
        return aliases;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }
}
