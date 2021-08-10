package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class BoxSubcommandHandler implements SubcommandHandler {

	public String name() { return "box"; }
	public String usage() { return "/itemhunt box"; }
	public String help() { return "Designate an item deposit box for the ItemHunt game. You must be OP to run this command."; }

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
			return;
		}
		sender.sendMessage(ChatColor.YELLOW + "TODO!");
	}
}
