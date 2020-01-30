package mando.sirius.bot.commands.discord.habbo;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogLimitedConfiguration;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Map;

public class LTDAvailableCommand extends Command {
    public LTDAvailableCommand(String name, String description, String[] needPermissions, String[] aliases, boolean needAuth) {
        super(name, description, needPermissions, aliases, needAuth);
    }

    @Override
    public boolean execute(@Nonnull Message message, @Nullable SiriusUser selfUser, String... args) {
        //TODO this
        // Emulator.getGameEnvironment().getCatalogManager().limitedNumbers.get(0).limitedSold();
        for (Map.Entry<Integer, CatalogLimitedConfiguration> ltdConfiguration : Emulator.getGameEnvironment().getCatalogManager().limitedNumbers.entrySet()) {
            if (ltdConfiguration == null)
                continue;

            CatalogItem ltd = Emulator.getGameEnvironment().getCatalogManager().getCatalogItem(ltdConfiguration.getKey());
            if (ltd == null)
                continue;

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("LTD's Available")
                    .addField("Name", ltd.getName(), false)
                    .addField("Credits", String.valueOf(ltd.getCredits()), true)
                    .setThumbnail(Sirius.getConfig().getValue("sirius.bot.ltd.icon").replace("%icon%", ltd.getName()))
                    .setTimestamp(Instant.now());
        }
        return false;
    }
}
