package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class JoinSubcommandHandler extends SubcommandHandler {
	public String name = "join";
	public String usage = "/itemhunt join <teamname>";
	public String help = "Join the ItemHunt game. You can join another team or make your own.";

	public void execute(ItemHunt ih, CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Sorry, but only players can run this type of command!");
			return;
		}
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Usage: " + usage);
			return;
		}

		Player player = (Player) sender;
		String username = player.getName();
		String teamname = args[0];
		try {
			ih.addPlayer(username, teamname);
		} catch (IllegalStateException err) {
			sender.sendMessage(ChatColor.RED + err.getMessage());
		}
	}
}
