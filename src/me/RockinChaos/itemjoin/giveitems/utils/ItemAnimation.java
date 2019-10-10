package me.RockinChaos.itemjoin.giveitems.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.listeners.InventoryClick;
import me.RockinChaos.itemjoin.utils.Utils;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.RockinChaos.itemjoin.utils.Legacy;

public class ItemAnimation {
	
	private ItemMap itemMap;
	private List<String> dynamicNames = null;
	private List<List<String>> dynamicLores = null;
	private List<String> dynamicMaterials = null;
	private List<String> dynamicOwners = null;
	private List<String> dynamicTextures = null;
	private boolean stopAnimations = false;
	private boolean menu = false;
	private List<List<String>> menuLores = null;
	
	public ItemAnimation(ItemMap item) {
		this.itemMap = item;
		if (item.getDynamicNames() != null && !item.getDynamicNames().isEmpty()) { this.dynamicNames = item.getDynamicNames(); }
		if (item.getDynamicLores() != null && !item.getDynamicLores().isEmpty()) { this.dynamicLores = item.getDynamicLores(); }
		if (item.getDynamicMaterials() != null && !item.getDynamicMaterials().isEmpty()) { this.dynamicMaterials = item.getDynamicMaterials(); }
		if (item.getDynamicOwners() != null && !item.getDynamicOwners().isEmpty()) { this.dynamicOwners = item.getDynamicOwners(); }
		else if (item.getDynamicTextures() != null && !item.getDynamicTextures().isEmpty()) { this.dynamicTextures = item.getDynamicTextures(); }
	}
	
