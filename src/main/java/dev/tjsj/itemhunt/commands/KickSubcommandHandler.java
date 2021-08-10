package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class KickSubcommandHandler extends SubcommandHandler {
	public String name = "kick";
	public String usage = "/itemhunt kick <username>";
	public String help = "Kick a player from the ItemHunt game. You must be OP to run this command.";

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
			return;
		}
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Usage: " + usage);
			return;
		}

		try {
			String username = args[0];
			ih.removePlayer(username);
			sender.sendMessage(ChatColor.YELLOW + "Kicked '" + username + "' from the item hunt!");
		} catch (IllegalStateException err) {
			sender.sendMessage(ChatColor.RED + err.getMessage());
		}
	}
}
