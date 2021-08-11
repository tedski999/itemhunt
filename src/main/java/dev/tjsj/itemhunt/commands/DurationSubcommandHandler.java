package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class DurationSubcommandHandler implements SubcommandHandler {
	public String name() { return "duration"; }
	public String usage() { return "/itemhunt duration <hours?> <minutes?> <seconds?>"; }
	public String help() { return "View or change the duration of the item hunt."; }

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
			return;
		}
		if (args.length > 3) {
			sender.sendMessage(ChatColor.RED + "Usage: " + usage());
			return;
		}

		// Set a new game duration if args are provided
		if (args.length != 0) {
			int duration = 0;

			// Parse ints from args and convert from h:m:s to seconds
			try {
				duration = convertHMSToSeconds(
					(args.length > 0) ? Integer.parseInt(args[0]) : 0,
					(args.length > 1) ? Integer.parseInt(args[1]) : 0,
					(args.length > 2) ? Integer.parseInt(args[2]) : 0
				);
			} catch (NumberFormatException err) {
				sender.sendMessage(ChatColor.RED + "Those don't look like numbers!");
				return;
			}

			if (duration < 0) {
				sender.sendMessage(ChatColor.RED + "The duration can't be negative!");
				return;
			}

			ih.getConfig().set("duration", duration);
		}

		// Print the game duration
		int[] hms = convertSecondsToHMS(ih.getConfig().getInt("duration"));
		sender.sendMessage(String.format("Game duration set to %02d:%02d:%02d", hms[0], hms[1], hms[2]));
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

	// Convert { hours, minutes, seconds } to seconds
	private static int convertHMSToSeconds(int hours, int minutes, int seconds) {
		return hours * 60 * 60 + minutes * 60 + seconds;
	}
}
