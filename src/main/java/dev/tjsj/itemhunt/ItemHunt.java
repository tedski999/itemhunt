package dev.tjsj.itemhunt;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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
	private Map<Material, Integer> itemRewards = new HashMap<>();

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

		// Add items to the itemRewards map

		int startReward = 10;

		// Plains
		itemRewards.put(Material.DIRT, startReward);
		itemRewards.put(Material.SUNFLOWER, startReward);
		itemRewards.put(Material.POPPY, startReward);
		itemRewards.put(Material.RABBIT_FOOT, startReward);
		// Forests
		itemRewards.put(Material.BEEHIVE, startReward);
		itemRewards.put(Material.TOTEM_OF_UNDYING, startReward);
		// Jungles
		itemRewards.put(Material.BAMBOO, startReward);
		itemRewards.put(Material.COCOA_BEANS, startReward);
		// Mountains
		itemRewards.put(Material.EMERALD, startReward);
		// Deserts
		itemRewards.put(Material.CACTUS, startReward);
		itemRewards.put(Material.DEAD_BUSH, startReward);
		itemRewards.put(Material.SANDSTONE, startReward);
		// Taiga
		itemRewards.put(Material.MYCELIUM, startReward);
		itemRewards.put(Material.SWEET_BERRIES, startReward);
		// Ice Spikes
		itemRewards.put(Material.PACKED_ICE, startReward);
		itemRewards.put(Material.BLUE_ICE, startReward);
		// Swamps
		itemRewards.put(Material.SLIME_BALL, startReward);
		itemRewards.put(Material.SLIME_BLOCK, startReward);
		// Oceans
		itemRewards.put(Material.COOKED_SALMON, startReward);
		itemRewards.put(Material.COOKED_COD, startReward);
		itemRewards.put(Material.TURTLE_EGG, startReward);
		itemRewards.put(Material.TURTLE_HELMET, startReward);
		itemRewards.put(Material.HEART_OF_THE_SEA, startReward);
		itemRewards.put(Material.TRIDENT, startReward);
		itemRewards.put(Material.AXOLOTL_BUCKET, startReward);
		itemRewards.put(Material.SUGAR_CANE, startReward);
		itemRewards.put(Material.SPONGE, startReward);
		itemRewards.put(Material.SEA_PICKLE, startReward);
		itemRewards.put(Material.SEA_LANTERN, startReward);
		itemRewards.put(Material.PRISMARINE_SHARD, startReward);
		// Underground
		itemRewards.put(Material.GLOW_BERRIES, startReward);
		itemRewards.put(Material.CALCITE, startReward);
		itemRewards.put(Material.AMETHYST_SHARD, startReward);
		itemRewards.put(Material.COBWEB, startReward);
		itemRewards.put(Material.OBSIDIAN, startReward);
		itemRewards.put(Material.CRYING_OBSIDIAN, startReward);
		itemRewards.put(Material.DIAMOND, startReward);
		itemRewards.put(Material.DIAMOND_ORE, startReward);
		itemRewards.put(Material.DEEPSLATE, startReward);
		itemRewards.put(Material.MINECART, startReward);
		// Nether
		itemRewards.put(Material.SOUL_SAND, startReward);
		itemRewards.put(Material.SOUL_SOIL, startReward);
		itemRewards.put(Material.GLOWSTONE, startReward);
		itemRewards.put(Material.ANCIENT_DEBRIS, startReward);
		itemRewards.put(Material.NETHERITE_SCRAP, startReward);
		itemRewards.put(Material.NETHERITE_INGOT, startReward);
		itemRewards.put(Material.NETHERITE_BLOCK, startReward);
		itemRewards.put(Material.BASALT, startReward);
		itemRewards.put(Material.BLACKSTONE, startReward);
		itemRewards.put(Material.NETHER_BRICKS, startReward);
		itemRewards.put(Material.RED_NETHER_BRICKS, startReward);
		itemRewards.put(Material.NETHER_WART, startReward);
		// The End
		itemRewards.put(Material.ENDER_EYE, startReward);
		itemRewards.put(Material.ENDER_CHEST, startReward);
		itemRewards.put(Material.END_ROD, startReward);
		itemRewards.put(Material.DRAGON_EGG, startReward);
		itemRewards.put(Material.ELYTRA, startReward);
		itemRewards.put(Material.SHULKER_BOX, startReward);
		itemRewards.put(Material.CHORUS_FRUIT, startReward);
		// Misc
		itemRewards.put(Material.COOKED_BEEF, startReward);
		itemRewards.put(Material.BONE_BLOCK, startReward);
		itemRewards.put(Material.WITHER_ROSE, startReward);
		itemRewards.put(Material.FLOWER_POT, startReward);
		itemRewards.put(Material.BRICKS, startReward);
		itemRewards.put(Material.TNT, startReward);
		itemRewards.put(Material.CAKE, startReward);
		itemRewards.put(Material.GLISTERING_MELON_SLICE, startReward);
		itemRewards.put(Material.GOLDEN_APPLE, startReward);
		itemRewards.put(Material.ENCHANTED_GOLDEN_APPLE, startReward);
		itemRewards.put(Material.FIREWORK_ROCKET, startReward);
		itemRewards.put(Material.STICKY_PISTON, startReward);
		itemRewards.put(Material.CARROT_ON_A_STICK, startReward);
		itemRewards.put(Material.POISONOUS_POTATO, startReward);
		itemRewards.put(Material.GOLDEN_CARROT, startReward);
		itemRewards.put(Material.PUMPKIN_PIE, startReward);
		itemRewards.put(Material.PINK_WOOL, startReward);
		itemRewards.put(Material.SADDLE, startReward);
		itemRewards.put(Material.CRIMSON_HYPHAE, startReward);
		itemRewards.put(Material.GOLD_BLOCK, startReward);
		itemRewards.put(Material.DIAMOND_BLOCK, startReward);
		itemRewards.put(Material.QUARTZ_BLOCK, startReward);
		itemRewards.put(Material.LAVA_BUCKET, startReward);
		itemRewards.put(Material.DIAMOND_HORSE_ARMOR, startReward);
		itemRewards.put(Material.SPECTRAL_ARROW, startReward);

		// TODO: reconds?
		//itemRewards.put(Material.MUSIC_DISK, startReward);

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

		// NOTE: players can dc and reconnect after the game starts to keep items
		// NOTE: players can place items in chest before the game starts to keep items
		// temp solution: adventure mode till game start

		// Clear players inventories and announce start
		Set<Player> onlinePlayersInHunt = getServer().getOnlinePlayers().stream()
			.filter(p -> playerTeams.containsKey(p.getName()))
			.collect(Collectors.toSet());
		for (Player player : onlinePlayersInHunt) {
			player.getInventory().clear();
			player.sendMessage(ChatColor.YELLOW + "The item hunt has begun!");
		}

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

		// Assign the players scoreboard to ours
		player.setScoreboard(board);

		// Don't do anything if (re)joining the same team as before,
		// otherwise leave the old team
		if (oldTeamName != null) {
			if (oldTeamName.equals(teamName))
				return;
			leaveTeam(player, teamName);
		}

		// Initialize new team if it doesn't exist yet
		playerTeams.put(playerName, teamName);
		if (!teamPlayers.containsKey(teamName)) {
			teamPlayers.put(teamName, new ArrayList<>());
			teamItems.put(teamName, new HashSet<>());
			setTeamScore(teamName, 0);
		}

		// Add player to team list of members
		teamPlayers.get(teamName).add(playerName);
	}

	// Remove player from team
	public void leaveTeam(Player player, String teamName) {
		teamPlayers.get(teamName).remove(player.getName());
		player.sendMessage(ChatColor.YELLOW + "Left item hunt team " + teamName + ".");

		// Clear previous team if now empty
		if (teamPlayers.get(teamName).size() == 0) {
			teamScores.remove(teamName);
			teamPlayers.remove(teamName);
			createScoreboard(); // We need to recreate the scoreboard to remove entries...
		}
	}

	// TODO: proper error handling
	public void depositItem(String playerName, Material itemType, Inventory inv) throws Exception {
		if (!isGameRunning())
			throw new Exception("Item hunt not yet started!");

		String teamName = playerTeams.get(playerName);

		if (teamItems.get(teamName).contains(itemType))
			throw new Exception("You've already collected that!");

		if (!itemRewards.containsKey(itemType))
			throw new Exception("Not a collectable item!");

		// Reward team and update reward
		int reward = itemRewards.get(itemType);
		addTeamScore(teamName, reward);
		teamItems.get(teamName).add(itemType);
		itemRewards.put(itemType, (int) Math.ceil((double) reward / 2));

		// Announce collection
		String itemName = itemType.toString().replace("_", " ").toLowerCase();
		String pointsSurfix = (reward == 1) ? "s" : "";
		Set<Player> onlinePlayersInHunt = getServer().getOnlinePlayers().stream()
			.filter(p -> playerTeams.containsKey(p.getName()))
			.collect(Collectors.toSet());
		for (Player player : onlinePlayersInHunt)
			player.sendMessage(
				ChatColor.YELLOW + String.format(
					"%s has collected the %s item, gaining team %s %d point%s!",
					playerName, itemName, teamName, reward, pointsSurfix));

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
		if (playerTeams.containsKey(playerName)) {
			String teamName = playerTeams.get(playerName);
			requestTeam(player, teamName);
			player.sendMessage(ChatColor.BLUE + "Welcome back! You have automatically rejoined the item hunt team " + teamName + ".");
		}
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
