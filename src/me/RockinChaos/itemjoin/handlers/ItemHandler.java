/*
 * ItemJoin
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.itemjoin.handlers;

import java.io.EOFException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.item.ItemMap;
import me.RockinChaos.itemjoin.item.ItemUtilities;
import me.RockinChaos.itemjoin.item.ItemUtilities.CustomSlot;
import me.RockinChaos.itemjoin.listeners.InventoryCrafting;
import me.RockinChaos.itemjoin.utils.DependAPI;
import me.RockinChaos.itemjoin.utils.LegacyAPI;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Utils;
import me.RockinChaos.itemjoin.utils.sqlite.SQLite;

public class ItemHandler {
	
	private static ItemHandler item;
	private HashMap < String, GameProfile > gameProfiles = new HashMap < String, GameProfile > ();
	
   /**
    * Adds a list of lores to the specified ItemStack.
    * 
    * @param item - The ItemStack to be modified.
    * @param lores - The list of lores to be added to the item.
    * @return The ItemStack with its newly added lores.
    */
	public ItemStack addLore(final ItemStack item, final String... lores) {
		ItemMeta meta = item.getItemMeta();
		List<String> newLore = new ArrayList<String>();
		if (meta.hasLore()) { newLore = meta.getLore(); }
		for (String lore : lores) { newLore.add(Utils.getUtils().colorFormat(lore)); }
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return item;
	}
	
   /**
    * Gets the exact name of the ItemStack Material with normal case and no underlines.
    * 
    * @param item - The ItemStack to have its Material name fetched.
    * @return A friendly String version of the Material name with normal case and no underlines.
    */
	public String getMaterialName(final ItemStack item) {
		try {
			return WordUtils.capitalizeFully(item.getType().name().toLowerCase().replace('_', ' '));
		} catch (NullPointerException e) {ServerHandler.getServer().sendDebugTrace(e); }
		return null;
	}
	
   /**
    * Gets the String name of the Bukkit Enchantment.
    * 
    * @param enchant - The Enchantment to have its String name found.
    * @return The String name of the Bukkit Enchantment.
    */
	public String getEnchantName(final Enchantment enchant) {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_13")) { return LegacyAPI.getLegacy().getEnchantName(enchant); } 
		else { return enchant.getKey().getKey().toString(); }
	}
	
   /**
    * Gets the exact Bukkit Enchantment provided its String name.
    * 
    * @param name - Name of the Bukkit Enchantment.
    * @return The proper Bukkit Enchantment instance.
    */
	public Enchantment getEnchantByName(final String name) {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_13")) {
			Enchantment enchantName = LegacyAPI.getLegacy().getEnchant(name);
			if (enchantName != null) { return enchantName; }
		} else {
			try {
				Enchantment enchantName = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()));
				if (enchantName != null) {
					return enchantName;
				} else { return LegacyAPI.getLegacy().getEnchant(name); }
			} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		}
		return null;
	}
	
   /**
    * Gets the current Durability Value of the specified ItemStack.
    * 
    * @param item - The ItemStack to have its Durability found.
    * @return The Durability value of the ItemStack.
    */
	public short getDurability(final ItemStack item) {
		if (!ServerHandler.getServer().hasSpecificUpdate("1_13")) {
			return LegacyAPI.getLegacy().getDurability(item);
		} else { return ((short) ((org.bukkit.inventory.meta.Damageable) item.getItemMeta()).getDamage()); }
	}
	
   /**
    * Sets the specified Durability to the specified ItemStack.
    * 
    * @param item - The ItemStack to have its Durability changed.
    * @param durability - The Durability to be set to the ItemStack.
    * @return The ItemStack with its new Durability.
    */
	public ItemStack setDurability(final ItemStack item, final int durability) {
		if (item.getType().getMaxDurability() != 0 && durability != 0) {
			if (ServerHandler.getServer().hasSpecificUpdate("1_13")) {
				ItemMeta tempMeta = item.getItemMeta();
				((org.bukkit.inventory.meta.Damageable) tempMeta).setDamage(durability);
				item.setItemMeta(tempMeta);
				return item;
			} else {
				return LegacyAPI.getLegacy().setDurability(item, (short)durability);
			}
		}
		return item;
	}
	
   /**
    * Modifies the ItemStack.
    * 
    * @param itemCopy - The copy of the ItemStack to be modified.
    * @param allItems - If the item should not have its amount changed.
    * @param amount - The intended stack size.
    * @return The newly Modified ItemStack.
    */
	public ItemStack modifyItem(final ItemStack itemCopy, final boolean allItems, final int amount) {
		ItemStack item = new ItemStack(itemCopy);
		if (((item.getAmount() > amount && item.getAmount() != amount) || item.getAmount() < amount) && !allItems) { item.setAmount(item.getAmount() - amount); } 
		else { item = new ItemStack(Material.AIR); }
		return item;
	}
	
   /**
    * Creates a new ItemStack with the specified material, count, 
    * adding a invisible glowing enchant, custom name, and lore.
    * 
    * @param material - The material name and data value of the ItemStack, Example: "WOOL:14".
    * @param count - The stack size of the ItemStack.
    * @param glowing - If the ItemStack should visually glow.
    * @param name - The custom name to be added to the ItemStack.
    * @param lores - The custom lore to be added to the ItemStack.
    */
    public ItemStack getItem(String material, final int count, final boolean glowing, String name, final String... lores) {
        ItemStack tempItem; if (!ServerHandler.getServer().hasSpecificUpdate("1_8") && material.equals("BARRIER")) { material = "WOOL:14"; }
        if (this.getMaterial(material, null) == null) { material = "STONE"; } 
        if (ServerHandler.getServer().hasSpecificUpdate("1_13")) { tempItem = new ItemStack(this.getMaterial(material, null), count); } 
        else { short dataValue = 0; if (material.contains(":")) { String[] parts = material.split(":"); material = parts[0]; dataValue = (short) Integer.parseInt(parts[1]); } tempItem = LegacyAPI.getLegacy().newItemStack(getMaterial(material, null), count, dataValue); }
        if (glowing && material != "AIR") { tempItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1); }
        ItemMeta tempMeta = tempItem.getItemMeta();
        if (ServerHandler.getServer().hasSpecificUpdate("1_8") && material != "AIR") { tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); }
        if (name != null && material != "AIR") { name = Utils.getUtils().colorFormat(name); tempMeta.setDisplayName(name); }
        if (lores != null && lores.length != 0 && material != "AIR") {
        	ArrayList<String> loreList = new ArrayList<String>();
        	for (String loreString: lores) { 
        		if (!loreString.isEmpty()) {
        			if (loreString.contains("/n")) {
        				String[] loreSplit = loreString.split(" /n ");
        				for (String loreStringSplit : loreSplit) { loreList.add(Utils.getUtils().colorFormat(loreStringSplit)); }
        			} else { loreList.add(Utils.getUtils().colorFormat(loreString)); }
        		} 
        	}
        	tempMeta.setLore(loreList);
        }
        tempItem.setItemMeta(tempMeta); 
        return tempItem;
    }
    
   /**
    * Checks the Players Inventory for an item in the ItemMap's Specified slot,
    * and returns the item if it exists.
    * 
    * @param player - The Player having their Inventory checked.
    * @param itemMap - The ItemMap that is having its slot checked.
    * @return The existing ItemStack from the Players Inventory.
    */
	public ItemStack getItem(final Player player, final ItemMap itemMap) {
		int craftSlot = Utils.getUtils().getSlotConversion(itemMap.getSlot());
		ItemStack existingItem = null;
		if (Utils.getUtils().isInt(itemMap.getSlot())) {
			existingItem = player.getInventory().getItem(Integer.parseInt(itemMap.getSlot()));
		} else if (CustomSlot.HELMET.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getHelmet();
		} else if (CustomSlot.CHESTPLATE.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getChestplate();
		} else if (CustomSlot.LEGGINGS.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getLeggings();
		} else if (CustomSlot.BOOTS.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getBoots();
		} else if (ServerHandler.getServer().hasSpecificUpdate("1_9") && CustomSlot.OFFHAND.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getItemInOffHand();
		} else if (craftSlot != -1) {
			existingItem = player.getOpenInventory().getTopInventory().getItem(craftSlot);
		}
		return (existingItem != null && existingItem.getType() != Material.AIR ? existingItem : null);
	}
    
   /**
    * Gets the Bukkit Material instance of the specified String material name and data value.
    * 
    * @param material - The item ID or Bukkit Material String name.
    * @param data - The data value of the item, usually this is zero.
    * @return The proper Bukkit Material instance.
    */
	public Material getMaterial(String material, String data) {
		try {
			boolean isLegacy = (data != null);
			if (material.contains(":")) { String[] parts = material.split(":"); material = parts[0]; if (!parts[1].equalsIgnoreCase("0")) { data = parts[1]; isLegacy = true; } }
			if (Utils.getUtils().isInt(material) && !ServerHandler.getServer().hasSpecificUpdate("1_13")) {
				return LegacyAPI.getLegacy().findMaterial(Integer.parseInt(material));
			} else if (Utils.getUtils().isInt(material) && ServerHandler.getServer().hasSpecificUpdate("1_13") || isLegacy && ServerHandler.getServer().hasSpecificUpdate("1_13")) {
				int dataValue;
				if (!Utils.getUtils().isInt(material)) { material = "LEGACY_" + material; }
				if (data != null) { dataValue = Integer.parseInt(data); } else { dataValue = 0; }
				if (!Utils.getUtils().isInt(material)) { return LegacyAPI.getLegacy().getMaterial(Material.getMaterial(material.toUpperCase()), (byte) dataValue); } 
				else { return LegacyAPI.getLegacy().getMaterial(Integer.parseInt(material), (byte) dataValue); }
			} else if (!ServerHandler.getServer().hasSpecificUpdate("1_13")) {
				return Material.getMaterial(material.toUpperCase());
			} else {
				return Material.matchMaterial(material.toUpperCase());
			}
		} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		return null;
	}
	
   /**
    * Gets the material as defined in the ConfigurationSection.
    * 
    * @param itemNode - The node ConfigurationSection (location) of the config section.
    * @return The material exactly as found in the Configuration Section, without any animation delay.
    */
	public String getMaterial(final ConfigurationSection itemNode) {
		String material = this.cutDelay(itemNode.getString(".id"));
		if (ConfigHandler.getConfig(false).getMaterialSection(itemNode) != null) {
			List<String> materials = new ArrayList<String>();
			for (String materialKey : ConfigHandler.getConfig(false).getMaterialSection(itemNode).getKeys(false)) {
				String materialList = itemNode.getString(".id." + materialKey);
				if (materialList != null) {
					materials.add(materialList);
				}
			}
			material = this.cutDelay(itemNode.getString(".id." + ConfigHandler.getConfig(false).getMaterialSection(itemNode).getKeys(false).iterator().next()));
			return material;
		}
		return material;
	}
	
   /**
    * Checks if the ItemStack List contains the Material.
    * 
    * @param itemstacks - The ItemStacks being checked.
    * @param material - The Material expected.
    * @return If the List contains the Material
    */
	public boolean containsMaterial(final Collection<ItemStack> itemstacks, final Material material) {
		for (ItemStack item: itemstacks) {
			if (item.getType().equals(material)) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Sets the Skull Texture to the ItemStack.
    * 
    * @param item - The ItemStack to have its Skull Texture changed.
    * @param skullTexture - The Skull Texture to be added to the ItemStack.
    */
    public ItemStack setSkullTexture(final ItemStack item, String skullTexture) {
    	try {
	        ItemMeta itemMeta = item.getItemMeta();
			GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
			gameProfile.getProperties().put("textures", new Property("textures", new String(skullTexture)));
			Field declaredField = itemMeta.getClass().getDeclaredField("profile");
			declaredField.setAccessible(true);
			declaredField.set(itemMeta, gameProfile);
			item.setItemMeta(itemMeta);
    	} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
    	return item;
    }
	
   /**
    * Gets the current Skull Texture of the ItemMeta.
    * 
    * @param meta - The ItemMeta to have its Skull Texture found.
    * @return The found Skull Texture String value.
    */
	public String getSkullTexture(final ItemMeta meta) {
		try {
			final Class < ? > cls = Reflection.getReflection().getCraftBukkitClass("inventory.CraftMetaSkull");
			final Object real = cls.cast(meta);
			final Field field = real.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			final GameProfile profile = (GameProfile) field.get(real);
			final Collection < Property > props = profile.getProperties().get("textures");
			for (final Property property: props) {
				if (property.getName().equals("textures")) { return property.getValue(); }
			}
		} catch (Exception e) { }
		return "";
	}
	
   /**
    * Sets the Skull Owner name to the ItemMeta.
    * 
    * @param meta - The ItemMeta to have its Skull Owner changed.
    * @param owner - The String name of the Skull Owner to be set.
    * @return The ItemMeta with the new Skull Owner.
    */
	public ItemMeta setSkullOwner(final ItemMeta meta, final String owner) {
		if (ServerHandler.getServer().hasSpecificUpdate("1_8") && !DependAPI.getDepends(false).skinsRestorerEnabled()) {
			try {
				Method fetchProfile = Reflection.getReflection().getCraftBukkitClass("entity.CraftPlayer").getDeclaredMethod("getProfile");
				Field declaredField = meta.getClass().getDeclaredField("profile");
				declaredField.setAccessible(true);
				if (PlayerHandler.getPlayer().getPlayerString(owner) != null) {
					declaredField.set(meta, fetchProfile.invoke(PlayerHandler.getPlayer().getPlayerString(owner)));
				} else if (PlayerHandler.getPlayer().getPlayerString(owner) == null) {
					if (this.gameProfiles.get(owner) == null) {
						String uuidString = Utils.getUtils().getMojangUUID(owner);
						if (uuidString != null) {
							GameProfile profile = ServerHandler.getServer().setSkin(new GameProfile(Utils.getUtils().UUIDConversion(uuidString), owner), Utils.getUtils().UUIDConversion(uuidString));
							this.gameProfiles.put(owner, profile);
						}
					}
					declaredField.set(meta, this.gameProfiles.get(owner));
				}
			} catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); LegacyAPI.getLegacy().setSkullOwner(((SkullMeta) meta), owner); }
		} else if (!DependAPI.getDepends(false).skinsRestorerEnabled()) {
			ServerHandler.getServer().logDebug("{ItemMap} Minecraft does not support offline player heads below Version 1.8.");
			ServerHandler.getServer().logDebug("{ItemMap} Player heads will only be given a skin if the player has previously joined the sever.");
			return LegacyAPI.getLegacy().setSkullOwner(((SkullMeta) meta), owner);
		} else {
			OfflinePlayer player;
			String uuidString = Utils.getUtils().getMojangUUID(owner);
			try { player = Bukkit.getOfflinePlayer(UUID.fromString(uuidString)); }
			catch (Exception e) { player = LegacyAPI.getLegacy().getOfflinePlayer(owner); }
			((SkullMeta) meta).setOwningPlayer(player);
		}
		return meta;
	}
	
   /**
	* Stacks two items together.
	* 
	* @param player - The player being referenced.
	* @param item1 - The main item being removed.
	* @param item2 - The secondary item being stacked.
	* @param slot - The new event slot of the main item.
	* @return The Remaining amount to be set (if any).
	*/
	public int stackItems(final Player player, final ItemStack item1, final ItemStack item2, final int slot) {
		int MINECRAFT_STACK_MAX = 64;
		int DESIRED_STACK_SIZE = item1.getAmount() + item2.getAmount();
		int REMAINING_STACK_SIZE = 0;
		if (DESIRED_STACK_SIZE > MINECRAFT_STACK_MAX) {
			item2.setAmount(MINECRAFT_STACK_MAX);
			item1.setAmount(DESIRED_STACK_SIZE - MINECRAFT_STACK_MAX);
			REMAINING_STACK_SIZE = item1.getAmount();
		} else {
			item2.setAmount(item2.getAmount() + item1.getAmount());
			if (slot == -1) {
				player.getOpenInventory().setCursor(new ItemStack(Material.AIR));
			} else if (slot != -2) {
				player.getInventory().setItem(slot, new ItemStack(Material.AIR));
			}
		}
		return REMAINING_STACK_SIZE;
	}
	
   /**
    * Gets the existing MapView for the image id.
    * 
    * @param id - that will recieve the items.
    * @retrn The existing MapView.
    */
	public MapView existingView(final int id) {
		MapView view = LegacyAPI.getLegacy().getMapView(id);
		if (view == null) { view = LegacyAPI.getLegacy().createMapView(); }
		return view;
	}
	
   /**
    * Saves any existing players that are on cooldown for each item.
    * 
    */
	public void saveCooldowns() {
		for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
			for (String keys: itemMap.getPlayersOnCooldown().keySet()) {
				String[] parts = keys.split("-.-");
				if (System.currentTimeMillis() - itemMap.getPlayersOnCooldown().get(keys) <= itemMap.getCommandCooldown() * 1000) {
					SQLite.getLite(false).saveCooldown(itemMap.getConfigName(), parts[1], parts[0], itemMap.getPlayersOnCooldown().get(keys), itemMap.getCommandCooldown());
				}
			}
		}
	}
	
   /**
    * Removes the Crafting Items from ALL Online Players.
    * 
    * @param saveCrafting - If the Crafting Items should be saved to be returned later.
    */
	public void purgeCraftItems(final boolean saveCrafting) {
		PlayerHandler.getPlayer().forOnlinePlayers(player -> {
			if (saveCrafting) { this.saveCraftItems(player); }
			LegacyAPI.getLegacy().removeCraftItems(player); 
		});
	}
    
   /**
    * Saves the players current crafting items for later retrieval.
    * 
    * @param player - The Player to have its crafting items saved.
    */
    public void saveCraftItems(final Player player) {
			if (InventoryCrafting.getCreativeCraftItems().containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
				Inventory inv = Bukkit.createInventory(null, 9);
				boolean notNull = false;
				ItemStack[] craftingContents = InventoryCrafting.getCreativeCraftItems().get(PlayerHandler.getPlayer().getPlayerID(player));
				for (int k = 0; k <= 4; k++) {
					inv.setItem(k, craftingContents[k]); 
					if (craftingContents[k] != null && craftingContents[k].getType() != Material.AIR) { notNull = true; }
				}
				if (notNull) { SQLite.getLite(false).saveReturnCraftItems(player, inv); }
			} else if (InventoryCrafting.getOpenCraftItems().containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
				Inventory inv = Bukkit.createInventory(null, 9);
				boolean notNull = false;
				ItemStack[] craftingContents = InventoryCrafting.getOpenCraftItems().get(PlayerHandler.getPlayer().getPlayerID(player));
				for (int k = 0; k <= 4; k++) {
					inv.setItem(k, craftingContents[k]); 
					if (craftingContents[k] != null && craftingContents[k].getType() != Material.AIR) { notNull = true; }
				}
				if (notNull) { SQLite.getLite(false).saveReturnCraftItems(player, inv); }
			} else if (InventoryCrafting.getCraftItems().containsKey(PlayerHandler.getPlayer().getPlayerID(player))) {
				Inventory inv = Bukkit.createInventory(null, 9);
				boolean notNull = false;
				ItemStack[] craftingContents = InventoryCrafting.getCraftItems().get(PlayerHandler.getPlayer().getPlayerID(player));
				for (int k = 0; k <= 4; k++) {
					inv.setItem(k, craftingContents[k]); 
					if (craftingContents[k] != null && craftingContents[k].getType() != Material.AIR) { notNull = true; }
				}
				if (notNull) { SQLite.getLite(false).saveReturnCraftItems(player, inv); }
			}
    }
    
   /**
    * Restores the players crafting items to their inventory if they were previously saved.
    * 
    * @param player - The Player to have its crafting items restored.
    */
    public void restoreCraftItems(final Player player) {
    	Inventory inventory = SQLite.getLite(false).getReturnCraftItems(player);
		Inventory craftView = player.getOpenInventory().getTopInventory();
		if (inventory != null && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
			for (int k = 4; k >= 0; k--) {
				if (inventory.getItem(k) != null && inventory.getItem(k).getType() != Material.AIR) {
					craftView.setItem(k, inventory.getItem(k).clone());
				}
			}
			PlayerHandler.getPlayer().updateInventory(player, 1L);
			SQLite.getLite(false).removeReturnCraftItems(player);
		} else if (!PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
			ServerHandler.getServer().runAsyncThread(main -> { this.restoreCraftItems(player); }, 60L);
		}
    }
    
   /**
    * Removes all crafting items from the players inventory.
    * 
    * @param player - The Player to have their crafting items removed.
    */
    public void removeCraftItems(final Player player) {
    	Inventory craftView = player.getOpenInventory().getBottomInventory();
    	Inventory inventory = SQLite.getLite(false).getReturnCraftItems(player);
    	if (inventory != null && ServerHandler.getServer().hasSpecificUpdate("1_15") && PlayerHandler.getPlayer().isCraftingInv(player.getOpenInventory())) {
    		for (int k = 4; k >= 0; k--) {
    			if (inventory.getItem(k) != null && inventory.getItem(k).getType() != Material.AIR) {
    				int amount = inventory.getItem(k).getAmount();
    				for (int i = 0; i < craftView.getSize(); i++) {
    					for (ItemMap itemMap: ItemUtilities.getUtilities().getCraftingItems()) {
    						if (craftView.getItem(i) != null && craftView.getItem(i).getType() != Material.AIR && itemMap.isReal(inventory.getItem(k)) && itemMap.isReal(craftView.getItem(i))) {
    							amount -= craftView.getItem(i).getAmount();
    							if (amount <= 0) {
    								craftView.getItem(i).setAmount(0);
    								craftView.setItem(i, new ItemStack(Material.AIR));
    								if (amount == 0) { break; }
    							} else { craftView.getItem(i).setAmount(amount); }
    						}
    					}
    				}
    			}
    		}
    	}
    }
    
   /**
    * Copies the specified ItemStack contents.
    * 
    * @param contents - The ItemStack contents to be copied.
    * @return The copied ItemStack contents.
    */
    public ItemStack[] cloneContents(final ItemStack[] contents) {
    	int itr = 0;
    	ItemStack[] copyContents = contents;
    	for (ItemStack itemStack: contents) {
    		if (copyContents[itr] != null) {
    			copyContents[itr] = itemStack.clone();
    		}
    		itr++;
    	}
    	return copyContents;
    }
    
   /**
    * Checks if the ItemStack contents are NULL or empty.
    * 
    * @param contents - The ItemStack contents to be checked.
    * @return If the contents do not exist.
    */
    public boolean isContentsEmpty(final ItemStack[] contents) {
    	int size = 0; 
    	for (ItemStack itemStack: contents) { 
    		if (itemStack == null || itemStack.getType().equals(Material.AIR)) { 
    			size++; 
    		} 
    	}
    	return (size == contents.length);
    }
    
   /**
    * Converts the Inventory to a Base64 String.
    * This is a way of encrypting a Inventory to be decrypted and referenced later.
    * 
    * @param inventory - The Inventory to be converted.
    * @return The Base64 String of the Inventory.
    */
	public String serializeInventory(final Inventory inventory) {
	    try {
	    	java.io.ByteArrayOutputStream str = new java.io.ByteArrayOutputStream();
	        org.bukkit.util.io.BukkitObjectOutputStream data = new org.bukkit.util.io.BukkitObjectOutputStream(str);
	        data.writeInt(inventory.getSize());
	        for (int i = 0; i < inventory.getSize(); i++) {
	            data.writeObject(inventory.getItem(i));
	        }
	        data.close();
	        return Base64.getEncoder().encodeToString(str.toByteArray());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "";
	}

   /**
    * Converts the Base64 String to a Inventory.
    * This is a way of decrypting an encrypted Inventory to be referenced.
    * 
    * @param inventoryData - The Base64 String to be converted to an Inventory.
    * @return The Inventory instance that has been deserialized.
    */
	public Inventory deserializeInventory(final String inventoryData) {
	    try {
	    	java.io.ByteArrayInputStream stream = new java.io.ByteArrayInputStream(Base64.getDecoder().decode(inventoryData));
	        org.bukkit.util.io.BukkitObjectInputStream data = new org.bukkit.util.io.BukkitObjectInputStream(stream);
	        Inventory inventory = Bukkit.createInventory(null, data.readInt());
	        for (int i = 0; i < inventory.getSize(); i++) {
	            inventory.setItem(i, (ItemStack) data.readObject());
	        }
	        data.close();
	        return inventory;
	    } catch (EOFException e) {
	    	return null;
	    } catch (Exception e) {
	        ServerHandler.getServer().sendDebugTrace(e);
	    }
	    return null;
	}
	
   /**
    * Gets the custom NBTData of the ItemStack.
    * 
    * @param item - The ItemStack to have its custom NBTData found.
    * @return The String of NBTData found on the ItemStack.
    */
	public String getNBTData(final ItemStack item) {
		if (this.dataTagsEnabled() && item != null && item.getType() != Material.AIR) {
			try {
				Object nms = Reflection.getReflection().getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
				Object cacheTag = Reflection.getReflection().getMinecraftClass("ItemStack").getMethod("getTag").invoke(nms);
				if (cacheTag != null) {
					String data = (String) cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin");
					String data1 = (String) cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin Name");
					String data2 = (String) cacheTag.getClass().getMethod("getString", String.class).invoke(cacheTag, "ItemJoin Slot");
					if (data1 != null && data2 != null && !data1.isEmpty() && !data2.isEmpty()) {
						return data1 + " " + data2;
					} else if (data != null && !data.isEmpty()) { 
						return data.replace("Slot: ", "");
					}
				}
			} catch (Exception e) {
				ServerHandler.getServer().logSevere("{ItemMap} An error has occured when getting NBTData to an item.");
				ServerHandler.getServer().sendDebugTrace(e);
			}
		}
		return null;
	}
	
   /**
    * Checks if the ItemStack contains plugin specific NBTData.
    * 
    * @param item - The ItemStack to be checked for custom NBTData.
    * @return If the ItemStack has plugin specific NBTData.
    */
	public boolean containsNBTData(final ItemStack item) {
		if (this.dataTagsEnabled() && item != null && item.getType() != Material.AIR && this.getNBTData(item) != null) {
			return true;
		} else if (!this.dataTagsEnabled()) { 
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& Utils.getUtils().colorDecode(item.getItemMeta().getDisplayName()).contains(Utils.getUtils().colorDecode(Utils.getUtils().colorEncode("ItemJoin")))) {
				return true;
			}
		}
		return false;
	}
	
   /**
    * Attempts to fetch the designated slot of an item.
    * 
    * @param material - The ItemStack to be checked for a designated slot.
    * @return The proper designated slot name.
    */
	public String getDesignatedSlot(final Material material) {
		String name = material.name().contains("_") ? material.name().split("_")[1] : material.name();
		String hand = (ServerHandler.getServer().hasSpecificUpdate("1_13") ? "hand" : "mainhand");
		return (name != null ? (name.equalsIgnoreCase("HELMET") ? "head" : name.equalsIgnoreCase("CHESTPLATE") ? "chest" : name.equalsIgnoreCase("LEGGINGS") ? "legs" : name.equalsIgnoreCase("BOOTS") ? "feet" : 
			    name.equalsIgnoreCase("HOE") ? hand : name.equalsIgnoreCase("SWORD") ? hand : name.equalsIgnoreCase("SHOVEL") ? hand : name.equalsIgnoreCase("AXE") ? hand : name.equalsIgnoreCase("PICKAXE") ? hand : "noslot") : "noslot");
	}
	
   /**
    * Checks if NBTData is enabled for the current server version.
    * 
    * @return If NBTData is enabled.
    */
	public boolean dataTagsEnabled() {
		if (ServerHandler.getServer().hasSpecificUpdate("1_8")) {
			return ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Settings.DataTags");
		}
		return false;
	}
	
   /**
    * Gets the Integer Delay of the specified String.
    * 
    * @param context - The String to have the Delay found.
    * @return The Delay of the String as an Integer.
    */
	public int getDelay(final String context) {
		try { if (Utils.getUtils().returnInteger(context) != null) { return Utils.getUtils().returnInteger(context); } } 
		catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
		return 0;
	}
	
   /**
    * Gets the delay format including the proper delay Integer of the String.
    * 
    * @param context - The String to have the Delay found.
    * @return The Delay Format of the String with the proper Integer value.
    */
	public String getDelayFormat(final String context) {
		if (Utils.getUtils().containsIgnoreCase(context, "<delay:" + Utils.getUtils().returnInteger(context) + ">") 
				|| Utils.getUtils().containsIgnoreCase(context, "delay:" + Utils.getUtils().returnInteger(context) + "") 
				|| Utils.getUtils().containsIgnoreCase(context, "<delay: " + Utils.getUtils().returnInteger(context) + ">")
				|| Utils.getUtils().containsIgnoreCase(context, "delay: " + Utils.getUtils().returnInteger(context) + "")) {
			return ("<delay:" + Utils.getUtils().returnInteger(context) + ">");
		}
		return null;
	}
	
   /**
    * Removes the delay formatting from the specified String.
    * 
    * @param context - The String to have the Delay Formatting removed.
    * @return The String with the removed Delay Formatting.
    */
	public String cutDelay(final String context) {
		if (this.getDelayFormat(context) != null) {
			return context.replace(this.getDelayFormat(context), "");
		} 
		return context;
	}
	
   /**
    * Removes the delay formatting from the specified String List.
    * 
    * @param context - The String List to have the Delay Formatting removed.
    * @return The String List with the removed Delay Formatting.
    */
	public List<String> cutDelay(final List <String> context) {
		List<String> newContext = new ArrayList<String>();
		for (String minorContext : context) {
			newContext.add(this.cutDelay(minorContext));
		}
		return newContext;
	}
	
   /**
    * Checks if the specified String slot is a Custom Slot.
    * 
    * @param slot - The slot to be checked.
    * @return If the slot is a custom slot.
    */
	public boolean isCustomSlot(final String slot) {
		if (slot.equalsIgnoreCase("Offhand") || slot.equalsIgnoreCase("Arbitrary") || slot.equalsIgnoreCase("Helmet") 
				|| slot.equalsIgnoreCase("Chestplate") || slot.equalsIgnoreCase("Leggings") || slot.equalsIgnoreCase("Boots") || this.isCraftingSlot(slot)) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the specified String slot is a Crafting Slot.
    * 
    * @param slot - The slot to be checked.
    * @return If the slot is a crafting slot.
    */
	public boolean isCraftingSlot(final String slot) {
		if (slot.equalsIgnoreCase("CRAFTING[0]") || slot.equalsIgnoreCase("CRAFTING[1]") 
				|| slot.equalsIgnoreCase("CRAFTING[2]") || slot.equalsIgnoreCase("CRAFTING[3]") || slot.equalsIgnoreCase("CRAFTING[4]")) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the Material is a Legacy Skull Item or Player Skull.
    * 
    * @param material - The Material to be checked.
    * @return If the Material is a Skull/Player Head.
    */
	public boolean isSkull(final Material material) {
		if (material.toString().equalsIgnoreCase("SKULL_ITEM") || material.toString().equalsIgnoreCase("PLAYER_HEAD")) {
			return true;
		}
		return false;
	}
	
   /**
    * Checks if the ItemStack is a Writable Book.
    * 
    * @param item - The ItemStack to be checked.
    * @return If the ItemStack is a Writable Book.
    */
	public boolean isBookQuill(ItemStack item) {
		if (item != null && item.getType() != null && (item.getType().toString().equalsIgnoreCase("WRITABLE_BOOK") || item.getType().toString().equalsIgnoreCase("BOOK_AND_QUILL"))) {
			return true; 
		} 
		return false;
	}
	
   /**
    * Gets the instance of the ItemHandler.
    * 
    * @return The ItemHandler instance.
    */
    public static ItemHandler getItem() { 
        if (item == null) { item = new ItemHandler(); }
        return item; 
    } 
}