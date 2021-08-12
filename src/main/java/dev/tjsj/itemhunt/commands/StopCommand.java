package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class StopCommand {
	public String name() { return "stop"; }
	public String usage() { return "/itemhunt stop"; }
	public String help() { return "Stop the ItemHunt game early. You must be OP to run this command."; }

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
			return;
		}
		if (args.length != 0) {
			sender.sendMessage(ChatColor.RED + "Usage: " + usage());
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
