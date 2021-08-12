package dev.tjsj.itemhunt.commands;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.tjsj.itemhunt.ItemHunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class StartCommand extends CommandAPICommand {

	public StartCommand(ItemHunt ih)
	{
		super("start");
		withPermission(CommandPermission.OP);

		executes((sender, args) -> {
			try {
				ih.startGame();
				sender.sendMessage(ChatColor.GREEN + "Starting item hunt...");
			} catch (IllegalStateException err) {
				sender.sendMessage(ChatColor.RED + err.getMessage());
			}
		});
	}
}
