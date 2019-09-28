package me.RockinChaos.itemjoin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.utils.interfaces.Interface;

public class MultiForm implements Listener {
  	
//  ============================================== //
//          Handlers for virtualInventory          //
//  ============================================== //
  	private Interface expiredInventory;

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder instanceof Interface) {
			((Interface) holder).onClick(event);
    		this.expiredInventory = ((Interface) holder);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (this.expiredInventory != null && this.expiredInventory.chatPending()) {
			this.expiredInventory.onChat(event);
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (ConfigHandler.getItemCreator().modifyMenu((Player)event.getPlayer())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!ConfigHandler.getItemCreator().isOpen((Player)event.getPlayer())) {
						ConfigHandler.getItemCreator().setModifyMenu(false, (Player)event.getPlayer());
						for (ItemMap itemMap : ItemUtilities.getItems()) {
							itemMap.getAnimationHandler().get(event.getPlayer()).setMenu(false);
						}
					}
				}
			}.runTaskLater(ItemJoin.getInstance(), 40L);
		}
	}

//  ===============================================================================

}