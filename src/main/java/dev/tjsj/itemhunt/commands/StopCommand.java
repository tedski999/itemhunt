package dev.tjsj.itemhunt.commands;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.tjsj.itemhunt.ItemHunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class StopCommand extends CommandAPICommand {

	public StopCommand(ItemHunt ih)
	{
		super("stop");
		withPermission(CommandPermission.OP);

		executes((sender, args) -> {
			try {
				ih.stopGame();
				sender.sendMessage(ChatColor.GREEN + "Stopping item hunt early...");
			} catch (IllegalStateException err) {
				sender.sendMessage(ChatColor.RED + err.getMessage());
			}
		});
	}
}
