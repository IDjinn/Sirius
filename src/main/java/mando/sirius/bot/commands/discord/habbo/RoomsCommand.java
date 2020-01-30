package mando.sirius.bot.commands.discord.habbo;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import mando.sirius.bot.Sirius;
import mando.sirius.bot.structures.Command;
import mando.sirius.bot.structures.SiriusUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;

public class RoomsCommand extends Command {
    public RoomsCommand() {
        super("rooms", "description", new String[]{}, new String[]{}, false);
    }

    @Override
    public boolean execute(@Nonnull Message message, @Nullable SiriusUser selfUser, String... args) {
        if (Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() > 0) {
            String roomsName = "", roomsUsers = "";
            for (Room room : Emulator.getGameEnvironment().getRoomManager().getActiveRooms()) {
                if (room == null || room.getUserCount() == 0)
                    continue;

                roomsName += room.getName() + "\n";
                roomsUsers += room.getCurrentHabbos().size() + "\n";
            }
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Quartos Ativos")
                    .addField("Nome", roomsName, true)
                    .addField("Usuários", roomsUsers, true)
                    .setTimestamp(Instant.now());

            message.getChannel().sendMessage(builder.build()).queue();
            /*message.getChannel().sendMessage(Sirius.getTexts().getValue("sirius.bot.cmd_rooms.sucess")
                    .replace("%rooms%", String.valueOf(Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size()))).queue();*/
        } else {
            message.getChannel().sendMessage(Sirius.getTexts().getValue("sirius.bot.cmd_rooms.no_rooms")).queue();
        }
        return true;
    }
}