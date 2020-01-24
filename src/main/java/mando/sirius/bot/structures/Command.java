package mando.sirius.bot.structures;


import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;

public abstract class Command {
    private String name;
    private String description;
    private String[] needPermissions;
    private boolean needAuth;

    public Command(String name, String description, String[] needPermissions, boolean needAuth) {
        this.name = name;
        this.description = description;
        this.needPermissions = needPermissions;
        this.needAuth = needAuth;
    }

    public abstract boolean execute(@Nonnull Message message, String... args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getNeedPermissions() {
        return needPermissions;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }
}
