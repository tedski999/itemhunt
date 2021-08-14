package dev.tjsj.itemhunt;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.ChatColor;

class ItemHuntEventHandler implements Listener {
	private ItemHunt ih;

	ItemHuntEventHandler(ItemHunt plugin) {
		ih = plugin;
	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent event) {
		InventoryHolder holder = event.getView().getTopInventory().getHolder();
		if (!ih.isGameRunning() &&
			holder instanceof BlockState &&
			((BlockState) holder).getBlock().equals(ih.getBox()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryDragEvent(InventoryDragEvent event) {

		// Skip if not the box
		InventoryHolder holder = event.getView().getTopInventory().getHolder();
		if (!(holder instanceof BlockState) ||
			!((BlockState) holder).getBlock().equals(ih.getBox()))
			return;

		// Only deal with players
		if (!(event.getView().getBottomInventory().getHolder() instanceof Player))
			return;
		Player player = (Player) event.getView().getBottomInventory().getHolder();

		// Attempt deposit if all items are dragged into top inventory.
		// Cancel event if drag crosses between the two inventories.
		boolean attemptDeposit = true;
		int size = event.getView().getTopInventory().getSize();
		for (int i : event.getRawSlots()) {
			if (i < size)
				event.setCancelled(true);
			else
				attemptDeposit = false;
		}

		// Only attempt deposit if above checks passed
		if (event.isCancelled() && attemptDeposit) {
			ItemStack depositedItems = event.getOldCursor();
			try {
				ih.depositItem(
					player.getName(),
					depositedItems.getType(),
					event.getView().getTopInventory());
				event.setCancelled(false);
				event.setCursor(depositedItems);
				event.getCursor().setAmount(depositedItems.getAmount() - 1);
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {

		// Skip if not the box
		InventoryHolder holder = event.getView().getTopInventory().getHolder();
		if (!(holder instanceof BlockState) ||
			!((BlockState) holder).getBlock().equals(ih.getBox()))
			return;

		// Only deal with players
		if (!(event.getView().getBottomInventory().getHolder() instanceof Player))
			return;
		Player player = (Player) event.getView().getBottomInventory().getHolder();

		// Only attempt to deposit items if items have been deposited
		ItemStack depositedItems = null;
		InventoryAction action = event.getAction();
		if (event.getClickedInventory() == event.getView().getBottomInventory()) {

			// Is the user depositing items with shift-click?
			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				event.setCancelled(true);
				depositedItems = event.getCurrentItem();
			}

		} else if (event.getClickedInventory() == event.getView().getTopInventory()) {
			event.setCancelled(true);

			// Is the user depositing items normally?
			if (action == InventoryAction.SWAP_WITH_CURSOR ||
				action == InventoryAction.PLACE_ONE ||
				action == InventoryAction.PLACE_SOME ||
				action == InventoryAction.PLACE_ALL)
				depositedItems = event.getCursor();
		}

		// Attempt to deposit items if checks passed above
		if (depositedItems != null) {
			try {
				ih.depositItem(
					player.getName(),
					depositedItems.getType(),
					event.getView().getTopInventory());
				depositedItems.setAmount(depositedItems.getAmount() - 1);
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
	}

	@EventHandler
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
		InventoryHolder holder = event.getDestination().getHolder();
		if (holder instanceof BlockState &&
			((BlockState) holder).getBlock().equals(ih.getBox()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getBlock().equals(ih.getBox()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		for (Block block : new ArrayList<Block>(event.blockList()))
			if (block.equals(ih.getBox()))
				event.blockList().remove(block);
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		ih.checkForPlayerRejoin(event.getPlayer());
	}
}
