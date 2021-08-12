package dev.tjsj.itemhunt.commands;
import dev.jorel.commandapi.CommandAPICommand;
import dev.tjsj.itemhunt.ItemHunt;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class LeaveCommand extends CommandAPICommand {

	public LeaveCommand(ItemHunt ih)
	{
		super("leave");

		executesPlayer((sender, args) -> {
			Player player = (Player) sender;
			String username = player.getName();
			try {
				ih.removePlayer(username);
				sender.sendMessage(ChatColor.GREEN + "You have left the item hunt.");
			} catch (IllegalStateException err) {
				sender.sendMessage(ChatColor.RED + "You haven't yet joined the item hunt!");
			}
		});
	}
}

