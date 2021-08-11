package dev.tjsj.itemhunt;

import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class DepositBoxListener implements Listener {
	private ItemHunt ih;

	public DepositBoxListener(ItemHunt ih) {
		this.ih = ih;
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {

		// Only item hunt players should be allowed to open the deposit box
		e.getInventory();
		if (true) { // TODO: check if this is the deposit box
			String username = e.getPlayer().getName();
			Map<String, Team> players = ih.getPlayers();
			if (!ih.isGameRunning() || !players.containsKey(username))
				e.setCancelled(true);
		}
	}
}
