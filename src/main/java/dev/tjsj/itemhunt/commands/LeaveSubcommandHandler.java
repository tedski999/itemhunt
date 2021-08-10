package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class LeaveSubcommandHandler extends SubcommandHandler {
	public String name = "leave";
	public String usage = "/itemhunt leave";
	public String help = "Leave the ItemHunt game.";

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Sorry, but only players can run this type of command!");
			return;
		}

		Player player = (Player) sender;
		String username = player.getName();
		try {
			ih.removePlayer(username);
			sender.sendMessage(ChatColor.GREEN + "You have left the item hunt.");
		} catch (IllegalStateException err) {
			sender.sendMessage(ChatColor.RED + "You haven't yet joined the item hunt!");
		}
	}
}
