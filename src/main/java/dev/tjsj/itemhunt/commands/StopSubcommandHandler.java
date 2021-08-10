package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class StopSubcommandHandler extends SubcommandHandler {
	public String name = "stop";
	public String usage = "/itemhunt stop";
	public String help = "Stop the ItemHunt game. You must be OP to run this command.";

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
			return;
		}

		try {
			ih.stopGame();
			sender.sendMessage(ChatColor.GREEN + "Stopping item hunt early...");
		} catch (IllegalStateException err) {
			sender.sendMessage(ChatColor.RED + err.getMessage());
		}
	}
}
