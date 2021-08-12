package dev.tjsj.itemhunt.commands;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.tjsj.itemhunt.ItemHunt;

import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KickCommand extends CommandAPICommand {

	public KickCommand(ItemHunt ih) {
		super("kick");
		withArguments(new TextArgument(("Player")));
		withPermission(CommandPermission.OP);

		executes((sender, args) ->	{
			try {
				String username = (String) args[0];
				ih.removePlayer(username);
				sender.sendMessage(ChatColor.YELLOW + "Kicked '" + username + "' from the item hunt!");
			} catch (IllegalStateException err) {
				sender.sendMessage(ChatColor.RED + err.getMessage());
			}
		});
	}
}