	public void openAnimation(Player player) {
		if (this.dynamicNames != null) { this.nameTasks(player); }
		if (this.dynamicLores != null) { this.loreTasks(player); }
		if (this.dynamicMaterials != null) { this.materialTasks(player); }
		if (this.dynamicOwners != null) { this.ownerTasks(player); }
		else if (this.dynamicTextures != null) { this.textureTasks(player); }
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
			if (Utils.returnInteger(ItemHandler.getDelay(name)) != null) { ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(name)); }
			else { ticks = ticks + 180; }
			AnimateTask(player, it.hasNext(), name, null, null, null, null, ticks, 0);
		}
	}
	
	private void loreTasks(Player player) {
		long ticks = 0;
		int position = 0;
		Iterator<List<String>> it = this.dynamicLores.iterator();
		while (it.hasNext()) {
			List<String> lore = it.next();
			if (Utils.returnInteger(ItemHandler.getDelay(lore.get(0))) != null) { ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(lore.get(0))); }
			else { ticks = ticks + 180; }
			AnimateTask(player, it.hasNext(), null, lore, null, null, null, ticks, position);
			position++;
		}
	}
	
	private void materialTasks(Player player) {
		long ticks = 0;
		Iterator<String> it = this.dynamicMaterials.iterator();
		while (it.hasNext()) {
			String mat = it.next();
			if (Utils.returnInteger(ItemHandler.getDelay(mat)) != null) { ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(mat)); }
			else { ticks = ticks + 180; }
			AnimateTask(player, it.hasNext(), null, null, mat, null, null, ticks, 0);
		}
	}
	
	private void ownerTasks(Player player) {
		long ticks = 0;
		Iterator<String> it = this.dynamicOwners.iterator();
		while (it.hasNext()) {
			String owner = it.next();
			if (Utils.returnInteger(ItemHandler.getDelay(owner)) != null) { ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(owner)); }
			else { ticks = ticks + 180; }
			AnimateTask(player, it.hasNext(), null, null, null, owner, null, ticks, 0);
		}
	}
	
	private void textureTasks(Player player) {
		long ticks = 0;
		Iterator<String> it = this.dynamicTextures.iterator();
		while (it.hasNext()) {
			String texture = it.next();
			if (Utils.returnInteger(ItemHandler.getDelay(texture)) != null) { ticks = ticks + Utils.returnInteger(ItemHandler.getDelay(texture)); }
			else { ticks = ticks + 180; }
			AnimateTask(player, it.hasNext(), null, null, null, null, texture, ticks, 0);
		}
	}
	
	private void AnimateTask(final Player player, final boolean hasNext, final String nameString, final List<String> loreString, final String materialString, final String ownerString, final String textureString, final long UpdateDelay, final int position) {
		final ItemMap itemMap = this.itemMap;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!stopAnimations) {
				// ============== Animate Within the Player Inventory ============== //
				for (ItemStack inPlayerInventory: player.getInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { setLoreData(player, inPlayerInventory, loreString); }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
						else if (ownerString != null || textureString != null) { setSkull(player, inPlayerInventory, ownerString, textureString); }
					}
				}
				// =============== Animate Within the Player's Armor =============== //
				for (ItemStack inPlayerInventory: player.getInventory().getArmorContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { setLoreData(player, inPlayerInventory, loreString); }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
						else if (ownerString != null || textureString != null) { setSkull(player, inPlayerInventory, ownerString, textureString); }
					}
				}
				// ========== Animate Within the Player Crafting/Chests ============ //
				for (ItemStack inPlayerInventory: player.getOpenInventory().getTopInventory().getContents()) {
					if (inPlayerInventory != null && itemMap.getTempItem() != null && itemMap.isSimilar(inPlayerInventory)) {
						if (nameString != null) { setNameData(player, inPlayerInventory, nameString); } 
						else if (loreString != null) { if (menu) { setLoreData(player, inPlayerInventory, menuLores.get(position)); } else { setLoreData(player, inPlayerInventory, loreString); } }
						else if (materialString != null) { setMaterialData(player, inPlayerInventory, materialString); }
						else if (ownerString != null || textureString != null) { setSkull(player, inPlayerInventory, ownerString, textureString); }
					}
				}
				// ============== Animate Within the Player's Cursor =============== //
				if (player.getItemOnCursor().getType() != null && player.getItemOnCursor().getType() != Material.AIR && itemMap.getTempItem() != null && itemMap.isSimilar(player.getItemOnCursor())) {
					ItemStack item = new ItemStack(player.getItemOnCursor());
					if (InventoryClick.cursorItem.get(PlayerHandler.getPlayerID(player)) != null && itemMap.isSimilar(InventoryClick.cursorItem.get(PlayerHandler.getPlayerID(player)))) { item = new ItemStack(InventoryClick.cursorItem.get(PlayerHandler.getPlayerID(player))); }
					if (nameString != null) { setNameData(player, player.getItemOnCursor(), nameString); } 
					else if (loreString != null) { setLoreData(player, player.getItemOnCursor(), loreString); }
					else if (materialString != null) { setMaterialData(player, player.getItemOnCursor(), materialString); }
					else if (ownerString != null || textureString != null) { setSkull(player, player.getItemOnCursor(), ownerString, textureString); }
					InventoryClick.cursorItem.put(PlayerHandler.getPlayerID(player), item);
				}
				// ============== This has Concluded all Animations.. ============== //
				
				if (!hasNext) { 
					if (nameString != null) { nameTasks(player); }
					else if (loreString != null) { loreTasks(player); }
					else if (materialString != null) { materialTasks(player); }
					else if (ownerString != null) { ownerTasks(player); }
					else if (textureString != null) { textureTasks(player); }
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
				if (!Utils.isInt(parts[0])) { mat = Legacy.getLegacyMaterial(Material.getMaterial(parts[0].toUpperCase()), (byte) Integer.parseInt(parts[1])); } 
				else { mat = Legacy.getLegacyMaterial(Integer.parseInt(parts[0]), (byte) Integer.parseInt(parts[1])); }
				if (mat != null && mat != Material.AIR) { transAnimate.setType(mat); }
			} else {
				mat = ItemHandler.getMaterial(parts[0], null);
				if (mat != null && mat != Material.AIR) { transAnimate.setType(mat); }	
				Legacy.setLegacyDurability(transAnimate, (byte) Integer.parseInt(parts[1]));
			}
		} else {
			mat = ItemHandler.getMaterial(materialString, null);
			if (mat != null && mat != Material.AIR) { transAnimate.setType(mat); }
		}
	}
	
	private void setSkull(Player player, ItemStack transAnimate, String ownerString, String textureString) {
		ItemMeta tempMeta = transAnimate.getItemMeta();
		if (ownerString != null) {
			ownerString = Utils.translateLayout(ItemHandler.purgeDelay(ownerString), player);
			tempMeta = ItemHandler.setSkullOwner(tempMeta, ownerString);
		} else if (textureString != null && textureString.contains("hdb-") && itemMap.isHeadDatabase()) {
			HeadDatabaseAPI api = new HeadDatabaseAPI();
			ItemStack sk = api.getItemHead(ItemHandler.purgeDelay(textureString).replace("hdb-", ""));
			transAnimate = (sk != null ? sk : transAnimate);
		} else if (textureString != null) {
			try {
				GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
				gameProfile.getProperties().put("textures", new Property("textures", new String(ItemHandler.purgeDelay(textureString))));
				Field declaredField = tempMeta.getClass().getDeclaredField("profile");
				declaredField.setAccessible(true);
				declaredField.set(tempMeta, gameProfile);
			} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
		}
		transAnimate.setItemMeta(tempMeta);
	}
	
	public void setMenu(boolean bool, int stage) {
		this.menu = bool;
		if (bool && this.dynamicLores != null) {
			this.menuLores = new ArrayList<List<String>>();
			for (List<String> lores : this.dynamicLores) {
				List<String> tempLores = new ArrayList<String>();
				for (String lore : lores) { tempLores.add(lore); }
				tempLores.add("&7");
				tempLores.add("&6---------------------------");
				if (stage == 1) { tempLores.add("&7*Click to set as a swap-item."); } 
				else { tempLores.add("&7*Click to modify this custom item."); }
				tempLores.add("&9&lNode: &a" + itemMap.getConfigName());
				tempLores.add("&7");
				this.menuLores.add(tempLores);
			}
		}
	}
}