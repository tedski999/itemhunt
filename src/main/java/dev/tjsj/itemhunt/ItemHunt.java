package dev.tjsj.itemhunt;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.Block;

public class ItemHunt extends JavaPlugin implements Listener {
	private BukkitRunnable gameTask;
	private int secondsRemaining;
	private Map<String, String> playerTeams = new HashMap<>();
	private Map<String, List<String>> teamPlayers = new HashMap<>();
	private Map<String, Integer> teamScores = new HashMap<>();

	// Setup the plugin after it has been enabled
	@Override
	public void onEnable() {

		// Register commands
		getCommand("ihstart").setExecutor(new StartCommand(this));
		getCommand("ihteam").setExecutor(new TeamCommand(this));

		// TODO: Register events
	}

	// Attempt to start the game of a certain duration
	public void startGame(int duration) throws IllegalStateException {
		if (isGameRunning())
			throw new IllegalStateException("Game already running");

		// Initialize teams
		for (Player player : getServer().getOnlinePlayers()) {
			String playerName = player.getName();
			String teamName = playerName; // Default team name to the players name
			if (playerTeams.containsKey(playerName)) // Change the requested team name if the player requested one
				teamName = playerTeams.get(playerName);
			if (!teamPlayers.containsKey(teamName)) { // Create a new team if it doesn't exist
				teamPlayers.put(teamName, new ArrayList<>()); // New list of team members
				teamScores.put(teamName, 0); // New team score is set to 0
			}
			teamPlayers.get(teamName).add(player.getName()); // Add player to requested team list of members
		}

		// Start the game
		secondsRemaining = duration;
		gameTask = new BukkitRunnable() {
			@Override
			public void run() {
				countSecond();
			}
		};
		gameTask.runTaskTimerAsynchronously(this, 0L, 20L);
	}

	// Change a players team
	public void requestTeam(String username, String teamname) {
		if (isGameRunning())
			// TODO: change current game teams? might be easier than i think
			;
		else
			playerTeams.put(username, teamname);

	}

	// Check if the async task is running
	private boolean isGameRunning() {
		return (gameTask != null && !gameTask.isCancelled());
	}

	// Executed as an async Bukkit task to count seconds with minimal disturbance from server lag.
	// Decrements the game countdown, updates the scoreboard and ends the game appropriately.
	private void countSecond() {
		if (--secondsRemaining <= 0) {
			gameTask.cancel();
			gameTask = null;
		}

		// TODO: update scoreboard
	}
}

// ihstart <duration in seconds>
class StartCommand implements CommandExecutor {
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
			if (duration > 0)
				ih.startGame(duration);
			else
				sender.sendMessage(ChatColor.RED + "The duration has to be greater than 0");
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "That doesn't look like a number");
		} catch (IllegalStateException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}

		return true;
	}
}

// ihteam <team name>
class TeamCommand implements CommandExecutor {
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

		// Change players team
		ih.requestTeam(player.getName(), args[0]);
		return true;
	}
}

// ihbox
class BoxCommand implements CommandExecutor {
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

		player.sendMessage(ChatColor.GREEN + "TODO");
		return true;
	}
}
