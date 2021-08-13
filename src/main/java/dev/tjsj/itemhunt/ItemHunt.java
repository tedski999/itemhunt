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

	public void startGame() throws IllegalStateException {
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
		secondsRemaining = 10; // TODO: from argument
		gameTask = new BukkitRunnable() {
			@Override
			public void run() {
				countSecond();
			}
		};
		gameTask.runTaskTimerAsynchronously(this, 0L, 20L);
	}

	public void requestTeam(String username, String teamname) {
		if (isGameRunning())
			// TODO: change current game teams? might be easier than i think
			;
		else
			playerTeams.put(username, teamname);

	}

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

		try {
			ih.startGame();
		} catch (IllegalStateException e) {
			sender.sendMessage(e.getMessage());
			return false;
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
		if (args.length != 1)
			return false;
		if (sender instanceof Player)
			ih.requestTeam(((Player) sender).getName(), args[0]);
		else
			sender.sendMessage("Only players can run this command");
		return true;
	}
}
