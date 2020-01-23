package mando.sirius.bot.structures;


import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;

public abstract class Command {
    private String name;
    private String description;
    private String[] needPermissions;
    public Command(String name, String description, String[] needPermissions){
        this.name = name;
        this.description = description;
        this.needPermissions = needPermissions;
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
}
