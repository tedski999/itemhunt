package dev.tjsj.itemhunt;

import org.bukkit.ChatColor;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;

public class DurationSetCommand extends CommandAPICommand {

	public DurationSetCommand(ItemHunt ih) {
		super("duration");
		withArguments(
			new IntegerArgument("hours"),
			new IntegerArgument("minutes"),
			new IntegerArgument("seconds"));
		withPermission(CommandPermission.OP);
		executes((sender, args) -> {

			// Convert arguments in hms to seconds
			int duration = convertHMSToSeconds((Integer) args[0], (Integer) args[1], (Integer) args[2]);
			if (duration < 0) {
				sender.sendMessage(ChatColor.RED + "The game duration can't be negative!");
				return;
			}

			// Save to config and send feedback
			ih.getConfig().set("duration", duration);
			int[] hms = convertSecondsToHMS(duration);
			sender.sendMessage(String.format("Game duration set to %02d:%02d:%02d", hms[0], hms[1], hms[2]));
		});
	}

	// Convert seconds to { hours, minutes, seconds }
	// TODO: this method is common to DurarionGetCommand
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

	// Convert { hours, minutes, seconds } to seconds
	private static int convertHMSToSeconds(int hours, int minutes, int seconds) {
		return hours * 60 * 60 + minutes * 60 + seconds;
	}
}
