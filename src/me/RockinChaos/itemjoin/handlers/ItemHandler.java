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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
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
import me.RockinChaos.itemjoin.item.ItemUtilities.TriggerType;
import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.ReflectionUtils.MinecraftMethod;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.DependAPI;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

public class ItemHandler {
	
   /**
    * Adds a list of lores to the specified ItemStack.
    * 
    * @param item - The ItemStack to be modified.
    * @param lores - The list of lores to be added to the item.
    * @return The ItemStack with its newly added lores.
    */
	public static ItemStack addLore(final ItemStack item, final String... lores) {
		if (item != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			List<String> newLore = new ArrayList<String>();
			if (meta.hasLore()) { newLore = meta.getLore(); }
			for (String lore : lores) { newLore.add(StringUtils.colorFormat(lore)); }
			meta.setLore(newLore);
			item.setItemMeta(meta);
		}
		return item;
	}
	
   /**
    * Gets the exact name of the ItemStack Material with normal case and no underlines.
    * 
    * @param item - The ItemStack to have its Material name fetched.
    * @return A friendly String version of the Material name with normal case and no underlines.
    */
	public static String getMaterialName(final ItemStack item) {
		try {
			return WordUtils.capitalizeFully(item.getType().name().toLowerCase().replace('_', ' '));
		} catch (NullPointerException e) { ServerUtils.sendDebugTrace(e); }
		return null;
	}
	
   /**
    * Gets the String name of the Bukkit Enchantment.
    * 
    * @param enchant - The Enchantment to have its String name found.
    * @return The String name of the Bukkit Enchantment.
    */
	public static String getEnchantName(final Enchantment enchant) {
		if (!ServerUtils.hasSpecificUpdate("1_13")) { return LegacyAPI.getEnchantName(enchant); } 
		else { return enchant.getKey().getKey().toString(); }
	}
	
   /**
    * Gets the exact Bukkit Enchantment provided its String name.
    * 
    * @param name - Name of the Bukkit Enchantment.
    * @return The proper Bukkit Enchantment instance.
    */
	public static Enchantment getEnchantByName(final String name) {
		if (!ServerUtils.hasSpecificUpdate("1_13")) {
			Enchantment enchantName = LegacyAPI.getEnchant(name);
			if (enchantName != null) { return enchantName; }
		} else {
			try {
				Enchantment enchantName = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(name.toLowerCase()));
				if (enchantName != null) {
					return enchantName;
				} else { return LegacyAPI.getEnchant(name); }
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		}
		return null;
	}
	
   /**
    * Gets the current Durability Value of the specified ItemStack.
    * 
    * @param item - The ItemStack to have its Durability found.
    * @return The Durability value of the ItemStack.
    */
	public static short getDurability(final ItemStack item) {
		if (!ServerUtils.hasSpecificUpdate("1_13")) {
			return LegacyAPI.getDurability(item);
		} else { return ((short) ((org.bukkit.inventory.meta.Damageable) item.getItemMeta()).getDamage()); }
	}
	
