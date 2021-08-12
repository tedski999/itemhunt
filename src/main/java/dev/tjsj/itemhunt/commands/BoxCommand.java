package dev.tjsj.itemhunt;

import org.bukkit.ChatColor;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

public class BoxCommand extends CommandAPICommand {
	public BoxCommand(ItemHunt ih) {
		super("box");
		withPermission(CommandPermission.OP);
		executesPlayer((player, args) -> {
			player.sendMessage(ChatColor.YELLOW + "TODO!");
		});
	}
}
