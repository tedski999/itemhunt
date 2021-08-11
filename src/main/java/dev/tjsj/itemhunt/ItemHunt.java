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
import org.bukkit.configuration.file.FileConfiguration;

public class ItemHunt extends JavaPlugin implements CommandExecutor {
    private FileConfiguration config;
	private SubcommandHandler[] subcommandHandlers;
	private BukkitRunnable gameTask;
	private int secondsRemaining;
	private Map<String, Team> players; // Map player usernames to teams
	private Map<String, Team> teams; // Map team names to teams

	public ItemHunt() {
		players = new HashMap<>();
		teams = new HashMap<>();

		// Create the subcommand handlers
		subcommandHandlers = new SubcommandHandler[] {
			new BoxSubcommandHandler(),
			new JoinSubcommandHandler(),
			new KickSubcommandHandler(),
			new LeaveSubcommandHandler(),
			new StartSubcommandHandler(),
			new StopSubcommandHandler()
		};
	}

	// Setup the plugin after it has been enabled
	@Override
	public void onEnable() {

		// Load config from disk
		saveDefaultConfig();
		config = super.getConfig();

		// Register event listeners and base command handler
		getServer().getPluginManager().registerEvents(new ItemDepositListener(this), this);
		getServer().getPluginManager().registerEvents(new DepositBoxListener(this), this);
		getCommand("itemhunt").setExecutor(this);
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

	// === Config Methods === //

	// Return the loaded config
	public FileConfiguration getConfig() {
		return config;
	}

	// Write the config to disk
	public void saveConfig() {
		config.options().copyDefaults(true);
		super.saveConfig();
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

		// Reset team scores
		for (Team team : teams.values())
			team.score = 0;

		// Create a new runnable we will use for our async Bukkit task to count seconds
		// countSecond() will be run asynchronously every 20 ticks (1 second)
		secondsRemaining = config.getInt("duration");
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

	// Return a map of plater usernames to their team
	public Map<String, Team> getPlayers() {
		return players;
	}

	// Return a map of team names to corresponding teams
	public Map<String, Team> getTeams() {
		return teams;
	}

	// Add a username to a team if possible
	public void addPlayer(String username, String teamname) throws IllegalStateException {
		if (isGameRunning())
			throw new IllegalStateException("The item hunt has already started!");

		// Create a new team if it doesn't exist. Add username to the list of members.
		if (!teams.containsKey(teamname))
			teams.put(teamname, new Team(teamname));
		teams.get(teamname).members.add(username);
	}

	// Remove a username from a team if possible
	public void removePlayer(String username) throws IllegalStateException {
		Team team = players.get(username);
		if (team == null)
			throw new IllegalStateException("'" + username + "' is not part of any team!");

		// Remove username from team members and map between username and team
		team.members.remove(username);
		players.remove(username);
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

class Team {
	public String name;
	public int score = 0;
	public List<String> members = new ArrayList<>();
	public Team(String name) { this.name = name; }
}