   /**
    * Sets the specified Durability to the specified ItemStack.
    * 
    * @param item - The ItemStack to have its Durability changed.
    * @param durability - The Durability to be set to the ItemStack.
    * @return The ItemStack with its new Durability.
    */
	public static ItemStack setDurability(final ItemStack item, final int durability) {
		if (item.getType().getMaxDurability() != 0 && durability != 0) {
			if (ServerUtils.hasSpecificUpdate("1_13")) {
				ItemMeta tempMeta = item.getItemMeta();
				((org.bukkit.inventory.meta.Damageable) tempMeta).setDamage(durability);
				item.setItemMeta(tempMeta);
				return item;
			} else {
				return LegacyAPI.setDurability(item, (short)durability);
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
	public static ItemStack modifyItem(final ItemStack itemCopy, final boolean allItems, final int amount) {
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
    public static ItemStack getItem(String material, final int count, final boolean glowing, String name, final String... lores) {
        ItemStack tempItem; if (!ServerUtils.hasSpecificUpdate("1_8") && material.equals("BARRIER")) { material = "WOOL:14"; }
        if (material.equalsIgnoreCase("AIR") || material.equalsIgnoreCase("AIR:0")) { material = "GLASS_PANE"; }
        if (getMaterial(material, null) == null) { material = "STONE"; } 
        if (ServerUtils.hasSpecificUpdate("1_13")) { tempItem = new ItemStack(getMaterial(material, null), count); } 
        else { short dataValue = 0; if (material.contains(":")) { String[] parts = material.split(":"); material = parts[0]; dataValue = (short) Integer.parseInt(parts[1]); } 
        tempItem = LegacyAPI.newItemStack(getMaterial(material, null), count, dataValue); }
        if (glowing) { tempItem.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1); }
        ItemMeta tempMeta = tempItem.getItemMeta();
        if (ServerUtils.hasSpecificUpdate("1_8")) { tempMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS); }
        if (name != null) { name = StringUtils.colorFormat(name); tempMeta.setDisplayName(name); }
        if (lores != null && lores.length != 0) {
        	ArrayList<String> loreList = new ArrayList<String>();
        	for (String loreString: lores) { 
        		if (!loreString.isEmpty()) {
        			if (loreString.contains("/n")) {
        				String[] loreSplit = loreString.split(" /n ");
        				for (String loreStringSplit : loreSplit) { loreList.add(StringUtils.colorFormat(loreStringSplit)); }
        			} else { loreList.add(StringUtils.colorFormat(loreString)); }
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
	public static ItemStack getItem(final Player player, final ItemMap itemMap) {
		int craftSlot = StringUtils.getSlotConversion(itemMap.getSlot());
		ItemStack existingItem = null;
		if (StringUtils.isInt(itemMap.getSlot())) {
			existingItem = player.getInventory().getItem(Integer.parseInt(itemMap.getSlot()));
		} else if (itemMap.getSlot().contains("%")) {
			String slot = StringUtils.translateLayout(itemMap.getSlot(), player);
			if (StringUtils.isInt(slot)) {
				existingItem = player.getInventory().getItem(Integer.parseInt(slot));
			}
		} else if (CustomSlot.HELMET.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getHelmet();
		} else if (CustomSlot.CHESTPLATE.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getChestplate();
		} else if (CustomSlot.LEGGINGS.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getLeggings();
		} else if (CustomSlot.BOOTS.isSlot(itemMap.getSlot())) {
			existingItem = player.getEquipment().getBoots();
		} else if (ServerUtils.hasSpecificUpdate("1_9") && CustomSlot.OFFHAND.isSlot(itemMap.getSlot())) {
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
	public static Material getMaterial(String material, String data) {
		try {
			boolean isLegacy = (data != null);
			if (material.contains(":")) { String[] parts = material.split(":"); material = parts[0]; if (!parts[1].equalsIgnoreCase("0")) { data = parts[1]; isLegacy = true; } }
			if (StringUtils.isInt(material) && !ServerUtils.hasSpecificUpdate("1_13")) {
				return LegacyAPI.findMaterial(Integer.parseInt(material));
			} else if (StringUtils.isInt(material) && ServerUtils.hasSpecificUpdate("1_13") || isLegacy && ServerUtils.hasSpecificUpdate("1_13")) {
				int dataValue;
				if (!StringUtils.isInt(material)) { material = "LEGACY_" + material; }
				if (data != null) { dataValue = Integer.parseInt(data); } else { dataValue = 0; }
				if (!StringUtils.isInt(material)) { return LegacyAPI.getMaterial(Material.getMaterial(material.toUpperCase()), (byte) dataValue); } 
				else { return LegacyAPI.getMaterial(Integer.parseInt(material), (byte) dataValue); }
			} else if (!ServerUtils.hasSpecificUpdate("1_13")) {
				return Material.getMaterial(material.toUpperCase());
			} else {
				return Material.matchMaterial(material.toUpperCase());
			}
		} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		return null;
	}
	
   /**
    * Gets the material as defined in the ConfigurationSection.
    * 
    * @param itemNode - The node ConfigurationSection (location) of the config section.
    * @return The material exactly as found in the Configuration Section, without any animation delay.
    */
	public static String getMaterial(final ConfigurationSection itemNode) {
		String material = cutDelay(itemNode.getString(".id"));
		if (ConfigHandler.getConfig().getMaterialSection(itemNode) != null) {
			List<String> materials = new ArrayList<String>();
			for (String materialKey : ConfigHandler.getConfig().getMaterialSection(itemNode).getKeys(false)) {
				String materialList = itemNode.getString(".id." + materialKey);
				if (materialList != null) {
					materials.add(materialList);
				}
			}
			material = cutDelay(itemNode.getString(".id." + ConfigHandler.getConfig().getMaterialSection(itemNode).getKeys(false).iterator().next()));
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
	public static boolean containsMaterial(final Collection<ItemStack> itemstacks, final Material material) {
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
    public static ItemStack setSkullTexture(final ItemStack item, final String skullTexture) {
    	try {
    		if (ServerUtils.hasSpecificUpdate("1_8")) {
		        ItemMeta itemMeta = item.getItemMeta();
				GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
				gameProfile.getProperties().put("textures", new Property("textures", new String(skullTexture)));
				Field declaredField = itemMeta.getClass().getDeclaredField("profile");
				declaredField.setAccessible(true);
				declaredField.set(itemMeta, gameProfile);
				item.setItemMeta(itemMeta);
    		}
    	} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
    	return item;
    }
    
   /**
    * Sets the Skull Texture to the ItemStack.
    * 
    * @param item - The ItemStack to have its Skull Texture changed.
    * @param skullTexture - The Skull Texture to be added to the ItemStack.
    */
    public static ItemMeta setSkullTexture(final ItemMeta itemMeta, final String skullTexture) {
    	try {
    		if (ServerUtils.hasSpecificUpdate("1_8")) {
				GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
				gameProfile.getProperties().put("textures", new Property("textures", new String(skullTexture)));
				Field declaredField = itemMeta.getClass().getDeclaredField("profile");
				declaredField.setAccessible(true);
				declaredField.set(itemMeta, gameProfile);
    		}
    	} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
    	return itemMeta;
    }
	
   /**
    * Gets the current Skull Texture of the ItemMeta.
    * 
    * @param meta - The ItemMeta to have its Skull Texture found.
    * @return The found Skull Texture String value.
    */
	public static String getSkullTexture(final ItemMeta meta) {
		try {
			final Class < ? > cls = ReflectionUtils.getCraftBukkitClass("inventory.CraftMetaSkull");
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
    * Gets the current Skull Texture of the ItemMeta.
    * 
    * @param skull - The Skull to have its Skull Texture found.
    * @return The found Skull Texture String value.
    */
	public static String getSkullTexture(final Skull skull) {
		try {
			final Field field = skull.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			final GameProfile profile = (GameProfile) field.get(skull);
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
	public static ItemMeta setSkullOwner(final ItemMeta meta, final String owner) {
		if (!ServerUtils.hasSpecificUpdate("1_8")) {
			ServerUtils.logDebug("{ItemHandler} Minecraft does not support offline player heads below Version 1.8.");
			ServerUtils.logDebug("{ItemHandler} Player heads will only be given a skin if the player has previously joined the sever.");
		}
		setStoredSkull(meta, owner);
		return meta;
	}
	
   /**
    * Sets the locale stored skull owner.
    * 
    * @param meta - The referenced ItemMeta.
    * @param owner - The referenced Skull Owner
    */
	public static void setStoredSkull(final ItemMeta meta, final String owner) {
		if (!owner.isEmpty()) {
			SkullMeta skullMeta = (SkullMeta)meta;
			OfflinePlayer player = LegacyAPI.getOfflinePlayer(owner);
			if (DependAPI.getDepends(false).skinsRestorerEnabled()) {
				final String textureValue = DependAPI.getDepends(false).getSkinValue(owner);
				if (textureValue != null) {
					setSkullTexture(meta, textureValue);
				} else if (player != null) {
					try {
						skullMeta.setOwningPlayer(player);
					} catch (Throwable t) {
						LegacyAPI.setSkullOwner(skullMeta, player.getName());
					}
				} else {
					LegacyAPI.setSkullOwner(skullMeta, owner);
				}
			} else if (player != null) {
				try {
					skullMeta.setOwningPlayer(player);
				} catch (Throwable t) {
					LegacyAPI.setSkullOwner(skullMeta, player.getName());
				}
			} else {
				LegacyAPI.setSkullOwner(skullMeta, owner);
			}
		}
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
	public static int stackItems(final Player player, final ItemStack item1, final ItemStack item2, final int slot) {
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
	public static MapView existingView(final int id) {
		MapView view = LegacyAPI.getMapView(id);
		if (view == null) { view = LegacyAPI.createMapView(); }
		return view;
	}
	
   /**
    * Saves any existing players that are on cooldown for each item.
    * 
    */
	public static void saveCooldowns() {
		for (ItemMap itemMap: ItemUtilities.getUtilities().getItems()) {
			for (String key: itemMap.getPlayersOnCooldown().keySet()) {
				if (System.currentTimeMillis() - itemMap.getPlayersOnCooldown().get(key) <= itemMap.getCommandCooldown() * 1000) {
					SQL.getData().saveData(new DataObject(Table.ON_COOLDOWN, key, "GLOBAL", itemMap.getConfigName(), itemMap.getCommandCooldown().toString(), itemMap.getPlayersOnCooldown().get(key).toString()));
				}
			}
		}
	}
	
   /**
    * Removes the Crafting Items from ALL Online Players.
    * 
    * @param saveCrafting - If the Crafting Items should be saved to be returned later.
    */
	public static void purgeCraftItems(final boolean saveCrafting) {
		PlayerHandler.forOnlinePlayers(player -> {
			if (saveCrafting) { saveCraftItems(player); }
			removeCraftItems(player); 
		});
	}
    
   /**
    * Saves the players current crafting items for later retrieval.
    * 
    * @param player - The Player to have its crafting items saved.
    */
    public static void saveCraftItems(final Player player) {
			if (PlayerHandler.getCreativeCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
				Inventory inv = Bukkit.createInventory(null, 9);
				boolean notNull = false;
				ItemStack[] craftingContents = PlayerHandler.getCreativeCraftItems().get(PlayerHandler.getPlayerID(player));
				for (int k = 0; k <= 4; k++) {
					if (craftingContents != null && craftingContents[k] != null) {
						inv.setItem(k, craftingContents[k]); 
						if (craftingContents[k] != null && craftingContents[k].getType() != Material.AIR) { notNull = true; }
					}
				}
				if (notNull) { SQL.getData().saveData(new DataObject(Table.RETURN_CRAFTITEMS, PlayerHandler.getPlayerID(player), "", serializeInventory(inv))); }
			} else if (PlayerHandler.getOpenCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
				Inventory inv = Bukkit.createInventory(null, 9);
				boolean notNull = false;
				ItemStack[] craftingContents = PlayerHandler.getOpenCraftItems().get(PlayerHandler.getPlayerID(player));
				for (int k = 0; k <= 4; k++) {
					if (craftingContents != null && craftingContents[k] != null) {
						inv.setItem(k, craftingContents[k]); 
						if (craftingContents[k] != null && craftingContents[k].getType() != Material.AIR) { notNull = true; }
					}
				}
				if (notNull) { SQL.getData().saveData(new DataObject(Table.RETURN_CRAFTITEMS, PlayerHandler.getPlayerID(player), "", serializeInventory(inv))); }
			} else if (PlayerHandler.getCraftItems().containsKey(PlayerHandler.getPlayerID(player))) {
				Inventory inv = Bukkit.createInventory(null, 9);
				boolean notNull = false;
				ItemStack[] craftingContents = PlayerHandler.getCraftItems().get(PlayerHandler.getPlayerID(player));
				for (int k = 0; k <= 4; k++) {
					if (craftingContents != null && craftingContents[k] != null) {
						inv.setItem(k, craftingContents[k]); 
						if (craftingContents[k] != null && craftingContents[k].getType() != Material.AIR) { notNull = true; }
					}
				}
				if (notNull) { SQL.getData().saveData(new DataObject(Table.RETURN_CRAFTITEMS, PlayerHandler.getPlayerID(player), "", serializeInventory(inv))); }
			}
    }
    
   /**
    * Restores the players crafting items to their inventory if they were previously saved.
    * 
    * @param player - The Player to have its crafting items restored.
    */
    public static void restoreCraftItems(final Player player, final TriggerType type) {
    	if (!type.equals(TriggerType.QUIT)) {
	    	DataObject dataObject = SQL.getData().getData(new DataObject(Table.RETURN_CRAFTITEMS, PlayerHandler.getPlayerID(player), "", ""));
	    	Inventory inventory = (dataObject != null ? deserializeInventory(dataObject.getInventory64()) : null);
			Inventory craftView = player.getOpenInventory().getTopInventory();
			if (inventory != null && PlayerHandler.isCraftingInv(player.getOpenInventory())) {
				for (int k = 4; k >= 0; k--) {
					if (inventory.getItem(k) != null && inventory.getItem(k).getType() != Material.AIR) {
					 craftView.setItem(k, inventory.getItem(k).clone());
					}
				}
				PlayerHandler.updateInventory(player, 1L);
				SQL.getData().removeData(dataObject);
			} else if (!PlayerHandler.isCraftingInv(player.getOpenInventory())) {
				SchedulerUtils.runLater(60L, () -> restoreCraftItems(player, type));
			}
    	}
    }
    
   /**
    * Removes all crafting items from the players inventory.
    * 
    * @param player - The Player to have their crafting items removed.
    */
    public static void removeCraftItems(final Player player) {
    	ItemStack[] craftingContents = player.getOpenInventory().getTopInventory().getContents();
	    Inventory craftView = player.getOpenInventory().getTopInventory();
	    if (PlayerHandler.isCraftingInv(player.getOpenInventory())) {
	    	for (int k = 0; k < craftingContents.length; k++) {
	    		craftView.setItem(k, new ItemStack(Material.AIR));
	    	}
	    }
    }
    
   /**
    * Returns the custom crafting item to the player after the specified delay.
    * 
    * @param player - the Player having their item returned.
    * @param slot - the slot to return the crafting item to.
    * @param item - the item to be returned.
    * @param delay - the delay to wait before returning the item.
    */
    public static void returnCraftingItem(final Player player, final int slot, final ItemStack item, long delay) {
    	if (item == null) { return; } if (slot == 0) { delay += 1L; }
    	SchedulerUtils.runLater(delay, () -> {
	    	if (!player.isOnline()) { return; }
	    	if (PlayerHandler.isCraftingInv(player.getOpenInventory()) && player.getGameMode() != GameMode.CREATIVE) {
	    	    player.getOpenInventory().getTopInventory().setItem(slot, item);	
	    	    PlayerHandler.updateInventory(player, 1L);
	    	} else {
	    		returnCraftingItem(player, slot, item, 10L);
	    	}
	    });
    }
    
   /**
    * Copies the specified ItemStack contents.
    * 
    * @param contents - The ItemStack contents to be copied.
    * @return The copied ItemStack contents.
    */
    public static ItemStack[] cloneContents(final ItemStack[] contents) {
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
    public static boolean isContentsEmpty(final ItemStack[] contents) {
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
	public static String serializeInventory(final Inventory inventory) {
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
	public static Inventory deserializeInventory(final String inventoryData) {
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
	        ServerUtils.sendDebugTrace(e);
	    }
	    return null;
	}
	
   /**
    * Gets the custom NBTData of the ItemStack.
    * 
    * @param item - The ItemStack to have its custom NBTData found.
    * @return The String of NBTData found on the ItemStack.
    */
	public static String getNBTData(final ItemStack item) {
		if (dataTagsEnabled() && item != null && item.getType() != Material.AIR) {
			try {
				Object nms = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
				Class<?> itemClass = ReflectionUtils.getMinecraftClass("ItemStack");
				Object cacheTag = itemClass.getMethod(MinecraftMethod.getTag.getMethod(itemClass)).invoke(nms);
				if (cacheTag != null) {
					String data = (String) cacheTag.getClass().getMethod(MinecraftMethod.getString.getMethod(cacheTag, String.class), String.class).invoke(cacheTag, "ItemJoin");
					String data1 = (String) cacheTag.getClass().getMethod(MinecraftMethod.getString.getMethod(cacheTag, String.class), String.class).invoke(cacheTag, "ItemJoin Name");
					String data2 = (String) cacheTag.getClass().getMethod(MinecraftMethod.getString.getMethod(cacheTag, String.class), String.class).invoke(cacheTag, "ItemJoin Slot");
					if (data1 != null && data2 != null && !data1.isEmpty() && !data2.isEmpty()) {
						return data1 + " " + data2;
					} else if (data != null && !data.isEmpty()) { 
						return data.replace("Slot: ", "");
					}
				}
			} catch (Exception e) {
				ServerUtils.logSevere("{ItemHandler} An error has occured when getting NBTData to an item.");
				ServerUtils.sendDebugTrace(e);
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
	public static boolean containsNBTData(final ItemStack item) {
		if (dataTagsEnabled() && item != null && item.getType() != Material.AIR && getNBTData(item) != null) {
			return true;
		} else if (!dataTagsEnabled()) { 
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
				&& StringUtils.colorDecode(item) != null && !StringUtils.colorDecode(item).isEmpty()) {
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
	public static String getDesignatedSlot(final Material material) {
		String name = material.name().contains("_") ? material.name().split("_")[1] : material.name();
		String hand = (ServerUtils.hasSpecificUpdate("1_13") ? "hand" : "mainhand");
		return (name != null ? (name.equalsIgnoreCase("HELMET") ? "head" : name.equalsIgnoreCase("CHESTPLATE") ? "chest" : name.equalsIgnoreCase("LEGGINGS") ? "legs" : name.equalsIgnoreCase("BOOTS") ? "feet" : 
			    name.equalsIgnoreCase("HOE") ? hand : name.equalsIgnoreCase("SWORD") ? hand : name.equalsIgnoreCase("SHOVEL") ? hand : name.equalsIgnoreCase("AXE") ? hand : name.equalsIgnoreCase("PICKAXE") ? hand : "noslot") : "noslot");
	}
	
   /**
    * Checks if NBTData is enabled for the current server version.
    * 
    * @return If NBTData is enabled.
    */
	public static boolean dataTagsEnabled() {
		if (ServerUtils.hasSpecificUpdate("1_8")) {
			return ConfigHandler.getConfig().getFile("config.yml").getBoolean("Settings.DataTags");
		}
		return false;
	}
	
   /**
    * Checks if the server is using the new skull method.
    * 
    * @return If the server is using the new skull method.
    */
	public static boolean usesOwningPlayer() {
		try {
			if (Class.forName("org.bukkit.inventory.meta.SkullMeta").getMethod("getOwningPlayer") != null) { return true; }
		} catch (Exception e) { }
		return false;
	}
	
   /**
    * Gets the Integer Delay of the specified String.
    * 
    * @param context - The String to have the Delay found.
    * @return The Delay of the String as an Integer.
    */
	public static int getDelay(final String context) {
		try { if (StringUtils.returnInteger(context) != null) { return StringUtils.returnInteger(context); } } 
		catch (Exception e) { ServerUtils.sendDebugTrace(e); }
		return 0;
	}
	
   /**
    * Gets the delay format including the proper delay Integer of the String.
    * 
    * @param context - The String to have the Delay found.
    * @return The Delay Format of the String with the proper Integer value.
    */
	public static String getDelayFormat(final String context) {
		if (StringUtils.containsIgnoreCase(context, "<delay:" + StringUtils.returnInteger(context) + ">") 
				|| StringUtils.containsIgnoreCase(context, "delay:" + StringUtils.returnInteger(context) + "") 
				|| StringUtils.containsIgnoreCase(context, "<delay: " + StringUtils.returnInteger(context) + ">")
				|| StringUtils.containsIgnoreCase(context, "delay: " + StringUtils.returnInteger(context) + "")) {
			return ("<delay:" + StringUtils.returnInteger(context) + ">");
		}
		return null;
	}
	
   /**
    * Removes the delay formatting from the specified String.
    * 
    * @param context - The String to have the Delay Formatting removed.
    * @return The String with the removed Delay Formatting.
    */
	public static String cutDelay(final String context) {
		if (getDelayFormat(context) != null) {
			return context.replace(getDelayFormat(context), "");
		} 
		return context;
	}
	
   /**
    * Removes the delay formatting from the specified String List.
    * 
    * @param context - The String List to have the Delay Formatting removed.
    * @return The String List with the removed Delay Formatting.
    */
	public static List<String> cutDelay(final List <String> context) {
		List<String> newContext = new ArrayList<String>();
		for (String minorContext : context) {
			newContext.add(cutDelay(minorContext));
		}
		return newContext;
	}
	
   /**
	* Checks if the book pages contains a JSONEvent.
	* 
	* @param formatPage - The page to be formatted..
	* @return If the book page contains a JSONEvent.
	*/
	public static boolean containsJSONEvent(final String formatPage) {
		if (formatPage.contains(JSONEvent.TEXT.matchType) || formatPage.contains(JSONEvent.SHOW_TEXT.matchType) || formatPage.contains(JSONEvent.OPEN_URL.matchType) || formatPage.contains(JSONEvent.RUN_COMMAND.matchType)) {
			return true;
		}
		return false;
	}
	
   /**
	* Checks to see if the open_url is correctly defined with https or http.
	* 
	* @param itemMap - The ItemMap being modified.
	* @param type - The JSONEvent type.
	* @param inputResult - The input for the JSONEvent.
	*/
	public static void safteyCheckURL(final ItemMap itemMap, final JSONEvent type, final String inputResult) {
		if (type.equals(JSONEvent.OPEN_URL)) {
			if (!StringUtils.containsIgnoreCase(inputResult, "https") && !StringUtils.containsIgnoreCase(inputResult, "http")) {
				ServerUtils.logSevere("{ItemHandler} The URL Specified for the clickable link in the book " + itemMap.getConfigName() + " is missing http or https and will not be clickable.");
				ServerUtils.logWarn("{ItemHandler} A URL designed for a clickable link should resemble this link structure: https://www.google.com/");
			}
		}
	}
	
	
   /**
	* Defines the JSONEvents for their action, event, and matchType.
	* 
	*/
	public enum JSONEvent {
		TEXT("nullEvent", "text", "<text:"),
		SHOW_TEXT("hoverEvent", "show_text", "<show_text:"),
		OPEN_URL("clickEvent", "open_url", "<open_url:"),
		RUN_COMMAND("clickEvent", "run_command", "<run_command:"),
		CHANGE_PAGE("clickEvent", "change_page", "<change_page:");
		public final String event;
		public final String action;
		public final String matchType;
		private JSONEvent(String Event, String Action, String MatchType) {
			this.event = Event;
			this.action = Action;
			this.matchType = MatchType;
		}
	}
	
   /**
    * Checks if the specified String slot is a Custom Slot.
    * 
    * @param slot - The slot to be checked.
    * @return If the slot is a custom slot.
    */
	public static boolean isCustomSlot(final String slot) {
		if (slot.equalsIgnoreCase("Offhand") || slot.equalsIgnoreCase("Arbitrary") || slot.equalsIgnoreCase("Helmet") 
				|| slot.equalsIgnoreCase("Chestplate") || slot.equalsIgnoreCase("Leggings") || slot.equalsIgnoreCase("Boots") || isCraftingSlot(slot) || slot.contains("%")) {
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
	public static boolean isCraftingSlot(final String slot) {
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
	public static boolean isSkull(final Material material) {
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
	public static boolean isBookQuill(ItemStack item) {
		if (item != null && item.getType() != null && (item.getType().toString().equalsIgnoreCase("WRITABLE_BOOK") || item.getType().toString().equalsIgnoreCase("BOOK_AND_QUILL"))) {
			return true; 
		} 
		return false;
	}
}