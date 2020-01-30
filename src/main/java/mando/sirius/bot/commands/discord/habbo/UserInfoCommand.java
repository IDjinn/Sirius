package mando.sirius.bot.commands.discord.habbo;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class UserInfoCommand extends Command {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UserInfoCommand() {
        super("userinfo", "a", new String[]{}, new String[]{}, false);
    }

    @Override
    public boolean execute(@Nonnull Message message, @Nullable SiriusUser selfUser, String... args) {
        if (args.length == 1) {
            message.getChannel().sendMessage(Sirius.getTexts().getValue("sirius.bot.generic.missing_member"));
            return false;
        } else if (!message.getMentionedMembers().isEmpty()) {
            Member mentionUser = message.getMentionedMembers().get(0);
            SiriusUser user = Sirius.getUsersManager().getUser(mentionUser.getIdLong());
            if (user == null) {
                message.getChannel().sendMessage(Sirius.getTexts().getValue("sirius.bot.generic.habbo.no_auth")).queue();
                return false;
            }
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(user.getHabboInfo().getUsername() + "'s Info")
                    .addField("Missão", user.getHabboInfo().getMotto(), false)
                    .addField("Moedas", String.valueOf(user.getHabboInfo().getCredits()), true)
                    .addField("Duckets", String.valueOf(user.getHabboInfo().getPixels()), true)
                    .addField("Diamantes", String.valueOf(user.getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("seasonal.primary.type"))), true)
                    .addField("Online?", user.getHabboInfo().isOnline() ? "Sim" : "Não", true)
                    .addField("Esmeraldas", String.valueOf(user.getHabboInfo().getCurrencyAmount(Emulator.getConfig().getInt("hotel.auto.gotwpoints.type"))), true)
                    .addField("Pontos de Conquista", String.valueOf(user.getHabboInfo().getHabboStats().getAchievementScore()), true)
                    .addField("Respeitos", String.valueOf(user.getHabboInfo().getHabboStats().respectPointsReceived), true)
                    .addField("Criado Em", formatter.format(user.getHabboInfo().getAccountCreated()), false)
                    .setThumbnail(Sirius.getConfig().getValue("sirius.base.habbo.avatar.image").replace("%look%", user.getHabboInfo().getLook()))
                    .setTimestamp(Instant.now());
            if (user.getHabboInfo().getRank().hasPermission("acc_supporttool", false) ||
                    user.getHabboInfo().getRank().hasPermission("cmd_ban", false)) {
                builder.addField("Rank Staff", user.getHabboInfo().getRank().getName(), false);
            }
            if (!user.getHabboInfo().isOnline()) {
                builder.addField("Último Login", formatter.format(user.getHabboInfo().getLastOnline()), false);
            } else if (user.getHabboInfo().getCurrentRoom() != null) {
                builder.addField("Quarto Atual", user.getHabboInfo().getCurrentRoom().getName(), false);
            }
            if (user.getHabboInfo().getHomeRoom() > 0) {
                Room room = Emulator.getGameEnvironment().getRoomManager().loadRoom(user.getHabboInfo().getHomeRoom());
                if (room != null) {
                    builder.addField("Cafofo", room.getName(), false);
                }
            }
            message.getChannel().sendMessage(builder.build()).queue();
        }
        return true;
    }
}