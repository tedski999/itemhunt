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
import org.bukkit.entity.ArmorStand;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.*;
import org.bukkit.block.Block;
import org.bukkit.Material;

// TODO: feedback to players using commands

public class ItemHunt extends JavaPlugin {
	private BukkitRunnable gameTask;
	private int secondsRemaining;
	private Block box;
	private ArmorStand boxLabel;
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

		// Create scoreboard
		createScoreboard();
	}

	// Clean up anything temporary made by the blugin
	@Override
	public void onDisable() {
		if (boxLabel != null)
			boxLabel.remove();
		if (board != null)
			for (Player player : getServer().getOnlinePlayers())
				if (player.getScoreboard() == board)
					player.setScoreboard(getServer().getScoreboardManager().getNewScoreboard());
	}

	// Attempt to start the game of a certain duration
	public void startGame(int duration) throws IllegalStateException {
		if (isGameRunning())
			throw new IllegalStateException("Game already running");
		if (box == null)
			throw new IllegalStateException("No deposit box set with /ihbox");
		if (playerTeams.isEmpty())
			throw new IllegalStateException("No teams made yet");

		// Reset scores
		for (String teamName : teamScores.keySet())
			setTeamScore(teamName, 0);

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
	public void requestTeam(Player player, String teamName) {
		String playerName = player.getName();
		String oldTeamName = playerTeams.get(playerName);

		// Don't do anything if (re)joining the same team as before
		if (oldTeamName != null && oldTeamName.equals(teamName))
			return;

		// Remove player from previous team
		if (oldTeamName != null)
			teamPlayers.get(oldTeamName).remove(playerName);

		// Initialize new team if it doesn't exist yet
		playerTeams.put(playerName, teamName);
		if (!teamPlayers.containsKey(teamName)) {
			teamPlayers.put(teamName, new ArrayList<>());
			teamItems.put(teamName, new HashSet<>());
			setTeamScore(teamName, 0);
		}

		// Add player to team list of members
		teamPlayers.get(teamName).add(playerName);

		// Clear previous team if now empty
		if (oldTeamName != null && teamPlayers.get(oldTeamName).size() == 0) {
			teamScores.remove(oldTeamName);
			teamPlayers.remove(oldTeamName);
			createScoreboard(); // We need to recreate the scoreboard to remove entries...
		}

		// Assign the players scoreboard to ours
		player.setScoreboard(board);
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

	// Set the new deposit box
	public void setBox(Block block) {
		if (boxLabel != null)
			boxLabel.remove();
		box = block;
		boxLabel = (ArmorStand) box.getWorld().spawn(
			box.getLocation().add(0.5, 1.0, 0.5),
			ArmorStand.class);
		boxLabel.setVisible(false);
		boxLabel.setGravity(false);
		boxLabel.setMarker(true);
		boxLabel.setSmall(true);
		boxLabel.setCustomName("The Box™");
		boxLabel.setCustomNameVisible(true);
	}

	// Get the deposit box
	public Block getBox() {
		return box;
	}

	public void checkForPlayerRejoin(Player player) {
		String playerName = player.getName();
		if (playerTeams.containsKey(playerName))
			requestTeam(player, playerTeams.get(playerName));
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

			// Find winning team
			String winningTeam = null;
			int highestScore = 0;
			for (Entry<String, Integer> entry : teamScores.entrySet()) {
				if (entry.getValue() > highestScore) {
					highestScore = entry.getValue();
					winningTeam = entry.getKey();
				} else if (entry.getValue() == highestScore) {
					winningTeam = null;
				}
			}

			// Display winner
			if (winningTeam != null)
				board.getObjective("ItemHunt").setDisplayName(winningTeam + " wins!");
			else
				board.getObjective("ItemHunt").setDisplayName("Draw!");
		} else {
			board.getObjective("ItemHunt").setDisplayName(convertSecondsToHMS(secondsRemaining));
		}
	}

	// Convert the number of seconds to hours : minutes : seconds
	private static String convertSecondsToHMS(int total) {
		int hours = total / 3600;
		int remainder = total - hours * 3600;
		int minutes = remainder / 60;
		return String.format("%02d:%02d:%02d", hours, minutes, remainder - minutes * 60);
	}

	// Create scoreboard
	private void createScoreboard() {

		// Create the new scoreboard
		Scoreboard newBoard = getServer().getScoreboardManager().getNewScoreboard();
		Objective obj = newBoard.registerNewObjective("ItemHunt", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		// Initial display name to show
		if (isGameRunning())
			obj.setDisplayName(convertSecondsToHMS(secondsRemaining));
		else
			obj.setDisplayName("Waiting to start...");

		// Update players scoreboard
		for (Player online : getServer().getOnlinePlayers())
			if (online.getScoreboard() == board)
				online.setScoreboard(newBoard);

		// Update scores
		for (Entry<String, Integer> team : teamScores.entrySet())
			obj.getScore(team.getKey()).setScore(team.getValue());

		board = newBoard;
	}
}
