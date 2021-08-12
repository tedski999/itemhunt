package dev.tjsj.itemhunt.commands;
import dev.tjsj.itemhunt.ItemHunt;

import org.bukkit.ChatColor;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

public class DurationGetCommand extends CommandAPICommand {

	public DurationGetCommand(ItemHunt ih) {
		super("duration");
		executes((sender, args) -> {
			int[] hms = convertSecondsToHMS(ih.getConfig().getInt("duration"));
			sender.sendMessage(String.format("Game duration is set to %02d:%02d:%02d", hms[0], hms[1], hms[2]));
		});
	}

	// Convert seconds to { hours, minutes, seconds }
	private static int[] convertSecondsToHMS(int total) {
		int hours = total / 3600;
		int remainder = total - hours * 3600;
		int minutes = remainder / 60;
		return new int[] {
			hours,
			minutes,
			remainder - minutes * 60
		};
	}
}
