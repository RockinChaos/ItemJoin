package me.RockinChaos.itemjoin.giveitems.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.InvClickSurvival;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemAnimation {
	
	private ItemMap itemMap;
	private List<String> dynamicNames;
	private List<List<String>> dynamicLores;
	private boolean isLoreAnimated = false;
	private boolean isNameAnimated = false;
	private boolean stopAnimations = false;
	
	public ItemAnimation(ItemMap item, List<String> dynamicNames, List<List<String>> dynamicLores) {
		this.itemMap = item;
		if (dynamicLores != null) {
			this.dynamicNames = dynamicNames;
			this.isNameAnimated = true;
		}
		if (dynamicLores != null) {
			this.dynamicLores = dynamicLores;
			this.isLoreAnimated = true;
		}
	}
	
	public void openAnimation(Player player) {
		if (isNameAnimated) { nameTasks(player); }
		if (isLoreAnimated) { loreTasks(player); }
	}
	
	public void closeAnimation(Player player) {
		stopAnimations = true;
		ServerHandler.sendDebugMessage("[ANIMATIONS] Successfully closed all animations for the item [" + itemMap.getConfigName()+ "] with the instanced player " + player.getName() + ".");
	}
	
	private void nameTasks(Player player) {
		long ticks = 0;
		Iterator <String> it = this.dynamicNames.iterator();
		while (it.hasNext()) {
			String name = it.next();
			ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(name));
			AnimateTask(true, player, it.hasNext(), name, null, ticks);
		}
	}
	
	private void loreTasks(Player player) {
		long ticks = 0;
		Iterator<List<String>> it = this.dynamicLores.iterator();
		while (it.hasNext()) {
			List<String> lore = it.next();
			ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(lore.get(0)));
			AnimateTask(false, player, it.hasNext(), null, lore, ticks);
		}
	}
	
	private void AnimateTask(final boolean isName, final Player player, final boolean hasNext, final String nameString, final List<String> loreString, final long UpdateDelay) {
		final ItemMap itemMap = this.itemMap;
		new BukkitRunnable() {
			public void run() {
				if (!stopAnimations) {
				// ============== Animate Within the Player Inventory ============== //
				for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (isName) { setNameData(player, inPlayerInventory, nameString); } 
						else { setLoreData(player, inPlayerInventory, loreString); }
					}
				}
				// =============== Animate Within the Player's Armor =============== //
				for (ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (isName) { setNameData(player, inPlayerInventory, nameString); } 
						else { setLoreData(player, inPlayerInventory, loreString); }
					}
				}
				// ========== Animate Within the Player Crafting/Chests ============ //
				for (ItemStack inPlayerInventory: player.getOpenInventory().getTopInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (isName) { setNameData(player, inPlayerInventory, nameString); } 
						else { setLoreData(player, inPlayerInventory, loreString); }
					}
				}
				// ============== Animate Within the Player's Cursor =============== //
				if (player.getItemOnCursor().getType() != null && player.getItemOnCursor().getType() != Material.AIR && itemMap.getTempItem() != null && itemMap.isSimilar(player.getItemOnCursor())) {
					ItemStack item = new ItemStack(player.getItemOnCursor());
					if (InvClickSurvival.cursorItem.get(PlayerHandler.getPlayerID(player)) != null && itemMap.isSimilar(InvClickSurvival.cursorItem.get(PlayerHandler.getPlayerID(player)))) { item = new ItemStack(InvClickSurvival.cursorItem.get(PlayerHandler.getPlayerID(player))); }
					if (isName) { setNameData(player, player.getItemOnCursor(), nameString); } 
					else { setLoreData(player, player.getItemOnCursor(), loreString); }
					InvClickSurvival.cursorItem.put(PlayerHandler.getPlayerID(player), item);
				}
				// ============== This has Concluded all Animations.. ============== //
				
				if (!hasNext) { 
					if (isName) { nameTasks(player); }
					else { loreTasks(player); }
				}
				}
			}
		}.runTaskLater(ItemJoin.getInstance(), UpdateDelay);
	}
	
	private void setNameData(Player player, ItemStack transAnimate, String nameString) {
		ItemMeta tempmeta = transAnimate.getItemMeta();
		tempmeta.setDisplayName(Utils.translateLayout(ItemHandler.purgeDelay(nameString) + this.itemMap.getLegacySecret(), player));
		transAnimate.setItemMeta(tempmeta);
	}
	
	private void setLoreData(Player player, ItemStack transAnimate, List<String> loreString) {
		ItemMeta tempmeta = transAnimate.getItemMeta();
			List < String > loreList = loreString;
			List < String > loreFormatList = new ArrayList < String > ();
			for (int k = 0; k < loreList.size(); k++) {
				String formatLore = ItemHandler.purgeDelay(loreList.get(k));
				formatLore = Utils.translateLayout(formatLore, player);
				loreFormatList.add(formatLore);
			}
			tempmeta.setLore(loreFormatList);
		transAnimate.setItemMeta(tempmeta);
	}

}
