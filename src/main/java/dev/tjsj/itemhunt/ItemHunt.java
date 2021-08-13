package dev.tjsj.itemhunt;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemHunt extends JavaPlugin implements Listener {
	private BukkitRunnable gameTask;
	private int secondsRemaining;

	// Setup the plugin after it has been enabled
	@Override
	public void onEnable() {

		// Register commands
		getCommand("ihstart").setExecutor(new StartCommand(this));
		getCommand("ihteam").setExecutor(new TeamCommand(this));

		// TODO: Register events
	}

	public void startGame() throws IllegalStateException {

		// Fail if game is already started
		if (gameTask != null && !gameTask.isCancelled())
			throw new IllegalStateException("Game already running");

		// TODO: initialise teams
		// if player has not joined a team, their team is just players name

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
		// TODO set players team
		return false;
	}
}
