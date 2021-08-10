package dev.tjsj.itemhunt;

import org.bukkit.command.CommandSender;

public abstract class SubcommandHandler {
	public String name;
	public String usage;
	public String help;
	public abstract void execute(ItemHunt ih, CommandSender sender, String[] args);
}
