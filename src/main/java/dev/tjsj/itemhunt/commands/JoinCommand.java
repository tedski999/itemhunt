package dev.tjsj.itemhunt;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class JoinCommand extends CommandAPICommand {

	public JoinCommand(ItemHunt ih) {
		super("join");
		withArguments(new StringArgument(("Team")));
		withPermission(CommandPermission.OP);

		executes((sender, args) -> {
			Player player = (Player) sender;
			String username = player.getName();
			String teamname = (String) args[0];
			try {
				ih.addPlayer(username, teamname);
			} catch (IllegalStateException err) {
				sender.sendMessage(ChatColor.RED + err.getMessage());
			}
		});
	}
}
