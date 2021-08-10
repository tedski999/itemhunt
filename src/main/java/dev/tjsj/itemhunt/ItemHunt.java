package dev.tjsj.itemhunt;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ItemHunt extends JavaPlugin implements CommandExecutor {
	private List<SubcommandHandler> subcommandHandlers;
	private Map<String, String> playerTeams; // Map usernames to team names
	private Map<String, Integer> teamScores; // Map team names to scores
	private BukkitRunnable gameTask;
	private int secondsRemaining;

	public ItemHunt() {
		playerTeams = new HashMap<String, String>();

		// Create the subcommand handlers
		subcommandHandlers = new ArrayList<>();
		subcommandHandlers.add(new BoxSubcommandHandler());
		subcommandHandlers.add(new JoinSubcommandHandler());
		subcommandHandlers.add(new KickSubcommandHandler());
		subcommandHandlers.add(new LeaveSubcommandHandler());
		subcommandHandlers.add(new StartSubcommandHandler());
		subcommandHandlers.add(new StopSubcommandHandler());
	}

	// Setup the plugin after it has been enabled
	@Override
	public void onEnable() {

		// TODO: Load config
		this.saveDefaultConfig();

		// Register event listeners and base command handler
		getServer().getPluginManager().registerEvents(new ItemDepositListener(this), this);
		getServer().getPluginManager().registerEvents(new DepositBoxListener(this), this);
		getCommand("itemhunt").setExecutor(this);
	}

	// Clean up the plugin after it has been disabled
	@Override
	public void onDisable() {

	}

	// Distribute incoming commands to appropriate subcommand handlers
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// Print command usage if no arguments are provided
		if (args.length < 1)
			return false;
		String subcommand = args[0];

		// Execute the first subcommand handler with a name equal to the first argument
		for (SubcommandHandler handler : subcommandHandlers) {
			if (handler.name().equalsIgnoreCase(subcommand)) {
				handler.execute(this, sender, Arrays.copyOfRange(args, 1, args.length));
				return true;
			}
		}

		// Print command usage if no subcommands are found
		return false;
	}

	// === Game Control Methods === //

	// Is the game currently running?
	// Determined by if gameTask exists and is not yet cancelled
	public boolean isGameRunning() {
		return (gameTask != null && !gameTask.isCancelled());
	}

	// Start the game if possible
	public void startGame() throws IllegalStateException {
		if (isGameRunning())
			throw new IllegalStateException("The item hunt has already started!");

		// Create our teams from the players who joined
		teamScores = new HashMap<String, Integer>();
		// TODO: add each unique team name from playerTeams

		secondsRemaining = 10; // TODO: from config

		// Create a new runnable we will use for our async Bukkit task to count seconds
		// countSecond() will be run asynchronously every 20 ticks (1 second)
		gameTask = new BukkitRunnable() { public void run() { countSecond(); }};
		gameTask.runTaskTimerAsynchronously(this, 0L, 20L);

		// TODO: announce start of game (with countdown?)
	}

	// Stop the game if possible
	public void stopGame() throws IllegalStateException {
		if (!isGameRunning())
			throw new IllegalStateException("The item hunt hasn't yet started!");

		// Stop the async task
		gameTask.cancel();
		gameTask = null;

		// TODO: announce end of game (with results?)
	}

	// === Team Modifying Methods === //

	// Return a list of usernames mapped to their team name
	public Map<String, String> getPlayerTeams() {
		return playerTeams;
	}

	// Return a list of team names mapped to their score
	public Map<String, Integer> getTeamScores() {
		return teamScores;
	}

	// Add a username to a team if possible
	public void addPlayer(String username, String teamname) throws IllegalStateException {
		if (isGameRunning())
			throw new IllegalStateException("The item hunt has already started!");
		playerTeams.put(username, teamname);
	}

	// Remove a username from a team if possible
	public void removePlayer(String username) throws IllegalStateException {
		if (!playerTeams.containsKey(username))
			throw new IllegalStateException("No '" + username + "' has joined the item hunt!");

		playerTeams.remove(username);
		if (isGameRunning()) {
			// TODO: modify currently running game
		}
	}

	// === Private Implementation Methods === //

	// Executed as an async Bukkit task to count seconds with minimal disturbance from server lag.
	// Decrements the game countdown, updates the scoreboard and ends the game appropriately.
	private void countSecond() {
		if (--secondsRemaining <= 0)
			stopGame();
		// TODO: update scoreboard
	}
}
