package dev.tjsj.itemhunt;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.*;
import org.bukkit.Material;

// TODO: feedback to players using commands

public class ItemHunt extends JavaPlugin {
	private BukkitRunnable gameTask;
	private int secondsRemaining;
	private Scoreboard board;
	private Map<String, String> playerTeams = new HashMap<>();
	private Map<String, List<String>> teamPlayers = new HashMap<>();
	private Map<String, Integer> teamScores = new HashMap<>();
	private Map<String, Set<Material>> teamItems = new HashMap<>();

	// Setup the plugin after it has been enabled
	@Override
	public void onEnable() {

		// Register commands
		getCommand("ihstart").setExecutor(new StartCommand(this));
		getCommand("ihteam").setExecutor(new TeamCommand(this));
		getCommand("ihbox").setExecutor(new BoxCommand(this));

		// Register events
		getServer().getPluginManager().registerEvents(new ItemHuntEventHandler(this), this);
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
			else
				playerTeams.put(playerName, playerName);
			if (!teamPlayers.containsKey(teamName)) { // Create a new team if it doesn't exist
				teamPlayers.put(teamName, new ArrayList<>()); // New list of team members
				teamScores.put(teamName, 0); // New team score is set to 0
				teamItems.put(teamName, new HashSet<>()); // New team collected items set is empty
			}
			teamPlayers.get(teamName).add(player.getName()); // Add player to requested team list of members
		}

		// Create scoreboard
		board = getServer().getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("ItemHunt", "dummy", "Loading...");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		for(Player online : getServer().getOnlinePlayers())
			online.setScoreboard(board);
		for(Entry<String, Integer> team : teamScores.entrySet())
			obj.getScore(team.getKey()).setScore(team.getValue());

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

	// TODO: proper error handling
	public void depositItem(String playerName, Material itemType, Inventory inv) throws Exception {
		if (!isGameRunning())
			throw new Exception("game not runnignj");

		String teamName = playerTeams.get(playerName);

		if (teamItems.get(teamName).contains(itemType))
			throw new Exception("thing already collectd");

		// TODO: check if a valid item is being deposited, get reward and set new value
		addTeamScore(teamName, 10);
		teamItems.get(teamName).add(itemType);

		// Clear the box on the next tick
		BukkitRunnable clearTask = new BukkitRunnable() {
			@Override
			public void run() {
				inv.clear();
			}
		};
		clearTask.runTask(this);
	}

	// Add score to teams current score
	public void addTeamScore(String teamName, int reward) {
		setTeamScore(teamName, teamScores.get(teamName) + reward);
	}

	// Set the score for a team
	public void setTeamScore(String teamName, int newScore) {
		teamScores.put(teamName, newScore);
		board.getObjective("ItemHunt").getScore(teamName).setScore(newScore);
	}

	// Check if the async task is running
	public boolean isGameRunning() {
		return (gameTask != null && !gameTask.isCancelled());
	}

	// Executed as an async Bukkit task to count seconds with minimal disturbance from server lag.
	// Decrements the game countdown, updates the scoreboard and ends the game appropriately.
	private void countSecond() {
		if (--secondsRemaining <= 0) {
			gameTask.cancel();
			gameTask = null;
		}
		board.getObjective("ItemHunt").setDisplayName(convertSecondsToHMS(secondsRemaining));
	}

	// Convert the number of seconds to hours : minutes : seconds
	private static String convertSecondsToHMS(int total) {
		int hours = total / 3600;
		int remainder = total - hours * 3600;
		int minutes = remainder / 60;
		return String.format("%02d:%02d:%02d", hours, minutes, remainder - minutes * 60);
	}
}
