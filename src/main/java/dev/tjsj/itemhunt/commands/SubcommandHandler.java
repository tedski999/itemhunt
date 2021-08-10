package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;

public interface SubcommandHandler {
	String name();
	String usage();
	String help();
	void execute(ItemHunt ih, CommandSender sender, String[] args);
}
