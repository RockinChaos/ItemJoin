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
import me.RockinChaos.itemjoin.utils.Legacy;

public class ItemAnimation {
	
	private ItemMap itemMap;
	private List<String> dynamicNames = null;
	private List<List<String>> dynamicLores = null;
	private List<String> dynamicMaterials = null;
	private boolean stopAnimations = false;
	
	public ItemAnimation(ItemMap item) {
		this.itemMap = item;
		if (item.getDynamicNames() != null) { this.dynamicNames = item.getDynamicNames(); }
		if (item.getDynamicLores() != null) { this.dynamicLores = item.getDynamicLores(); }
		if (item.getDynamicMaterials() != null) { this.dynamicMaterials = item.getDynamicMaterials(); }
	}
	
	public void openAnimation(Player player) {
		if (dynamicNames != null) { nameTasks(player); }
		if (dynamicLores != null) { loreTasks(player); }
		if (dynamicMaterials != null) { materialTasks(player); }
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
			AnimateTask(player, it.hasNext(), name, null, null, ticks);
		}
	}
	
	private void loreTasks(Player player) {
		long ticks = 0;
		Iterator<List<String>> it = this.dynamicLores.iterator();
		while (it.hasNext()) {
			List<String> lore = it.next();
			ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(lore.get(0)));
			AnimateTask(player, it.hasNext(), null, lore, null, ticks);
		}
	}
	
	private void materialTasks(Player player) {
		long ticks = 0;
		Iterator<String> it = this.dynamicMaterials.iterator();
		while (it.hasNext()) {
			String mat = it.next();
			ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(mat));
			AnimateTask(player, it.hasNext(), null, null, mat, ticks);
		}
	}
	
	private void AnimateTask(final Player player, final boolean hasNext, final String nameString, final List<String> loreString, final String materialString, final long UpdateDelay) {
		final ItemMap itemMap = this.itemMap;
		new BukkitRunnable() {
			public void run() {
				if (!stopAnimations) {
				// ============== Animate Within the Player Inventory ============== //
				for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { setLoreData(player, inPlayerInventory, loreString); }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
					}
				}
				// =============== Animate Within the Player's Armor =============== //
				for (ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { setLoreData(player, inPlayerInventory, loreString); }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
					}
				}
				// ========== Animate Within the Player Crafting/Chests ============ //
				for (ItemStack inPlayerInventory: player.getOpenInventory().getTopInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { setLoreData(player, inPlayerInventory, loreString); }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
					}
				}
				// ============== Animate Within the Player's Cursor =============== //
				if (player.getItemOnCursor().getType() != null && player.getItemOnCursor().getType() != Material.AIR && itemMap.getTempItem() != null && itemMap.isSimilar(player.getItemOnCursor())) {
					ItemStack item = new ItemStack(player.getItemOnCursor());
					if (InvClickSurvival.cursorItem.get(PlayerHandler.getPlayerID(player)) != null && itemMap.isSimilar(InvClickSurvival.cursorItem.get(PlayerHandler.getPlayerID(player)))) { item = new ItemStack(InvClickSurvival.cursorItem.get(PlayerHandler.getPlayerID(player))); }
					if (nameString != null) { setNameData(player, player.getItemOnCursor(), nameString); } 
					else if (loreString != null) { setLoreData(player, player.getItemOnCursor(), loreString); }
					else if (materialString != null) { setMaterialData(player, player.getItemOnCursor(), materialString); }
					InvClickSurvival.cursorItem.put(PlayerHandler.getPlayerID(player), item);
				}
				// ============== This has Concluded all Animations.. ============== //
				
				if (!hasNext) { 
					if (nameString != null) { nameTasks(player); }
					else if (loreString != null) { loreTasks(player); }
					else if (materialString != null) { materialTasks(player); }
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
	
	private void setMaterialData(Player player, ItemStack transAnimate, String materialString) {
		Material mat = null;
		materialString = ItemHandler.purgeDelay(materialString);
		if (materialString.contains(":")) { 
			String[] parts = materialString.split(":");
			if (ServerHandler.hasAquaticUpdate()) {
				if (!Utils.isInt(parts[0])) { parts[0] = "LEGACY_" + parts[0]; }
				if (!Utils.isInt(parts[0])) { mat = Legacy.convertLegacyMaterial(Legacy.getLegacyMaterialID(Material.getMaterial(parts[0].toUpperCase())), (byte) Integer.parseInt(parts[1])); } 
				else { mat = Legacy.convertLegacyMaterial(Integer.parseInt(parts[0]), (byte) Integer.parseInt(parts[1])); }
				if (mat != null && mat != Material.AIR) { transAnimate.setType(mat); }
			} else {
				mat = ItemHandler.getMaterial(parts[0], null, itemMap.getConfigName());
				if (mat != null && mat != Material.AIR) { transAnimate.setType(mat); }	
				Legacy.setLegacyDurability(transAnimate, (byte) Integer.parseInt(parts[1]));
			}
		} else {
			mat = ItemHandler.getMaterial(materialString, null, itemMap.getConfigName());
			if (mat != null && mat != Material.AIR) { transAnimate.setType(mat); }
		}
	}
}