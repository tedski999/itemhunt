package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class StartSubcommandHandler extends SubcommandHandler {
	public String name = "start";
	public String usage = "/itemhunt start";
	public String help = "Start the ItemHunt game. You must be OP to run this command.";

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
			return;
		}

		try {
			ih.startGame();
			sender.sendMessage(ChatColor.GREEN + "Starting item hunt...");
		} catch (IllegalStateException err) {
			sender.sendMessage(ChatColor.RED + err.getMessage());
		}
	}
}
