package dev.tjsj.itemhunt;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class ItemDepositListener implements Listener {
	private ItemHunt ih;

	public ItemDepositListener(ItemHunt ih) {
		this.ih = ih;
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent e) {
		// if moving to deposit chest
		//   take only 1 item
		//   delete the item
		//   add to scoreboard
		ih.getLogger().info("item moved");
	}
}
