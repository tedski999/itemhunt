package dev.tjsj.itemhunt;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.inventory.InventoryHolder;

// ihstart <duration in seconds>
class StartCommand implements CommandExecutor, TabCompleter {
	private ItemHunt ih;

	public StartCommand(ItemHunt plugin) {
		ih = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// One arg required
		if (args.length != 1)
			return false;

		// Attempt to parse the arg and start the game
		try {
			int duration = Integer.parseInt(args[0]);
			if (duration <= 0) {
				sender.sendMessage(ChatColor.RED + "The duration has to be greater than 0");
				return true;
			}

			ih.startGame(duration);
			sender.sendMessage(ChatColor.GREEN + "Started item hunt!");
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "That doesn't look like a number");
		} catch (IllegalStateException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		String oneHour = Integer.toString(1 * 60 * 60);
		String twoHoursThirtyMins = Integer.toString(1 * 60 * 60 + 30 * 60);
		String fourHours = Integer.toString(4 * 60 * 60);
		String twentyFourHours = Integer.toString(24 * 60 * 60);
		if (args.length == 1)
			return List.of(oneHour, twoHoursThirtyMins, fourHours, twentyFourHours);
		else
			return List.of();
	}
}

// ihteam <team name>
class TeamCommand implements CommandExecutor, TabCompleter {
	private ItemHunt ih;

	public TeamCommand(ItemHunt plugin) {
		ih = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// One arg required
		if (args.length != 1)
			return false;

		// Check the sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can run this command");
			return true;
		}
		Player player = (Player) sender;
		if(args[0].length() > 16) {
			sender.sendMessage(ChatColor.RED + "Fuk u under 16 characters midgetman");
			return true;
		}

		// Change players team
		ih.requestTeam(player, args[0]);
		sender.sendMessage(ChatColor.GREEN + "You have joined the item hunt team '" + args[0] + "'");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1)
			return new ArrayList<String>(ih.getTeamNames());
		else
			return List.of();
	}
}

// ihbox
class BoxCommand implements CommandExecutor, TabCompleter {
	private ItemHunt ih;

	public BoxCommand(ItemHunt plugin) {
		ih = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// No args required
		if (args.length != 0)
			return false;

		// Check the sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can run this command");
			return true;
		}
		Player player = (Player) sender;

		// Get the block they're looking at
		Block block = player.getTargetBlock(null, 200);
		if (block == null || !(block.getState() instanceof InventoryHolder)) {
			player.sendMessage(ChatColor.RED + "Please point at a chest to designate as the item box before running this command");
			return true;
		}

		ih.setBox(block);
		player.sendMessage(ChatColor.GREEN + "Deposit box set");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return List.of();
	}
}

// ihitems
class ItemsCommand implements CommandExecutor, TabCompleter {
	private ItemHunt ih;

	public ItemsCommand(ItemHunt plugin) {
		ih = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// No args required
		if (args.length != 0)
			return false;

		// Check the sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can run this command");
			return true;
		}
		Player player = (Player) sender;

		// Check the game has started
		if (!ih.isGameRunning()) {
			sender.sendMessage(ChatColor.RED + "The catalog can only be viewed once the item hunt has started!");
			return true;
		}

		ih.openItemCatalog(player, 0);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return List.of();
	}
}
