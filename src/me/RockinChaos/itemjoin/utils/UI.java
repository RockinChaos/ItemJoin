package me.RockinChaos.itemjoin.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemCommand;
import me.RockinChaos.itemjoin.giveitems.utils.ItemCommand.ActionType;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap.CommandSequence;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap.CommandType;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.PlayerHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.interfaces.Button;
import me.RockinChaos.itemjoin.utils.interfaces.Interface;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

public class UI {
	private String GUIName = Utils.colorFormat("&7           &0&n ItemJoin Menu");
	private ItemStack fillerPaneBItem = ItemHandler.getItem("STAINED_GLASS_PANE:15", 1, false, "&7", "");
	private ItemStack fillerPaneGItem = ItemHandler.getItem("STAINED_GLASS_PANE:7", 1, false, "&7", "");
	private ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, false, "&c&l&nExit", "&7", "&7*Returns you to the game");
	private List<Player> modifyMenu = new ArrayList<Player>();

//  ============================================== //
//  			   Selection Menus      	       //
//	============================================== //
	
	public void startMenu(final CommandSender sender) {
		final Player player = (Player) sender;
		if (!ServerHandler.hasCombatUpdate()) {
			this.GUIName = Utils.colorFormat("&7           &0&n ItemJoin Menu");
		}
		Interface pagedPane = new Interface(false, 1, this.GUIName);
		pagedPane.addButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.addButton(new Button(this.fillerPaneBItem), 2);
		pagedPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WRITABLE_BOOK" : "386"), 1, false, "&a&l&nCreate", "&7", "&7*Create a new item from scratch."),
				event -> this.materialPane(player, new ItemMap("item_" + Utils.getPath(1), "ARBITRARY"), 0)));
		pagedPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, "&e&l&nSave", "&7", "&7*Save an existing item as a custom item."), event -> this.startHopper(player)));
		pagedPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&c&l&nModify", "&7", "&7*Modify an existing custom item"), event -> this.startModify(player)));
		pagedPane.addButton(new Button(this.fillerPaneBItem), 2);
		pagedPane.addButton(new Button(this.exitItem, event -> player.closeInventory()));
		pagedPane.open(player);
	}
	
	private void startModify(final Player player) {
		Interface modifyPane = new Interface(true, 6, this.GUIName);
		this.setPage(player, modifyPane, ItemUtilities.copyItems());
		modifyPane.open(player);
	}
	
	private void startHopper(Player player) {
		Interface dragDrop = new Interface(false, 1, this.GUIName);
		dragDrop.allowClick(true);
		dragDrop.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu&a&c&b&9&0&a&c&b&7."), event -> this.startMenu(player)));
		dragDrop.addButton(new Button(this.fillerPaneGItem), 3);
		dragDrop.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, "&a&lDrop Item", "&7", "&7*Click an item from your inventory", "&7to save and drop it in this", "&7friendly little hopper&a&c&b&9&0&a&c&b&7!"), event -> {
			if (event.getCursor().getType() != Material.AIR) {
				ItemStack item = event.getCursor().clone();
				event.getWhoClicked().setItemOnCursor(null);
				event.getWhoClicked().getInventory().addItem(item);
				this.convertStack(player, item);
				dragDrop.allowClick(false);
			}
		}));
		dragDrop.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:7", 1, false, "&a&c&b&9&0&a&c&b&7", "")), 3);
		dragDrop.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu&a&c&b&9&0&a&c&b&7."), event -> this.startMenu(player)));
		dragDrop.open(player);
	}
	
//  ===================================================================================================================================================================================================================
	
//  ============================================== //
//  			   Menu Utilities        	       //
//  ============================================== //
	
	private void setButton(final Player player, final ItemMap itemMap, final Interface pagedPane) {
		final ItemStack item = itemMap.getTempItem().clone();
		if (itemMap.isAnimated() || itemMap.isDynamic()) { this.setModifyMenu(true, player); itemMap.getAnimationHandler().get(player).setMenu(true, 0); }
		pagedPane.addButton(new Button(ItemHandler.addLore(item, "&7", "&6---------------------------", "&7*Click to modify this custom item.", "&9&lNode: &a" + itemMap.getConfigName(), "&7"), event ->  this.choicePane(player, itemMap, item)));
	}
	
	private void setPage(final Player player, final Interface modifyPane, final List < ItemMap > items) {
		ItemMap currentItem = null;
		boolean crafting = false;
		boolean arbitrary = false;
		Interface craftingPane = new Interface(false, 4, this.GUIName);
		craftingPane.addButton(new Button(this.fillerPaneGItem), 3);
		currentItem = ItemHandler.getItemMap("CRAFTING[1]", items);
		if (currentItem != null) {
			crafting = true;
			this.setButton(player, currentItem, craftingPane);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(this.fillerPaneGItem));
		}
		craftingPane.addButton(new Button(this.fillerPaneGItem));
		currentItem = ItemHandler.getItemMap("CRAFTING[2]", items);
		if (currentItem != null) {
			crafting = true;
			this.setButton(player, currentItem, craftingPane);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(this.fillerPaneGItem));
		}
		craftingPane.addButton(new Button(this.fillerPaneGItem), 10);
		currentItem = ItemHandler.getItemMap("CRAFTING[0]", items);
		if (currentItem != null) {
			crafting = true;
			this.setButton(player, currentItem, craftingPane);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(this.fillerPaneGItem));
		}
		craftingPane.addButton(new Button(this.fillerPaneGItem), 4);
		currentItem = ItemHandler.getItemMap("CRAFTING[3]", items);
		if (currentItem != null) {
			crafting = true;
			this.setButton(player, currentItem, craftingPane);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(this.fillerPaneGItem));
		}
		craftingPane.addButton(new Button(this.fillerPaneGItem));
		currentItem = ItemHandler.getItemMap("CRAFTING[4]", items);
		if (currentItem != null) {
			crafting = true;
			this.setButton(player, currentItem, craftingPane);
			items.remove(currentItem);
		} else {
			craftingPane.addButton(new Button(this.fillerPaneGItem));
		}
		craftingPane.addButton(new Button(this.fillerPaneGItem), 3);
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			craftingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"),
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> modifyPane.open(player)));
		} else {
			craftingPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the modifying selection menu"), event -> modifyPane.open(player)));
		}
		craftingPane.addButton(new Button(this.fillerPaneBItem), 7);
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			craftingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), 
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> modifyPane.open(player)));
		} else {
			craftingPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the modifying selection menu"), event -> modifyPane.open(player)));
		}
		Interface arbitraryPane = new Interface(true, 6, this.GUIName);
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			arbitraryPane.setReturnButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the modifying selection menu"),
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> modifyPane.open(player)));
		} else {
			arbitraryPane.setReturnButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the modifying selection menu"), event -> modifyPane.open(player)));
		}
		List < ItemMap > tempList = new ArrayList < ItemMap > ();
		tempList.addAll(items);
		for (final ItemMap item: tempList) {
			if (item.getSlot().equalsIgnoreCase("ARBITRARY")) {
				this.setButton(player, item, arbitraryPane);
				items.remove(item);
				arbitrary = true;
			}
		}
		modifyPane.addButton(new Button(this.fillerPaneGItem));
		if (arbitrary == true) {
			modifyPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&fArbitrary", "&7", "&7*Click to view the existing", "&7Arbitrary slot items to modify."), event -> arbitraryPane.open(player)));
		} else {
			modifyPane.addButton(new Button(this.fillerPaneGItem));
		}
		if (crafting == true) {
			modifyPane.addButton(new Button(ItemHandler.getItem("58", 1, false, "&fCrafting", "&7", "&7*Click to view the existing", "&7crafting slot items to modify."), event -> craftingPane.open(player)));
		} else {
			modifyPane.addButton(new Button(this.fillerPaneGItem));
		}
		currentItem = ItemHandler.getItemMap("HELMET", items);
		if (currentItem != null) {
			this.setButton(player, currentItem, modifyPane);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(this.fillerPaneGItem));
		}
		currentItem = ItemHandler.getItemMap("CHESTPLATE", items);
		if (currentItem != null) {
			this.setButton(player, currentItem, modifyPane);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(this.fillerPaneGItem));
		}
		currentItem = ItemHandler.getItemMap("LEGGINGS", items);
		if (currentItem != null) {
			this.setButton(player, currentItem, modifyPane);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(this.fillerPaneGItem));
		}
		currentItem = ItemHandler.getItemMap("BOOTS", items);
		if (currentItem != null) {
			this.setButton(player, currentItem, modifyPane);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(this.fillerPaneGItem));
		}
		currentItem = ItemHandler.getItemMap("OFFHAND", items);
		if (currentItem != null) {
			this.setButton(player, currentItem, modifyPane);
			items.remove(currentItem);
		} else {
			modifyPane.addButton(new Button(this.fillerPaneGItem));
		}
		modifyPane.addButton(new Button(this.fillerPaneGItem));
		for (int i = 9; i < 36; i++) {
			currentItem = ItemHandler.getItemMap(i + "", items);
			if (currentItem != null) {
				this.setButton(player, currentItem, modifyPane);
				items.remove(currentItem);
			} else {
				modifyPane.addButton(new Button(this.fillerPaneGItem));
			}
		}
		for (int j = 0; j < 9; j++) {
			currentItem = ItemHandler.getItemMap(j + "", items);
			if (currentItem != null) {
				this.setButton(player, currentItem, modifyPane);
				items.remove(currentItem);
			} else {
				modifyPane.addButton(new Button(this.fillerPaneGItem));
			}
		}
		if (!items.isEmpty()) {
			this.setPage(player, modifyPane, items);
		}
	}
	
	private void convertStack(Player player, ItemStack item) {
		ItemMap itemMap = new ItemMap("item_" + Utils.getPath(1), "ARBITRARY");
		itemMap.setMaterial(item.getType());
		if (!ServerHandler.hasAquaticUpdate()) { itemMap.setDataValue((short)Legacy.getDataValue(item)); }
		itemMap.setCount(item.getAmount() + "");
		if (item.getType().getMaxDurability() > 30 && ItemHandler.getDurability(item) != 0 && ItemHandler.getDurability(item) != (item.getType().getMaxDurability())) {
			itemMap.setDurability(ItemHandler.getDurability(item));
		}
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				itemMap.setCustomName(item.getItemMeta().getDisplayName().replace("§", "&"));
			}
			if (item.getItemMeta().hasLore()) {
				List < String > newLore = new ArrayList < String > ();
				for (String lore: item.getItemMeta().getLore()) {
					newLore.add(lore.replace("§", "&"));
				}
				itemMap.setCustomLore(newLore);
			}
			if (item.getItemMeta().hasEnchants()) {
				Map < String, Integer > enchantList = new HashMap < String, Integer > ();
				for (Enchantment e: item.getItemMeta().getEnchants().keySet()) {
					enchantList.put(ItemHandler.getEnchantName(e).toUpperCase(), item.getItemMeta().getEnchants().get(e));
				}
				itemMap.setEnchantments(enchantList);
			}
		}
		if (Utils.containsIgnoreCase(item.getType().toString(), "LEATHER_")) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			if (meta.getColor() != null) {
				itemMap.setLeatherColor(DyeColor.getByColor(meta.getColor()).name());
			}
		} else if (item.getType().toString().equalsIgnoreCase("SKULL_ITEM") || item.getType().toString().equalsIgnoreCase("PLAYER_HEAD")) {
			if (!PlayerHandler.getSkullOwner(item).equalsIgnoreCase("NULL")) {
				itemMap.setSkull(PlayerHandler.getSkullOwner(item));
			} else if (!ItemHandler.getSkullSkinTexture(item.getItemMeta()).isEmpty()) {
				itemMap.setSkullTexture(ItemHandler.getSkullSkinTexture(item.getItemMeta()));
			}
		} else if (itemMap.getMaterial().toString().contains("CHARGE") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_STAR")) {
			itemMap.setChargeColor(DyeColor.getByFireworkColor(((FireworkEffectMeta) item.getItemMeta()).getEffect().getColors().get(0)));
		} else if (itemMap.getMaterial().toString().contains("BANNER")) {
			itemMap.setBannerPatterns(((BannerMeta) item.getItemMeta()).getPatterns());
		} else if (itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
			List < DyeColor > colors = new ArrayList < DyeColor > ();
			for (Color color: ((FireworkMeta) item.getItemMeta()).getEffects().get(0).getColors()) {
				colors.add(DyeColor.getByFireworkColor(color));
			}
			itemMap.setFirework(((FireworkMeta) item.getItemMeta()).getEffects().get(0));
			itemMap.setFireworkColor(colors);
			itemMap.setFireworkFlicker(((FireworkMeta) item.getItemMeta()).getEffects().get(0).hasFlicker());
			itemMap.setFireworkTrail(((FireworkMeta) item.getItemMeta()).getEffects().get(0).hasTrail());
			itemMap.setFireworkType(((FireworkMeta) item.getItemMeta()).getEffects().get(0).getType());
			itemMap.setFireworkPower(((FireworkMeta) item.getItemMeta()).getPower());
		} else if (itemMap.getMaterial() == Material.WRITTEN_BOOK) {
			itemMap.setAuthor(((BookMeta) item.getItemMeta()).getAuthor().replace("§", "&"));
			itemMap.setTitle(((BookMeta) item.getItemMeta()).getTitle().replace("§", "&"));
			if (ServerHandler.hasSpecificUpdate("1_10")) {
				itemMap.setGeneration(((BookMeta) item.getItemMeta()).getGeneration());
			}
			List < String > newPages = new ArrayList < String > ();
			for (String page: ((BookMeta) item.getItemMeta()).getPages()) {
				newPages.add(page.replace("§", "&"));
			}
			itemMap.setPages(newPages);
			List < List < String >> savePages = new ArrayList < List < String >> ();
			for (String page: ((BookMeta) item.getItemMeta()).getPages()) {
				List < String > pageList = new ArrayList < String > ();
				for (String splitPage: page.split("\n")) {
					pageList.add(splitPage.replace("§", "&"));
				}
				savePages.add(pageList);
			}
			itemMap.setListPages(savePages);
		}
		this.switchPane(player, itemMap, 0);
	}
	
	private void choicePane(final Player player, final ItemMap itemMap, final ItemStack item) {
		Interface choicePane = new Interface(false, 3, this.GUIName);
		choicePane.addButton(new Button(this.fillerPaneBItem), 4);
		choicePane.addButton(new Button(item));
		choicePane.addButton(new Button(this.fillerPaneBItem), 4);
		choicePane.addButton(new Button(this.fillerPaneBItem), 3);
		choicePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, true, "&b&lSettings", "&7", "&7*Change the settings for this item.", "&7Make changes to the item name, lore,", "&7permissions, enabled-worlds, and more."), event -> {
			this.creatingPane(player, itemMap);
		}));
		choicePane.addButton(new Button(this.fillerPaneBItem));
		choicePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, true, "&c&lDelete", "&7", "&7*Delete this item.", "&7This will remove the item from the", "&7items.yml and will no longer be useable.", "&c&lWARNING: &7This &lCANNOT &7be undone!"), event -> {
			itemMap.removeFromConfig();
			String[] placeHolders = Language.newString();
			placeHolders[3] = itemMap.getConfigName();
			Language.sendLangMessage("Commands.UI.itemRemoved", player, placeHolders);
			ConfigHandler.getSQLData().executeLaterStatements();
			ItemUtilities.closeAnimations();
			ItemUtilities.clearItems();
			ConfigHandler.generateData(null);
			player.closeInventory();
		}));
		choicePane.addButton(new Button(this.fillerPaneBItem), 3);
		choicePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the modify menu"), event -> this.startModify(player)));
		choicePane.addButton(new Button(this.fillerPaneBItem), 7);
		choicePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the modify menu"), event -> this.startModify(player)));
		choicePane.open(player);
	}
	
	private void creatingPane(final Player player, final ItemMap itemMap) {
		Interface creatingPane = new Interface(false, 4, this.GUIName);
		String slotList = "";
		String slotString = "";
		if (Utils.nullCheck(itemMap.getMultipleSlots().toString()) != "NONE") {
			for (String slot: itemMap.getMultipleSlots()) {
				slotString += slot + ", ";
			}
			for (String split: Utils.softSplit(Utils.nullCheck(slotString.substring(0, slotString.length() - 2)))) {
				slotList += "&a" + split + " /n ";
			}
		}
		String itemflagsList = "";
		if (Utils.nullCheck(itemMap.getItemFlags()) != "NONE") {
			for (String split: Utils.softSplit(itemMap.getItemFlags())) {
				itemflagsList += "&a" + split + " /n ";
			}
		}
		String triggersList = "";
		if (Utils.nullCheck(itemMap.getTriggers()) != "NONE") {
			for (String split: Utils.softSplit(itemMap.getTriggers())) {
				triggersList += "&a" + split + " /n ";
			}
		}
		String worldList = "";
		if (Utils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE") {
			for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getEnabledWorlds().toString()))) {
				worldList += "&a" + split + " /n ";
			}
		}
		String regionList = "";
		if (Utils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE") {
			for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getEnabledRegions().toString()))) {
				regionList += "&a" + split + " /n ";
			}
		}
		String enchantList = "";
		if (Utils.nullCheck(itemMap.getEnchantments().toString()) != "NONE") {
			for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getEnchantments().toString()))) {
				enchantList += "&a" + split + " /n ";
			}
		}
		String potionList = "";
		String potionString = "";
		if (Utils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
			for (PotionEffect potions: itemMap.getPotionEffect()) {
				potionString += potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + potions.getDuration() / 160 + ", ";
			}
			for (String split: Utils.softSplit(Utils.nullCheck(potionString.substring(0, potionString.length() - 2)))) {
				potionList += "&a" + split + " /n ";
			}
		}
		String patternList = "";
		String patternString = "";
		if (Utils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
			for (Pattern patterns: itemMap.getBannerPatterns()) {
				patternString += patterns.getColor() + ":" + patterns.getPattern().name().toUpperCase() + ", ";
			}
			for (String split: Utils.softSplit(Utils.nullCheck(patternString.substring(0, patternString.length() - 2)))) {
				patternList += "&a" + split + " /n ";
			}
		}
		creatingPane.addButton(new Button(this.fillerPaneGItem), 4);
		creatingPane.addButton(new Button(this.headerStack(player, itemMap)));
		creatingPane.addButton(new Button(this.fillerPaneGItem), 4);
		creatingPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString() + ":" + itemMap.getDataValue(), 1, false, "&c&lMaterial", "&7", "&7*Set the material of the item.", "&9&lMATERIAL: &a" + 
		itemMap.getMaterial().toString() + (itemMap.getDataValue() != 0 ? ":" + itemMap.getDataValue() : "")), event -> { 
			if (itemMap.getDynamicMaterials() != null && !itemMap.getDynamicMaterials().isEmpty()) {
				this.animateMaterialPane(player, itemMap);
			} else {
				this.materialPane(player, itemMap, 1);
			}
			}));
		creatingPane.addButton(new Button(ItemHandler.getItem("GLASS", 1, false, "&c&lSlot", "&7", "&7*Set the slot that the", "&7item will be given in.", (itemMap.getMultipleSlots() != null && 
				!itemMap.getMultipleSlots().isEmpty() ? "&9&lSlot(s): &a" + slotList : "&9&lSlot: &a" + itemMap.getSlot().toUpperCase())), event -> this.switchPane(player, itemMap, 1)));
		creatingPane.addButton(new Button(ItemHandler.getItem("DIAMOND", itemMap.getCount(), false, "&b&lCount", "&7", "&7*Set the amount of the", "&7item to be given.", "&9&lCOUNT: &a" + 
				itemMap.getCount()), event -> this.countPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&b&lName", "&7", "&7*Set the name of the item.", "&9&lNAME: &f" + Utils.nullCheck(itemMap.getCustomName())), event -> {
			if (itemMap.getDynamicNames() != null && !itemMap.getDynamicNames().isEmpty()) {
				this.animatedNamePane(player, itemMap);
			} else {
				if (Utils.nullCheck(itemMap.getCustomName()) != "NONE") {
					itemMap.setCustomName(null);
					this.creatingPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = Language.newString();
					placeHolders[14] = "NAME";
					placeHolders[15] = "&bUltimate Sword";
					Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
					Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
				}
			}
		}, event -> {
			itemMap.setCustomName(event.getMessage());
			String[] placeHolders = Language.newString();
			placeHolders[14] = "NAME";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.creatingPane(event.getPlayer(), itemMap);
		}));
		creatingPane.addButton(new Button(ItemHandler.getItem("386", 1, false, "&b&lLore", "&7", "&7*Set the lore of the item.", "&9&lLORE: &f" + Utils.nullCheck(itemMap.getCustomLore().toString())), event -> {
			if (itemMap.getDynamicLores() != null && !itemMap.getDynamicLores().isEmpty()) {
				this.animatedLorePane(player, itemMap);
			} else { 
				this.lorePane(player, itemMap);
			}
		}, event -> {
			itemMap.setCustomLore(Utils.split(event.getMessage()));
			String[] placeHolders = Language.newString();
			placeHolders[14] = "LORE";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.creatingPane(event.getPlayer(), itemMap);
		}));
		creatingPane.addButton(new Button(ItemHandler.getItem("BOW", 1, false, "&b&lDamage", "&7", "&7*Set the damage of the item.", (itemMap.getMaterial().getMaxDurability() != 0 ? "&9&lDURABILITY: &a" + 
		Utils.nullCheck(itemMap.getDurability() + "&7") : "&c&lERROR: &7This item is NOT damagable.")), event -> {
			if (Utils.nullCheck(itemMap.getDurability() + "&7") != "NONE") {
				itemMap.setDurability(null);
				this.creatingPane(player, itemMap);
			} else if (itemMap.getMaterial().getMaxDurability() != 0) {
				this.damagePane(player, itemMap);
			}
		}));
		creatingPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, "&e&lCommand Settings", "&7", "&7*Define commands for the item", "&7which execute upon being", "&7interacted with."), event -> this.commandPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem("ENCHANTED_BOOK", 1, false, "&b&lEnchantments", "&7", "&7*Add enchants to make the", "&7item sparkle and powerful.", "&9&lENCHANTMENTS: &a" + 
		(Utils.nullCheck(itemMap.getEnchantments().toString()) != "NONE" ? "&a" + enchantList : "NONE")), event -> this.enchantPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&b&lItemflags", "&7", "&7*Special flags that will give", "&7the item abilities and", "&7custom features.", "&9&lITEMFLAGS: &a" + 
		(Utils.nullCheck(itemMap.getItemFlags()) != "NONE" ? "&a" + itemflagsList : "NONE")), event -> this.flagPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&b&lTriggers", "&7", "&7*When the players act upon these", "&7events, the item will be given.", "&9&lTRIGGERS: &a" +
		(Utils.nullCheck(itemMap.getTriggers()) != "NONE" ? "&a" + triggersList : "NONE")), event -> this.triggerPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasSpecificUpdate("1_15") ? "REDSTONE_TORCH" : "76"), 1, false, "&b&lPermission Node", "&7", "&7*Custom permission node that", "&7will be required by a permission", "&7plugin to receive the item.", "&7&lNote: &7Do NOT include", 
				"&7any spaces or special characters", "&9&lPERMISSION-NODE: &a" + Utils.nullCheck(itemMap.getPermissionNode())), event -> {
			if (Utils.nullCheck(itemMap.getPermissionNode()) != "NONE") {
				itemMap.setPerm(null);
				this.creatingPane(player, itemMap);
			} else {
				player.closeInventory();
				String[] placeHolders = Language.newString();
				placeHolders[14] = "CUSTOM PERMISSION";
				placeHolders[15] = "itemjoin.ultra";
				Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
				Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
			}
		}, event -> {
			itemMap.setPerm(event.getMessage());
			String[] placeHolders = Language.newString();
			placeHolders[14] = "CUSTOM PERMISSION";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.creatingPane(event.getPlayer(), itemMap);
		}));
		creatingPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "GRASS_BLOCK" : "2"), 1, false, "&b&lEnabled Worlds", "&7", "&7*Define the world(s) that the", "&7item will be given in.", 
				"&9&lENABLED-WORLDS: &a" + (Utils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE" ? "&a" + worldList : "NONE")), event -> this.worldPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, true, "&b&lEnabled Regions", "&7", "&7*Define the region(s) that the", "&7item will be given in.", (ConfigHandler.getDepends().getGuard().guardEnabled() ? 
				"&9&lENABLED-REGIONS: &a" + (Utils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE" ? "&a" + regionList : "NONE") : ""), (ConfigHandler.getDepends().getGuard().guardEnabled() ? "" : "&7"), 
				(ConfigHandler.getDepends().getGuard().guardEnabled() ? "" : "&c&lERROR: &7WorldGuard was NOT found."), (ConfigHandler.getDepends().getGuard().guardEnabled() ? "" : "&7This button will do nothing...")), event -> {
			if (ConfigHandler.getDepends().getGuard().guardEnabled()) {
				this.regionPane(player, itemMap);
			}
		}));
		creatingPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "STICKY_PISTON" : "29"), 1, false, "&e&lAnimation Settings", "&7", "&7*Define animations for the item", "&7Example: Custom iterations for the", 
				"&7items name, lore, and material type"), event -> this.animationPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "OAK_FENCE" : "FENCE"), 1, false, "&b&lLimit-Modes", "&7", "&7*Define the gamemode(s) that the", "&7item will be limited to.", "&9&lLIMIT-MODES: &a" + 
				Utils.nullCheck(itemMap.getLimitModes())), event -> this.limitPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem("NETHER_STAR", 1, false, "&b&lProbability", "&7", "&7*Define the chance that the", "&7item will be given to the player.", "&7", "&9&lPROBABILITY: &a" +
				Utils.nullCheck(itemMap.getProbability() + "&a%")), event -> {
			if (Utils.nullCheck(itemMap.getProbability() + "&a%") != "NONE") {
				itemMap.setProbability(-1);
				this.creatingPane(player, itemMap);
			} else {
				this.probabilityPane(player, itemMap);
			}
		}));
		creatingPane.addButton(new Button(ItemHandler.getItem("ICE", 1, false, "&b&lUsage Cooldown", "&7", "&7*Define the cooldown for", "&7interacting with the item.", "&9&lUSE-COOLDOWN: &a" +
		Utils.nullCheck(itemMap.getInteractCooldown() + "&7")), event -> {
			if (Utils.nullCheck(itemMap.getInteractCooldown() + "&7") != "NONE") {
				itemMap.setInteractCooldown(0);
				this.creatingPane(player, itemMap);
			} else {
				this.usePane(player, itemMap);
			}
		}));
		if (itemMap.getMaterial().toString().contains("MAP")) {
			creatingPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&e&lMap Image", "&7", "&7*Adds a custom map image that", "&7will be displayed when held.", "&7", "&7Place the custom map image", 
					"&7in the MAIN ItemJoin folder.", "&7", "&7The map CAN be a GIF but", "&7must be a 128x128 pixel image.", "&9&lImage: &a" + Utils.nullCheck(itemMap.getMapImage())), event -> {
				if (Utils.nullCheck(itemMap.getMapImage()) != "NONE") {
					itemMap.setMapImage(null);
					this.creatingPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = Language.newString();
					placeHolders[14] = "MAP IMAGE";
					placeHolders[15] = "minecraft.png OR minecraft-dance.gif";
					Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
					Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
				}
			}, event -> {
				itemMap.setMapImage(event.getMessage());
				String[] placeHolders = Language.newString();
				placeHolders[14] = "MAP IMAGE";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.creatingPane(event.getPlayer(), itemMap);
			}));
		} else if (itemMap.getMaterial().toString().contains("TIPPED_ARROW")) {
			creatingPane.addButton(new Button(ItemHandler.getItem("BLAZE_POWDER", 1, false, "&e&lEffects", "&7", "&7*Add custom effects", "&7to the arrow tip.", "&9&lTipped-Effect: &a" + Utils.nullCheck(potionList)),
					event -> this.potionPane(player, itemMap)));
		} else if (itemMap.getMaterial().toString().contains("CHARGE") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_STAR")) {
			Interface colorPane = new Interface(true, 6, this.GUIName);
			colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> this.creatingPane(player, itemMap)));
			for (DyeColor color: DyeColor.values()) {
				colorPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "GRAY_DYE" : "351:8"), 1, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your firework charge."), event -> {
					itemMap.setChargeColor(color);
					this.creatingPane(player, itemMap);
				}));
			}
			creatingPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "PINK_DYE" : "351:9"), 1, false, "&e&lCharge Color", "&7", "&7*Set the color of", "&7the firework star.", "&9&lColor: &a" +
			Utils.nullCheck(itemMap.getChargeColor() + "")), event -> {
				if (Utils.nullCheck(itemMap.getChargeColor() + "") != "NONE") {
					itemMap.setChargeColor(null);
					this.creatingPane(player, itemMap);
				} else {
					colorPane.open(player);
				}
			}));
		} else if (itemMap.getMaterial().toString().contains("GOLDEN_APPLE")) {
			creatingPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, "&e&lEffects", "&7", "&7*Add custom effects after", "&7consuming the apple item.", "&9&lNotch-Effects: &a" + Utils.nullCheck(potionList)),
					event -> this.potionPane(player, itemMap)));
		} else if (itemMap.getMaterial().toString().equalsIgnoreCase("POTION")) {
			creatingPane.addButton(new Button(ItemHandler.getItem("POTION", 1, false, "&e&lEffects", "&7", "&7*Add custom effects after", "&7consuming the potion item.", "&9&lPotion-Effects: &a" + Utils.nullCheck(potionList)),
					event -> this.potionPane(player, itemMap)));
		} else if (itemMap.getMaterial().toString().contains("BANNER")) {
			creatingPane.addButton(new Button(ItemHandler.getItem("CLAY_BALL", 1, false, "&e&lBanner Patterns", "&7", "&7*Set custom patterns that", "&7will appear on the item.", "&9&lBanner-Meta: &a" + Utils.nullCheck(patternList)),
					event -> this.bannerPane(player, itemMap)));
		} else {
			creatingPane.addButton(new Button(ItemHandler.getItem("MAGMA_CREAM", 1, false, "&e&lOther Settings", "&7", "&7*Settings that are specific", "&7to the item's material type.", (this.specialItem(itemMap) ? "" : "&7"),
					(this.specialItem(itemMap) ? "" : "&c&lERROR: &7A " + itemMap.getMaterial().name() + " &7is NOT"), (this.specialItem(itemMap) ? "" : "&7a special material type.")), event -> {
				if (this.specialItem(itemMap)) {
					this.otherPane(player, itemMap);
				}
			}));
		}
		creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return to the main menu", "&7", "&c&lWARNING: &7This item has NOT been saved!"), event -> this.startMenu(player)));
		creatingPane.addButton(new Button(this.fillerPaneBItem), 3);
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			creatingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&a&l&nSave to Config", "&7", "&7*Saves this custom item", "&7settings to the items.yml file."), 
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0"), event -> {
				itemMap.saveToConfig();
				String[] placeHolders = Language.newString();
				placeHolders[3] = itemMap.getConfigName();
				Language.sendLangMessage("Commands.UI.itemSaved", player, placeHolders);
				ConfigHandler.getSQLData().executeLaterStatements();
				ItemUtilities.closeAnimations();
				ItemUtilities.clearItems();
				ConfigHandler.generateData(null);
				player.closeInventory();
			}));
		} else {
			creatingPane.addButton(new Button(ItemHandler.getItem("WOOL:5", 1, false, "&a&l&nSave to Config", "&7", "&7*Saves this custom item", "&7to the items.yml file"), event -> {
				itemMap.saveToConfig();
				String[] placeHolders = Language.newString();
				placeHolders[3] = itemMap.getConfigName();
				Language.sendLangMessage("Commands.UI.itemSaved", player, placeHolders);
				ConfigHandler.getSQLData().executeLaterStatements();
				ItemUtilities.closeAnimations();
				ItemUtilities.clearItems();
				ConfigHandler.generateData(null);
				player.closeInventory();
			}));
		}
		creatingPane.addButton(new Button(this.fillerPaneBItem), 3);
		creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nMain Menu", "&7", "&7*Cancel and return you to the main menu", "&7", "&c&lWARNING: &7This item has NOT been saved!"), event -> this.startMenu(player)));
		creatingPane.open(player);
	}
	
// =======================================================================================================================================================================================================================================

//  ============================================== //
//             Item Definition Menus      	       //
//  ============================================== //
	
	private boolean safeMaterial(ItemStack item, Inventory inventoryCheck) {
		inventoryCheck.setItem(0, item);
		if (inventoryCheck.getItem(0) != null && inventoryCheck.getItem(0).getType().name() != "AIR") {
			return true;
		}
		return false;
	}
	
	private void materialPane(final Player player, final ItemMap itemMap, final int stage) {
		Interface materialPane = new Interface(true, 6, this.GUIName);
		if (stage != 0) {
			materialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
				this.creatingPane(player, itemMap);
			}));
		}
		materialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "BUKKIT MATERIAL";
			placeHolders[15] = "IRON_SWORD";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (ItemHandler.getMaterial(event.getMessage(), null) != null) {
				itemMap.setMaterial(ItemHandler.getMaterial(event.getMessage(), null));
				if (!ServerHandler.hasAquaticUpdate() && event.getMessage().contains(":")) {
					String[] dataValue = event.getMessage().split(":");
					if (Utils.isInt(dataValue[1])) {
						itemMap.setDataValue((short)Integer.parseInt(dataValue[1]));
					}	
				}
				String[] placeHolders = Language.newString();
				placeHolders[14] = "BUKKIT MATERIAL";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.creatingPane(event.getPlayer(), itemMap);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.noMatch", player, placeHolders);
				this.materialPane(player, itemMap, stage);
			}
		}));
		Inventory inventoryCheck = ItemJoin.getInstance().getServer().createInventory(null, 9, this.GUIName);
		for (Material material: Material.values()) {
			if (!material.name().contains("LEGACY") && material.name() != "AIR" && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, "", ""), inventoryCheck)) {
				if (!ServerHandler.hasAquaticUpdate() && Legacy.getDataValue(material) != 0) {
					for (int i = 0; i <= Legacy.getDataValue(material); i++) {
						if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
							final int dataValue = i;
							materialPane.addButton(new Button(ItemHandler.getItem(material.toString() + ":" + dataValue, 1, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
								itemMap.setMaterial(material);
								if (dataValue != 0) { itemMap.setDataValue((short)dataValue); }
								if (stage == 0) {
									this.switchPane(player, itemMap, 0);
								} else {
									this.creatingPane(player, itemMap);
								}
							}));
						}
					}
				} else {
				materialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
					itemMap.setMaterial(material);
					if (stage == 0) {
						this.switchPane(player, itemMap, 0);
					} else {
						this.creatingPane(player, itemMap);
					}
				}));
				}
			}
		}
		inventoryCheck.clear();
		materialPane.open(player);
	}
	
	private void switchPane(final Player player, final ItemMap itemMap, final int stage) {
		Interface slotPane = new Interface(false, 1, this.GUIName);
		if (stage == 0) {
			if (ServerHandler.hasSpecificUpdate("1_8")) {
				slotPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), 
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> this.materialPane(player, itemMap, 0)));
			} else {
				slotPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> this.materialPane(player, itemMap, 0)));
			}
		} else { 
			slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		}
		slotPane.addButton(new Button(this.fillerPaneBItem), 2);
		slotPane.addButton(new Button(ItemHandler.getItem("GLASS", 1, false, "&a&lSingle Slot", "&7", "&7*Define a single dedicated", "&7 slot for the item."), event -> this.slotPane(player, itemMap, stage, 0)));
		slotPane.addButton(new Button(this.fillerPaneBItem), 1);
		slotPane.addButton(new Button(ItemHandler.getItem("23", 1, false, "&b&lMultiple Slots", "&7", "&7*Define multiple slots for the item."), event -> this.slotPane(player, itemMap, stage, 1)));
		slotPane.addButton(new Button(this.fillerPaneBItem), 2);
		if (stage == 0) {
			if (ServerHandler.hasSpecificUpdate("1_8")) {
				slotPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), 
						"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> this.materialPane(player, itemMap, 0)));
			} else {
				slotPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> this.materialPane(player, itemMap, 0)));
			}
		} else { 
			slotPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		}
		slotPane.open(player);
	}
	
	private void slotPane(final Player player, final ItemMap itemMap, final int stage, final int type) { 
		Interface slotPane = new Interface(false, 6, this.GUIName);
		Interface craftingPane = new Interface(false, 4, this.GUIName);
		craftingPane.addButton(new Button(this.fillerPaneGItem), 3);
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]") : false), "&9&lSlot: &7&lCRAFTING&a&l[1]", "&7", "&7*Click to set the custom item", 
				"&7to appear in the &lCRAFTING &7slot &a&l[1]&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("CRAFTING[1]"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[1]")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("CRAFTING[1]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("CRAFTING[1]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				}
			}
		}));
		craftingPane.addButton(new Button(this.fillerPaneGItem));
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 2, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]") : false), "&9&lSlot: &7&lCRAFTING&a&l[2]", "&7", "&7*Click to set the custom item", 
				"&7to appear in the &lCRAFTING &7slot &a&l[2]&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("CRAFTING[2]"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[2]")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("CRAFTING[2]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("CRAFTING[2]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				}
			}
		}));
		craftingPane.addButton(new Button(this.fillerPaneGItem), 10);
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]") : false), "&9&lSlot: &7&lCRAFTING&a&l[0]", "&7", "&7*Click to set the custom item", 
				"&7to appear in the &lCRAFTING &7slot &a&l[0]&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("CRAFTING[0]"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[0]")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("CRAFTING[0]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("CRAFTING[0]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				}
			}
		}));
		craftingPane.addButton(new Button(this.fillerPaneGItem), 4);
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 3, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]") : false), "&9&lSlot: &7&lCRAFTING&a&l[3]", "&7", "&7*Click to set the custom item", 
				"&7to appear in the &lCRAFTING &7slot &a&l[3]&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("CRAFTING[3]"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[3]")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("CRAFTING[3]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("CRAFTING[3]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				}
			}
		}));
		craftingPane.addButton(new Button(this.fillerPaneGItem));
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 4, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]") : false), "&9&lSlot: &7&lCRAFTING&a&l[4]", "&7", "&7*Click to set the custom item", 
				"&7to appear in the &lCRAFTING &7slot &a&l[4]&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("CRAFTING[4]"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "CRAFTING[4]")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("CRAFTING[4]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("CRAFTING[4]"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 2);
				}
			}
		}));
		craftingPane.addButton(new Button(this.fillerPaneGItem), 3);
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			craftingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), 
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> slotPane.open(player)));
		} else {
			craftingPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), event -> slotPane.open(player)));
		}
		if (type > 0) {
			craftingPane.addButton(new Button(this.fillerPaneBItem), 3);
			craftingPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&a&lFinish Selecting", "&7", "&7*Saves the chosen slots", "&7to the item definition."), event -> this.creatingPane(player, itemMap)));
			craftingPane.addButton(new Button(this.fillerPaneBItem), 3);
		} else {
			craftingPane.addButton(new Button(this.fillerPaneBItem), 7);
		}
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			craftingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), 
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> slotPane.open(player)));
		} else {
			craftingPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu"), event -> slotPane.open(player)));
		}
		slotPane.addButton(new Button(this.fillerPaneGItem));
		slotPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY") : false), "&9&lSlot: &a&lARBITRARY", "&7", "&7*Click to set the custom item", 
				"&7to appear in slot &a&lArbitrary&7", "&7", "&7*Arbitrary is defined as giving the", "&7item in the first available slot.", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY") ? "&9&lENABLED: &aTRUE" : "")), 
				event -> {
			if (type == 0) { 
				itemMap.setSlot("ARBITRARY"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "ARBITRARY")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("ARBITRARY"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("ARBITRARY"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				}
			}
		}));
		slotPane.addButton(new Button(ItemHandler.getItem("58", 1, false, "&9&lSlot: &a&lCRAFTING", "&7", "&7*Click to see a list of crafting slots"), event -> craftingPane.open(player)));
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "HELMET") : false), "&9&lSlot: &a&lHELMET", "&7", "&7*Click to set the custom item", 
				"&7to appear in slot &a&lHELMET&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "HELMET") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("HELMET"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "HELMET")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("HELMET"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("HELMET"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				}
			}
		}));
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_CHESTPLATE", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE") : false), "&9&lSlot: &a&lCHESTPLATE", "&7", "&7*Click to set the custom item", 
				"&7to appear in slot &a&lCHESTPLATE&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("CHESTPLATE"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "CHESTPLATE")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("CHESTPLATE"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("CHESTPLATE"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				}
			}
		}));
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_LEGGINGS", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS") : false), "&9&lSlot: &a&lLEGGINGS", "&7", "&7*Click to set the custom item", 
				"&7to appear in slot &a&lLEGGINGS&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("LEGGINGS"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "LEGGINGS")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("LEGGINGS"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("LEGGINGS"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				}
			}
		}));
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_BOOTS", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "BOOTS") : false), "&9&lSlot: &a&lBOOTS", "&7", "&7*Click to set the custom item", 
				"&7to appear in slot &a&lBOOTS&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "BOOTS") ? "&9&lENABLED: &aTRUE" : "")), event -> {
			if (type == 0) { 
				itemMap.setSlot("BOOTS"); 
				itemMap.setMultipleSlots(new ArrayList<String>()); 
				this.creatingPane(player, itemMap);
			} else { 
				if (Utils.containsValue(itemMap.getMultipleSlots(), "BOOTS")) {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.remove("BOOTS"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				} else {
					List<String> slots = itemMap.getMultipleSlots(); 
					slots.add("BOOTS"); 
					itemMap.setMultipleSlots(slots);
					this.slotPane(player, itemMap, stage, 1);
				}
			}
		}));
		if (ServerHandler.hasCombatUpdate()) {
			slotPane.addButton(new Button(ItemHandler.getItem("SHIELD", 1, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), "OFFHAND") : false), "&9&lSlot: &a&lOFFHAND", "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&lOFFHAND&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), "OFFHAND") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot("OFFHAND"); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					this.creatingPane(player, itemMap);
				} else { 
					if (Utils.containsValue(itemMap.getMultipleSlots(), "OFFHAND")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove("OFFHAND"); 
						itemMap.setMultipleSlots(slots);
						this.slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add("OFFHAND"); 
						itemMap.setMultipleSlots(slots);
						this.slotPane(player, itemMap, stage, 1);
					}
				}
			}));
		} else {
			slotPane.addButton(new Button(this.fillerPaneGItem));
		}
		slotPane.addButton(new Button(this.fillerPaneGItem));
		for (int i = 9; i < 36; i++) {
			final int slot = i;
			slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:3", i, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), slot + "") : false), "&9&lSlot: &a&l" + i, "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&l" + i + "&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), slot + "") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot(slot + ""); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					this.creatingPane(player, itemMap);
				} else { 
					if (Utils.containsValue(itemMap.getMultipleSlots(), slot + "")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove(slot + ""); 
						itemMap.setMultipleSlots(slots);
						this.slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add(slot + ""); 
						itemMap.setMultipleSlots(slots);
						this.slotPane(player, itemMap, stage, 1);
					}
				}
			}));
		}
		for (int j = 0; j < 9; j++) {
			final int slot = j;
			int count = j;
			if (slot == 0) {
				count = 1;
			}
			slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", count, (type > 0 ? Utils.containsValue(itemMap.getMultipleSlots(), slot + "") : false), "&9&lSlot: &a&l" + j, "&7", "&7*Click to set the custom item", 
					"&7to appear in slot &a&l" + j + "&7", (type > 0 && Utils.containsValue(itemMap.getMultipleSlots(), slot + "") ? "&9&lENABLED: &aTRUE" : "")), event -> {
				if (type == 0) { 
					itemMap.setSlot(slot + ""); 
					itemMap.setMultipleSlots(new ArrayList<String>()); 
					this.creatingPane(player, itemMap);
				} else { 
					if (Utils.containsValue(itemMap.getMultipleSlots(), slot + "")) {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.remove(slot + ""); 
						itemMap.setMultipleSlots(slots);
						this.slotPane(player, itemMap, stage, 1);
					} else {
						List<String> slots = itemMap.getMultipleSlots(); 
						slots.add(slot + ""); 
						itemMap.setMultipleSlots(slots);
						this.slotPane(player, itemMap, stage, 1);
					}
				}
			}));
		}
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			slotPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."),
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> this.switchPane(player, itemMap, stage)));
		} else {
			slotPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."), event -> this.switchPane(player, itemMap, stage)));
		}
		if (type > 0) {
			slotPane.addButton(new Button(this.fillerPaneBItem), 3);
			slotPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&a&lFinish Selecting", "&7", "&7*Saves the chosen slots", "&7to the item definition."), event -> this.creatingPane(player, itemMap)));
			slotPane.addButton(new Button(this.fillerPaneBItem), 3);
		} else {
			slotPane.addButton(new Button(this.fillerPaneBItem), 7);
		}
		if (ServerHandler.hasSpecificUpdate("1_8")) {
			slotPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."), 
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> this.switchPane(player, itemMap, stage)));
		} else {
			slotPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, false, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the slot(s) selection menu."), event -> this.switchPane(player, itemMap, stage)));
		}
		if (type == 2) {
			craftingPane.open(player);
		} else {
			slotPane.open(player);
		}
	}
	
	private void countPane(final Player player, final ItemMap itemMap) {
		Interface countPane = new Interface(true, 6, this.GUIName);
		countPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
			this.creatingPane(player, itemMap);
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			countPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lCount: &a&l" + k, "&7", "&7*Click to set the", "&7count of the item."), event -> {
				itemMap.setCount(k + "");this.creatingPane(player, itemMap);
			}));
		}
		countPane.open(player);
	}
	
	private void damagePane(final Player player, final ItemMap itemMap) {
		Interface damagePane = new Interface(true, 6, this.GUIName);
		damagePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
			this.creatingPane(player, itemMap);
		}));
		damagePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Damage", "&7", "&7*Click to set a custom damage", "&7value for the item."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "DAMAGE";
			placeHolders[15] = "1893";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				itemMap.setDurability((short) Integer.parseInt(event.getMessage()));
				String[] placeHolders = Language.newString();
				placeHolders[14] = "DAMAGE";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.creatingPane(event.getPlayer(), itemMap);
		}));
		for (int i = 1; i <= itemMap.getMaterial().getMaxDurability(); i++) {
			final int k = i;
			damagePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:6", 1, false, "&9&lDamage: &a&l" + k, "&7", "&7*Click to set the", "&7damage of the item."), event -> {
				itemMap.setDurability((short) k);this.creatingPane(player, itemMap);
			}));
		}
		damagePane.open(player);
	}
	
//  ============================================== //
//               ItemCommand Menus        	       //
//  ============================================== //
	
	private void commandPane(final Player player, final ItemMap itemMap) {
		Interface commandPane = new Interface(false, 2, this.GUIName);
		commandPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WRITABLE_BOOK" : "386"), 1, false, "&e&lCommands", "&7", "&7*Click to define the custom command lines", "&7for the item and click type.", 
				"&7", "&9&lCommands: &a" + (itemMap.getCommands().length != 0 ? "YES" : "NONE")), event -> this.actionPane(player, itemMap)));
		commandPane.addButton(new Button(ItemHandler.getItem("STICK", 1, false, "&a&lType", "&7", "&7*The event type that will", "&7trigger command execution.", "&9&lCOMMANDS-TYPE: &a" + Utils.nullCheck(itemMap.getCommandType() + "")), 
				event -> this.typePane(player, itemMap)));
		commandPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&a&lParticle", "&7", "&7*Custom particle(s) that will be", "&7displayed when the commands", "&7are successfully executed.", "&9&lCOMMANDS-PARTICLE: &a" +
				Utils.nullCheck(itemMap.getCommandParticle() + "")), event -> {
			if (Utils.nullCheck(itemMap.getCommandParticle() + "") != "NONE") {
				itemMap.setCommandParticle(null);
				this.commandPane(player, itemMap);
			} else {
				this.particlePane(player, itemMap);
			}
		}));
		commandPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, false, "&a&lCost", "&7", "&7*Amount that the player will", "&7be charged upon successfully", "&7executing the commands.", "&9&lCOMMANDS-COST: &a" + 
		(Utils.nullCheck(itemMap.getCommandCost() + "&7") != "NONE" ? "$" : "") + Utils.nullCheck(itemMap.getCommandCost() + "&7")), event -> {
			if (Utils.nullCheck(itemMap.getCommandCost() + "&7") != "NONE") {
				itemMap.setCommandCost(0);
				this.commandPane(player, itemMap);
			} else {
				this.costPane(player, itemMap);
			}
		}));
		commandPane.addButton(new Button(ItemHandler.getItem("ICE", 1, false, "&a&lCooldown", "&7", "&7*The time that the commands will", "&7be on cooldown for.", "&7", "&9&lCOMMANDS-COOLDOWN: &a" + Utils.nullCheck(itemMap.getCommandCooldown() + "&7") + 
				(Utils.nullCheck(itemMap.getCommandCooldown() + "&7") != "NONE" ? "&a second(s)" : "")), event -> {
			if (Utils.nullCheck(itemMap.getCommandCooldown() + "&7") != "NONE") {
				itemMap.setCommandCooldown(0);
				this.commandPane(player, itemMap);
			} else {
				this.cooldownPane(player, itemMap);
			}
		}));
		commandPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "OAK_SIGN" : "SIGN"), 1, false, "&a&lCooldown Message", "&7", "&7*Optional cooldown message", "&7to be displayed when", "&7the items commands are", 
				"&7on cooldown.", "&9&lCOOLDOWN-MESSAGE: &a" + Utils.nullCheck(itemMap.getCooldownMessage())), event -> {
			if (Utils.nullCheck(itemMap.getCooldownMessage()) != "NONE") {
				itemMap.setCooldownMessage(null);
				this.commandPane(player, itemMap);
			} else {
				player.closeInventory();
				String[] placeHolders = Language.newString();
				placeHolders[14] = "COOLDOWN MESSAGE";
				placeHolders[15] = "&cThis item is on cooldown for &a%timeleft% &cseconds..";
				Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
				Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
			}
		}, event -> {
			itemMap.setCooldownMessage(event.getMessage());String[] placeHolders = Language.newString();placeHolders[14] = "COOLDOWN MESSAGE";Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);this.commandPane(event.getPlayer(), itemMap);
		}));
		commandPane.addButton(new Button(ItemHandler.getItem("JUKEBOX", 1, false, "&a&lSound", "&7", "&7*The sound that will be", "&7played after a successful", "&7command execution.", "&9&lCOMMANDS-SOUND: &a" + 
		Utils.nullCheck(itemMap.getCommandSound() + "")), event -> {
			if (Utils.nullCheck(itemMap.getCommandSound() + "") != "NONE") {
				itemMap.setCommandSound(null);
				this.commandPane(player, itemMap);
			} else {
				this.soundPane(player, itemMap);
			}
		}));
		commandPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "REPEATER" : "356"), 1, false, "&a&lSequence", "&7", "&7*The order that the command lines", "&7will be executed in.", "&9&lCOMMANDS-SEQUENCE: &a" + 
		Utils.nullCheck(itemMap.getCommandSequence() + "")), event -> {
			if (Utils.nullCheck(itemMap.getCommandSequence() + "") != "NONE") {
				itemMap.setCommandSequence(null);
				this.commandPane(player, itemMap);
			} else {
				this.sequencePane(player, itemMap);
			}
		}));
		commandPane.addButton(new Button(ItemHandler.getItem("327", 1, false, "&a&lWarmup", "&7", "&7*The time it will take before", "&7the commands are executed.", "&7Player movement will cancel the", "&7pending commands execution.", "&7", 
				"&9&lCOMMANDS-WARMUP: &a" + Utils.nullCheck(itemMap.getWarmDelay() + "&7") + (Utils.nullCheck(itemMap.getWarmDelay() + "&7") != "NONE" ? "&a second(s)" : "")), event -> {
			if (Utils.nullCheck(itemMap.getWarmDelay() + "&7") != "NONE") {
				itemMap.setWarmDelay(0);
				this.commandPane(player, itemMap);
			} else {
				this.warmPane(player, itemMap);
			}
		}));
		commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		commandPane.addButton(new Button(this.fillerPaneBItem), 7);
		commandPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		commandPane.open(player);
	}
	
	private void actionPane(final Player player, final ItemMap itemMap) {
		Interface clickPane = new Interface(false, 3, this.GUIName);
		clickPane.addButton(new Button(this.fillerPaneGItem), 3);
		clickPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, false, "&e&lMulti-Click", "&7", "&7*Commands that will execute only", "&7when left and right clicking.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.MULTI_CLICK_ALL)), event -> {
			this.commandListPane(player, itemMap, ActionType.MULTI_CLICK_ALL);
		}));
		clickPane.addButton(new Button(this.fillerPaneGItem));
		clickPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&e&lInventory", "&7", "&7*Commands that will execute only", "&7when cursor clicking the item", "&7with the players inventory open.", "&7", 
				"&7&lNote: &7The INVENTORY type", "&7for commands-type will need", "&7to be defined later.", "&7", "&9&lCommands: &a" + this.listCommands(itemMap, ActionType.INVENTORY)), event -> {
			this.commandListPane(player, itemMap, ActionType.INVENTORY);
		}));
		clickPane.addButton(new Button(this.fillerPaneGItem), 3);
		clickPane.addButton(new Button(ItemHandler.getItem("37", 1, false, "&e&lMulti-Click-Air", "&7", "&7*Commands that will execute only", "&7when left and right", "&7clicking the air.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.MULTI_CLICK_AIR)), event -> {
			this.commandListPane(player, itemMap, ActionType.MULTI_CLICK_AIR);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "OAK_DOOR" : (ServerHandler.hasSpecificUpdate("1_8") ? "324" : "64")), 1, false, "&e&lMulti-Click-Block", "&7", "&7*Commands that will execute only", "&7when left and right", "&7clicking a block.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.MULTI_CLICK_BLOCK)), event -> {
			this.commandListPane(player, itemMap, ActionType.MULTI_CLICK_BLOCK);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "GOLDEN_SWORD" : "GOLD_SWORD"), 1, false, "&e&lLeft-Click", "&7", "&7*Commands that will execute only", "&7when left clicking.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.LEFT_CLICK_ALL)), event -> {
			this.commandListPane(player, itemMap, ActionType.LEFT_CLICK_ALL);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem("37", 1, false, "&e&lLeft-Click-Air", "&7", "&7*Commands that will execute only", "&7when left clicking the air.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.LEFT_CLICK_AIR)), event -> {
			this.commandListPane(player, itemMap, ActionType.LEFT_CLICK_AIR);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "OAK_DOOR" : (ServerHandler.hasSpecificUpdate("1_8") ? "324" : "64")), 1, false, "&e&lLeft-Click-Block", "&7", "&7*Commands that will execute only", "&7when left clicking a block.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.LEFT_CLICK_BLOCK)), event -> {
			this.commandListPane(player, itemMap, ActionType.LEFT_CLICK_BLOCK);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "GOLDEN_SWORD" : "GOLD_SWORD"), 1, false, "&e&lRight-Click", "&7", "&7*Commands that will execute only", "&7when right clicking.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.RIGHT_CLICK_ALL)), event -> {
			this.commandListPane(player, itemMap, ActionType.RIGHT_CLICK_ALL);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem("37", 1, false, "&e&lRight-Click-Air", "&7", "&7*Commands that will execute only", "&7when right clicking the air.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.RIGHT_CLICK_AIR)), event -> {
			this.commandListPane(player, itemMap, ActionType.RIGHT_CLICK_AIR);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "OAK_DOOR" : (ServerHandler.hasSpecificUpdate("1_8") ? "324" : "64")), 1, false, "&e&lRight-Click-Block", "&7", "&7*Commands that will execute only", "&7when right clicking a block.", "&7", "&9&lCommands: &a" + 
		this.listCommands(itemMap, ActionType.RIGHT_CLICK_BLOCK)), event -> {
			this.commandListPane(player, itemMap, ActionType.RIGHT_CLICK_BLOCK);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "PISTON" : "PISTON_BASE"), 1, false, "&e&lPhysical", "&7", "&7*Commands that will execute", "&7when held in the player hand", "&7and they interact with a object", "&7such as a pressure plate.", "&7", 
				"&9&lCommands: &a" + this.listCommands(itemMap, ActionType.PHYSICAL)), event -> {
			this.commandListPane(player, itemMap, ActionType.PHYSICAL);
		}));
		clickPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> this.commandPane(player, itemMap)));
		clickPane.addButton(new Button(this.fillerPaneBItem), 7);
		clickPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> this.commandPane(player, itemMap)));
		clickPane.open(player);
	}
	
	private String listCommands(final ItemMap itemMap, final ActionType action) {
		String commands = "";
		String commandReturn = "NONE";
		for (ItemCommand command: itemMap.getCommands()) {
			if (command.matchAction(action)) {
				commands += command.getCommand() + " /n ";
			}
		}
		if (Utils.nullCheck(commands) != "NONE") {
			commandReturn = commands;
		}
		return commandReturn;
	}
	
	private void modifyCommands(final ItemMap itemMap, final ItemCommand itemCommand, final boolean newCommand) {
		List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
		for (ItemCommand command: itemMap.getCommands()) { arrayCommands.add(command); }
		if (newCommand) { arrayCommands.add(itemCommand); } 
		else { arrayCommands.remove(itemCommand); }
		final ItemCommand[] commands = new ItemCommand[arrayCommands.size()];
		for (int i = 0; i < arrayCommands.size(); ++i) { commands[i] = arrayCommands.get(i); }
		itemMap.setCommands(commands);
	}
	
	private int getCommandSize(final ItemMap itemMap, final ActionType action) {
		int l = 0;
		for (ItemCommand command: itemMap.getCommands()) {
			if (command.matchAction(action)) {
				l++;
			}
		}
		return l;
	}
	
	private void commandListPane(final Player player, final ItemMap itemMap, final ActionType action) {
		Interface commandListPane = new Interface(true, 2, this.GUIName);
		commandListPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the click type menu."), event -> {
			this.actionPane(player, itemMap);
		}));
		commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&lNew Line", "&7", "&7*Add a new command to be executed", "&7by &9&l" + action.name()), event -> {
			this.executorPane(player, itemMap, action);
		}));
		ItemCommand[] commandList = itemMap.getCommands();
		int l = 1;
		for (ItemCommand command: commandList) {
			if (command.matchAction(action)) {
				final int k = l;
				commandListPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&f" + command.getCommand(), "&7", "&7*Click to &lmodify &7this command.", "&9&lOrder Number: &a" + k), event -> {
					this.modifyCommandsPane(player, itemMap, action, command, k);
				}));
				l++;
			}
		}
		commandListPane.open(player);
	}
	
	private void orderPane(final Player player, final ItemMap itemMap, final ActionType action, final ItemCommand command, final int orderNumber) {
		Interface orderPane = new Interface(true, 2, this.GUIName);
		orderPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command modify menu."), event -> {
			this.modifyCommandsPane(player, itemMap, action, command, orderNumber);
		}));
		for (int i = 1; i <= this.getCommandSize(itemMap, action); i++) {
			final int k = i;
			orderPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:3", k, false, "&9&lOrder Number: &a&l" + k, "&7", "&7*Click to set the order", "&7number of the command."), event -> {
				List < ItemCommand > arrayCommands = new ArrayList < ItemCommand > ();
				int l = 0;
				for (ItemCommand Command: itemMap.getCommands()) {
					if (Command.matchAction(action)) {
						if ((l + 1) == k) {
							arrayCommands.add(command);
						}
						l++;
					}
					if (!Command.equals(command)) {
						arrayCommands.add(Command);
					}
				}
				final ItemCommand[] commands = new ItemCommand[arrayCommands.size()];
				for (int j = 0; j < arrayCommands.size(); ++j) {
					commands[j] = arrayCommands.get(j);
				}
				itemMap.setCommands(commands);
				this.commandListPane(player, itemMap, action);
			}));
		}
		orderPane.open(player);
	}
	
	private void modifyCommandsPane(final Player player, final ItemMap itemMap, final ActionType action, final ItemCommand command, final int orderNumber) {
		Interface modPane = new Interface(false, 3, this.GUIName);
		modPane.addButton(new Button(this.fillerPaneGItem), 4);
		modPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&f" + command.getCommand(), "&7", "&7*You are modifying this command.", "&9&lOrder Number: &a" + orderNumber)));
		modPane.addButton(new Button(this.fillerPaneGItem), 4);
		modPane.addButton(new Button(this.fillerPaneGItem), 3);
		modPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&fModify", "&7", "&7*Sets the command to", "&7another text entry."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "MODIFIED COMMAND";
			placeHolders[15] = "gamemode creative";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			ItemCommand[] commands = itemMap.getCommands();
			for (ItemCommand Command: commands) {
				if (Command.equals(command)) {
					Command.setCommand(event.getMessage());
				}
			}
			itemMap.setCommands(commands);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "MODIFIED COMMAND";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		modPane.addButton(new Button(ItemHandler.getItem("STICK", 1, false, "&fOrder", "&7", "&7*Changes the order of execution", "&7for this command line.", "&7", "&7This will simply set the order", "&7number and push the", 
				"&7other commands down by one."), event -> {
			this.orderPane(player, itemMap, action, command, orderNumber);
		}));
		modPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&fDelete", "&7", "&7*Click to &cdelete &7this command."), event -> {
			this.modifyCommands(itemMap, command, false);
			this.commandListPane(player, itemMap, action);
		}));
		modPane.addButton(new Button(this.fillerPaneGItem), 3);
		modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> this.commandListPane(player, itemMap, action)));
		modPane.addButton(new Button(this.fillerPaneBItem), 7);
		modPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> this.commandListPane(player, itemMap, action)));
		modPane.open(player);
	}
	
	private void executorPane(final Player player, final ItemMap itemMap, final ActionType action) {
		Interface executorPane = new Interface(false, 2, this.GUIName);
		executorPane.addButton(new Button(ItemHandler.getItem("289", 1, false, "&f")));
		executorPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, false, "&e&lPlayer", "&7", "&7*Executes the command", "&7as the player."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "PLAYER COMMAND";placeHolders[15] = "spawn";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			this.modifyCommands(itemMap, ItemCommand.fromString("player: " + event.getMessage(), action, CommandType.BOTH, 0L), true);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "PLAYER COMMAND";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		executorPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, true, "&e&lOp", "&7", "&7*Executes the command as if the", "&7player has /op (admin permissions)."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "OP COMMAND";
			placeHolders[15] = "broadcast I am &cADMIN!";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			this.modifyCommands(itemMap, ItemCommand.fromString("op: " + event.getMessage(), action, CommandType.BOTH, 0L), true);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "COMMAND LINE";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		executorPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&e&lConsole", "&7", "&7*Executes the command", "&7in the console window."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "CONSOLE COMMAND";
			placeHolders[15] = "gamemode creative %player%";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			this.modifyCommands(itemMap, ItemCommand.fromString("console: " + event.getMessage(), action, CommandType.BOTH, 0L), true);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "OP COMMAND";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		executorPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, false, "&e&lServer", "&7", "&7*Switches the player to", "&7the defined server name.", "&7", "&7&lNote: &7This is the name", 
				"&7defined in the BungeeCord config."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "SERVER SWITCH";
			placeHolders[15] = "survival";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			this.modifyCommands(itemMap, ItemCommand.fromString("server: " + event.getMessage(), action, CommandType.BOTH, 0L), true);
			String[] placeHolders = Language.newString();placeHolders[14] = "SERVER SWITCH";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		executorPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, false, "&e&lBungee", "&7", "&7*Executes a BungeeCord specific command."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "BUNGEE COMMAND";
			placeHolders[15] = "survival";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			this.modifyCommands(itemMap, ItemCommand.fromString("bungee: " + event.getMessage(), action, CommandType.BOTH, 0L), true);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "BUNGEE COMMAND";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		executorPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&e&lMessage", "&7", "&7*Sends the player a custom message."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "MESSAGE";
			placeHolders[15] = "&eWelcome to the Server!";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			this.modifyCommands(itemMap, ItemCommand.fromString("message: " + event.getMessage(), action, CommandType.BOTH, 0L), true);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "MESSAGE";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		executorPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "REPEATER" : "356"), 1, false, "&e&lSwap-Item", "&7", "&7*Swaps the item to another defined item."), event -> this.swapPane(player, itemMap, action)));
		executorPane.addButton(new Button(ItemHandler.getItem("CLOCK", 1, false, "&e&lDelay", "&7", "&7*Adds a delay between command lines."), event -> this.delayPane(player, itemMap, action)));
		executorPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> this.commandListPane(player, itemMap, action)));
		executorPane.addButton(new Button(this.fillerPaneBItem), 7);
		executorPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the command lines menu."), event -> this.commandListPane(player, itemMap, action)));
		executorPane.open(player);
	}
	
	private void swapPane(final Player player, final ItemMap itemMap, final ActionType action) {
		Interface swapPane = new Interface(true, 6, this.GUIName);
		swapPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the executors menu."), event -> {
			this.executorPane(player, itemMap, action);
		}));
		for (ItemMap item : ItemUtilities.copyItems()) {
			if (item.getNodeLocation() != itemMap.getNodeLocation()) {
				if (itemMap.isAnimated() || itemMap.isDynamic()) { this.setModifyMenu(true, player); itemMap.getAnimationHandler().get(player).setMenu(true, 1); }
				swapPane.addButton(new Button(ItemHandler.addLore(item.getTempItem(), "&7", "&6---------------------------", "&7*Click to set as a swap-item.", "&9&lNode: &a" + item.getConfigName(), "&7"), event -> { 
				this.modifyCommands(itemMap, ItemCommand.fromString("swap-item: " + item.getConfigName(), action, CommandType.BOTH, 0L), true);
				this.commandListPane(player, itemMap, action); }));
			}
		}
		swapPane.open(player);
	}
	
	private void delayPane(final Player player, final ItemMap itemMap, final ActionType action) {
		Interface delayPane = new Interface(true, 6, this.GUIName);
		delayPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the executors menu."), event -> {
			this.executorPane(player, itemMap, action);
		}));
		delayPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Cooldown", "&7", "&7*Click to set a custom", "&7delay for the next command."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "DELAY";
			placeHolders[15] = "180";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				this.modifyCommands(itemMap, ItemCommand.fromString("delay: " + Integer.parseInt(event.getMessage()), action, CommandType.BOTH, Integer.parseInt(event.getMessage())), true);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "DELAY";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.commandListPane(event.getPlayer(), itemMap, action);
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			delayPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:8", k, false, "&9&lDelay: &a&l" + k + " Tick(s)", "&7", "&7*Click to set the", "&7delay of the next command."), event -> {
				this.modifyCommands(itemMap, ItemCommand.fromString("delay: " + k, action, CommandType.BOTH, k), true);
				this.commandListPane(player, itemMap, action);
			}));
		}
		delayPane.open(player);
	}
	
	private void cooldownPane(final Player player, final ItemMap itemMap) {
		Interface cooldownPane = new Interface(true, 6, this.GUIName);
		cooldownPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
			this.commandPane(player, itemMap);
		}));
		cooldownPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Cooldown", "&7", "&7*Click to set a custom commands-cooldown", "&7value for the item."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "COMMAND COOLDOWN";
			placeHolders[15] = "180";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				itemMap.setCommandCooldown(Integer.parseInt(event.getMessage()));
				String[] placeHolders = Language.newString();
				placeHolders[14] = "COMMAND COOLDOWN";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.commandPane(event.getPlayer(), itemMap);
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			cooldownPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7commands-cooldown of the item."), event -> {
				itemMap.setCommandCooldown(k);
				this.commandPane(player, itemMap);
			}));
		}
		cooldownPane.open(player);
	}
	
	private void warmPane(final Player player, final ItemMap itemMap) {
		Interface warmPane = new Interface(true, 6, this.GUIName);
		warmPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
			this.commandPane(player, itemMap);
		}));
		warmPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Warmup", "&7", "&7*Click to set a custom commands-warmup", "&7value for the item."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "COMMAND WARMUP";
			placeHolders[15] = "12";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				itemMap.setWarmDelay(Integer.parseInt(event.getMessage()));
				String[] placeHolders = Language.newString();
				placeHolders[14] = "COMMAND WARMUP";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.commandPane(event.getPlayer(), itemMap);
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			warmPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lWarming: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7commands-warmup of the item."), event -> {
				itemMap.setWarmDelay(k);
				this.commandPane(player, itemMap);
			}));
		}
		warmPane.open(player);
	}
	
	private void costPane(final Player player, final ItemMap itemMap) {
		Interface costPane = new Interface(true, 6, this.GUIName);
		costPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
			this.commandPane(player, itemMap);
		}));
		costPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Cost", "&7", "&7*Click to set a custom commands-cost", "&7value for the item."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "COMMAND COST";
			placeHolders[15] = "340";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				itemMap.setCommandCost(Integer.parseInt(event.getMessage()));
				String[] placeHolders = Language.newString();
				placeHolders[14] = "COMMAND COST";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.commandPane(event.getPlayer(), itemMap);
		}));
		for (int i = 0; i <= 64; i++) {
			final int k = i;
			costPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", (k == 0 ? 1 : k), false, "&9&lCost: &a$&l" + k, "&7", "&7*Click to set the", "&7commands-cost of the item."), event -> {
				itemMap.setCommandCost(k);
				this.commandPane(player, itemMap);
			}));
		}
		costPane.open(player);
	}
	
	private void sequencePane(final Player player, final ItemMap itemMap) {
		Interface sequencePane = new Interface(false, 2, this.GUIName);
		sequencePane.addButton(new Button(this.fillerPaneGItem), 3);
		sequencePane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "CLOCK" : "347"), 1, false, "&a&lSequential", "&7", "&7*Executes the command lines", "&7in order from top to bottom."), event -> {
			itemMap.setCommandSequence(CommandSequence.SEQUENTIAL);this.commandPane(player, itemMap);
		}));
		sequencePane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, false, "&a&lRandom Single", "&7", "&7*Executes one of the command lines", "&7randomly with equal values."), event -> {
			itemMap.setCommandSequence(CommandSequence.RANDOM_SINGLE); this.commandPane(player, itemMap);
		}));
		sequencePane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, false, "&a&lRandom", "&7", "&7*Executes each command line in a", "&7random order with equal values."), event -> {
			itemMap.setCommandSequence(CommandSequence.RANDOM);this.commandPane(player, itemMap);
		}));
		sequencePane.addButton(new Button(this.fillerPaneGItem), 3);
		sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> this.commandPane(player, itemMap)));
		sequencePane.addButton(new Button(this.fillerPaneBItem), 7);
		sequencePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> this.commandPane(player, itemMap)));
		sequencePane.open(player);
	}
	
	private void soundPane(final Player player, final ItemMap itemMap) {
		Interface soundPane = new Interface(true, 6, this.GUIName);
		soundPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
			this.commandPane(player, itemMap);
		}));
		for (Sound sound: Sound.values()) {
			soundPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "MUSIC_DISC_MELLOHI" : "2262"), 1, false, "&f" + sound.name(), "&7", "&7*Click to set the", "&7commands-sound of the item."), event -> {
				itemMap.setCommandSound(sound);
				this.commandPane(player, itemMap);
			}));
		}
		soundPane.open(player);
	}
	
	private void particlePane(final Player player, final ItemMap itemMap) {
		Interface particlePane = new Interface(true, 6, this.GUIName);
		particlePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> {
			this.commandPane(player, itemMap);
		}));
		particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&fFIREWORK_FAKE", "&7", "&7*Click to set the lifetime", "&7commands-particle of the item."), event -> this.lifePane(player, itemMap, "FIREWORK", 1)));
		if (ServerHandler.hasCombatUpdate()) {
			for (org.bukkit.Particle particle: org.bukkit.Particle.values()) {
				particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&f" + particle.name(), "&7", "&7*Click to set the", "&7commands-particle of the item."), event -> this.lifePane(player, itemMap, particle.name(), 0)));
			}
		} else {
			for (org.bukkit.Effect effect: org.bukkit.Effect.values()) {
				particlePane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&f" + effect.name(), "&7", "&7*Click to set the", "&7commands-particle of the item."), event -> this.lifePane(player, itemMap, effect.name(), 0)));
			}
		}
		particlePane.open(player);
	}
	
	private void lifePane(final Player player, final ItemMap itemMap, final String particle, final int stage) {
		Interface lifePane = new Interface(true, 6, this.GUIName);
		lifePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> {
			this.particlePane(player, itemMap);
		}));
		lifePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom LifeTime", "&7", "&7*Click to set a lifetime (duration)", "&7value for particle effect."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "PARTICLE LIFETIME";
			placeHolders[15] = "170";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				if (stage == 0) {
					itemMap.setCommandParticle(particle + ":" + event.getMessage());
				} else {
					this.explosionPane(player, itemMap, particle, Integer.parseInt(event.getMessage()));
				}
				String[] placeHolders = Language.newString();
				placeHolders[14] = "PARTICLE LIFETIME";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
				this.particlePane(event.getPlayer(), itemMap);
			}
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			lifePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:9", k, false, "&9&lLifeTime: &a&l" + k + " Tick(s)", "&7", "&7*Click to set the lifetime", "&7(duration) of the particle effect."), event -> {
				if (stage == 0) {
					itemMap.setCommandParticle(particle + ":" + k);
					this.commandPane(player, itemMap);
				} else {
					this.explosionPane(player, itemMap, particle, k);
				}
			}));
		}
		lifePane.open(player);
	}
	
	private void explosionPane(final Player player, final ItemMap itemMap, final String particle, final int lifetime) {
		Interface patternPane = new Interface(true, 2, this.GUIName);
		patternPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> {
			this.particlePane(player, itemMap);
		}));
		for (Type explosion: Type.values()) {
			patternPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, false, "&f" + explosion.name(), "&7", "&7*Click to set the pattern", "&7of the firework explosion effect."), 
					event -> this.colorParticlePane(player, itemMap, particle, lifetime, explosion, null)));
		}
		patternPane.open(player);
	}
	
	private void colorParticlePane(final Player player, final ItemMap itemMap, final String particle, final int lifetime, final Type explosion, final DyeColor color1) {
		Interface colorPane = new Interface(true, 6, this.GUIName);
		colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the particle menu."), event -> {
			this.particlePane(player, itemMap);
		}));
		for (DyeColor color: DyeColor.values()) {
			colorPane.addButton(new Button(ItemHandler.getItem("GRAY_DYE", 1, false, "&f" + color.name(), "&7", "&7*Click to set the " + (color1 != null ? "&c&lend color" : "&9&lstart color"), "&7of the firework explosion effect."), event -> {
				if (color1 != null) {
					itemMap.setCommandParticle(particle + ":" + color1.name() + ":" + color.name() + ":" + explosion.name() + ":" + lifetime);
					this.commandPane(player, itemMap);
				} else {
					this.colorParticlePane(player, itemMap, particle, lifetime, explosion, color);
				}
			}));
		}
		colorPane.open(player);
	}
	
	private void typePane(final Player player, final ItemMap itemMap) {
		Interface typePane = new Interface(false, 2, this.GUIName);
		typePane.addButton(new Button(this.fillerPaneGItem), 3);
		typePane.addButton(new Button(ItemHandler.getItem("DIAMOND_PICKAXE", 1, false, "&a&lInteract", "&7", "&7*Executes the command when", "&7the player clicks the item", "&7with it in their hand", "&7either in the air or on a block."), event -> {
			itemMap.setCommandType(CommandType.INTERACT);this.commandPane(player, itemMap);
		}));
		typePane.addButton(new Button(ItemHandler.getItem("NETHER_STAR", 1, false, "&a&lBoth", "&7", "&7*Executes the item whether the", "&7player clicks the item in", "&7their inventory or in the air", "&7or on a block."), event -> {
			itemMap.setCommandType(CommandType.BOTH);this.commandPane(player, itemMap);
		}));
		typePane.addButton(new Button(ItemHandler.getItem("CHEST", 1, false, "&a&lInventory", "&7", "&7*Executes the commands only when", "&7the player clicks the item", "&7with their cursor in their inventory."), event -> {
			itemMap.setCommandType(CommandType.INVENTORY);this.commandPane(player, itemMap);
		}));
		typePane.addButton(new Button(this.fillerPaneGItem), 3);
		typePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> this.commandPane(player, itemMap)));
		typePane.addButton(new Button(this.fillerPaneBItem), 7);
		typePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item commands menu."), event -> this.commandPane(player, itemMap)));
		typePane.open(player);
	}
	
//  ============================================================================================================================================================================================================================================================
	
	private void enchantPane(final Player player, final ItemMap itemMap) { // TokenEnchant???
		Interface enchantPane = new Interface(true, 6, this.GUIName);
		enchantPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
			this.creatingPane(player, itemMap);
		}));
		for (Enchantment enchant: Enchantment.values()) {
			boolean containsKey = itemMap.getEnchantments().containsKey(ItemHandler.getEnchantName(enchant).toUpperCase());
			ItemStack enchantItem = ItemHandler.getItem((containsKey ? "ENCHANTED_BOOK" : "BOOK"), 1, false, "&f" + ItemHandler.getEnchantName(enchant).toUpperCase(), "&7", 
					"&7*Click to add this enchantment", "&7to the custom item.", "&7", "&9&lENABLED: &a" + (containsKey + "").toUpperCase(), (containsKey ? "&7" : ""), 
					(containsKey ? "&9&lLEVEL: &a" + itemMap.getEnchantments().get(ItemHandler.getEnchantName(enchant).toUpperCase()) : ""));
			enchantPane.addButton(new Button(enchantItem, event -> {
				if (containsKey) {
					Map < String, Integer > enchantments = itemMap.getEnchantments();
					enchantments.remove(ItemHandler.getEnchantName(enchant).toUpperCase());
					itemMap.setEnchantments(enchantments);
					this.enchantPane(player, itemMap);
				} else {
					this.enchantLevelPane(player, itemMap, enchant);
				}
			}));
		}
		enchantPane.open(player);
	}
	
	private void enchantLevelPane(final Player player, final ItemMap itemMap, final Enchantment enchant) {
		Interface enchantLevelPane = new Interface(true, 6, this.GUIName);
		enchantLevelPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the enchant selection menu."), event -> {
			this.enchantPane(player, itemMap);
		}));
		enchantLevelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Count", "&7", "&7*Click to set a custom damage", "&7value for the item."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ENCHANT LEVEL";
			placeHolders[15] = "86";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				Map < String, Integer > enchantments = itemMap.getEnchantments();
				enchantments.put(ItemHandler.getEnchantName(enchant).toUpperCase(), Integer.parseInt(event.getMessage()));
				itemMap.setEnchantments(enchantments);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "ENCHANT LEVEL";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.enchantPane(event.getPlayer(), itemMap);
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			enchantLevelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lLevel: &a&l" + k, "&7", "&7*Click to set the", "&7level of the item enchantment.", "&7", "&7This will be &l" + 
			ItemHandler.getEnchantName(enchant).toUpperCase() + ":" + k), event -> {
				Map < String,
				Integer > enchantments = itemMap.getEnchantments();enchantments.put(ItemHandler.getEnchantName(enchant).toUpperCase(), k);itemMap.setEnchantments(enchantments);this.enchantPane(player, itemMap);
			}));
		}
		enchantLevelPane.open(player);
	}
	
	private void flagPane(final Player player, final ItemMap itemMap) {
		Interface flagPane = new Interface(false, 5, this.GUIName);
		flagPane.addButton(new Button(this.fillerPaneGItem), 2);
		flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.isOpBypass(), "&a&l&nAllowOpBypass", "&7", "&7*Allows the player to bypass any", "&7itemflags that add restrictions", "&7ONLY if they are OP.", 
				"&9&lENABLED: &a" + (itemMap.isOpBypass() + "").toUpperCase()), event -> {
			if (itemMap.isOpBypass()) {
				itemMap.setOpBypass(false);
			} else {
				itemMap.setOpBypass(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(this.fillerPaneGItem));
		flagPane.addButton(new Button(ItemHandler.getItem("38", 1, itemMap.isInventoryClose(), "&a&l&nInventory Close", "&7", "&7*Closes the players inventory", "&7when clicking an item.", "&9&lENABLED: &a" +
		(itemMap.isInventoryClose() + "").toUpperCase()), event -> {
			if (itemMap.isInventoryClose()) {
				itemMap.setCloseInventory(false);
			} else {
				itemMap.setCloseInventory(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(this.fillerPaneGItem));
		flagPane.addButton(new Button(ItemHandler.getItem(itemMap.isCreativeBypass() ? "ENCHANTED_GOLDEN_APPLE" : "GOLDEN_APPLE", 1, itemMap.isCreativeBypass(), "&a&l&nCreativeBypass", "&7", "&7*Allows the player to bypass any", 
				"&7itemflags that add restrictions", "&7ONLY if they are in creative mode.", "&9&lENABLED: &a" + (itemMap.isCreativeBypass() + "").toUpperCase()), event -> {
			if (itemMap.isCreativeBypass()) {
				itemMap.setCreativeBypass(false);
			} else {
				itemMap.setCreativeBypass(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(this.fillerPaneGItem), 2);
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WOODEN_SWORD" : "268"), 1, itemMap.isVanilla(), "&a&l&nVanilla", "&7", "&7*Sets the item to be given", "&7as a default no-name item.", 
				"&7&lNOTE: &7Itemflags and commands", "&7will NOT work unless the vanilla-status", "&7or vanilla-control itemflags are defined", "&9&lENABLED: &a" + (itemMap.isVanilla() + "").toUpperCase()), event -> {
			if (itemMap.isVanilla()) {
				itemMap.setVanilla(false);
			} else {
				itemMap.setVanilla(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, itemMap.isVanillaStatus(), "&a&l&nVanilla Status", "&7", "&7*Allows the Vanilla itemflag to retain", "&7the properties functionality without.", 
				"&7making it only a plugin specific item", "&9&lENABLED: &a" + (itemMap.isVanillaStatus() + "").toUpperCase()), event -> {
			if (itemMap.isVanillaStatus()) {
				itemMap.setVanillaStatus(false);
			} else {
				itemMap.setVanillaStatus(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WOODEN_PICKAXE" : "270"), 1, itemMap.isVanillaControl(), "&a&l&nVanilla Control", "&7", "&7*Allows the Vanilla itemflag to retain",
				"&7the use of commands and itemflags.", "&9&lENABLED: &a" + (itemMap.isVanillaControl() + "").toUpperCase()), event -> {
			if (itemMap.isVanillaControl()) {
				itemMap.setVanillaControl(false);
			} else {
				itemMap.setVanillaControl(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "FILLED_MAP" : "MAP"), 1, itemMap.isOnlyFirstJoin(), "&a&l&nFirst Join", "&7", "&7*Gives the item only ONCE.", 
				"&9&lENABLED: &a" + (itemMap.isOnlyFirstJoin() + "").toUpperCase()), event -> {
			if (itemMap.isOnlyFirstJoin()) {
				itemMap.setOnlyFirstJoin(false);
			} else {
				itemMap.setOnlyFirstJoin(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, itemMap.isOnlyFirstWorld(), "&a&l&nFirst World", "&7", "&7*Gives the item only ONCE per world.", "&9&lENABLED: &a" + (itemMap.isOnlyFirstWorld() + "").toUpperCase()), event -> {
			if (itemMap.isOnlyFirstWorld()) {
				itemMap.setOnlyFirstWorld(false);
			} else {
				itemMap.setOnlyFirstWorld(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, itemMap.isIpLimted(), "&a&l&nIP Limit", "&7", "&7*Limits the item to the specific ip", "&7of the player, prevents alt", "&7accounts from receiving items.",
				"&7Useful to prevent item duplication", "&9&lENABLED: &a" + (itemMap.isIpLimted() + "").toUpperCase()), event -> {
			if (itemMap.isIpLimted()) {
				itemMap.setIpLimited(false);
			} else {
				itemMap.setIpLimited(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem(itemMap.isUnbreakable() ? "ENCHANTED_BOOK" : "BOOK", 1, itemMap.isUnbreakable(), "&a&l&nUnbreakable", "&7", "&7*Allows the item to be INDESTRUCTIBLE!", 
				"&9&lENABLED: &a" + (itemMap.isUnbreakable() + "").toUpperCase()), event -> {
			if (itemMap.isUnbreakable()) {
				itemMap.setUnbreakable(false);
			} else {
				itemMap.setUnbreakable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "ENDER_EYE" : "381"), 1, itemMap.isAttributesInfo(), "&a&l&nHide Attributes", "&7", "&7*Hides all item info",
				"&7Firework Color, Damage Values, Enchants, etc.", "&9&lENABLED: &a" + (itemMap.isAttributesInfo() + "").toUpperCase()), event -> {
			if (itemMap.isAttributesInfo()) {
				itemMap.setAttributesInfo(false);
			} else {
				itemMap.setAttributesInfo(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("IRON_SWORD", 1, itemMap.isDurabilityBar(), "&a&l&nHide Durability", "&7", "&7*Hides the durability bar.", "&9&lENABLED: &a" + (itemMap.isDurabilityBar() + "").toUpperCase()), event -> {
			if (itemMap.isDurabilityBar()) {
				itemMap.setDurabilityBar(false);
			} else {
				itemMap.setDurabilityBar(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("COBBLESTONE", 1, itemMap.isPlaceable(), "&a&l&nPlacement", "&7", "&7*Prevents the item from being", "&7placed on the ground and", "&7in any item-frames.", 
				"&9&lENABLED: &a" + (itemMap.isPlaceable() + "").toUpperCase()), event -> {
			if (itemMap.isPlaceable()) {
				itemMap.setPlaceable(false);
			} else {
				itemMap.setPlaceable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("MINECART", 1, itemMap.isMovement(), "&a&l&nInventory Modify", "&7", "&7*Prevents the item from being", "&7moved or switched to other slots",
				"&7and blocks placement in item-frames.", "&9&lENABLED: &a" + (itemMap.isMovement() + "").toUpperCase()), event -> {
			if (itemMap.isMovement()) {
				itemMap.setMovement(false);
			} else {
				itemMap.setMovement(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, itemMap.isDynamic(), "&a&l&nDynamic", "&7", "&7*Allows the item to dynamically", "&7update every 100 ticks", "&7Useful for updating placeholders.", 
				"&9&lENABLED: &a" + (itemMap.isDynamic() + "").toUpperCase()), event -> {
			if (itemMap.isDynamic()) {
				itemMap.setDynamic(false);
			} else {
				itemMap.setDynamic(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("EGG", 1, itemMap.isAnimated(), "&a&l&nAnimate", "&7", "&7*Allows the item to animate between", "&7its different iterations defined", "&7under the animations tab.", 
				"&9&lENABLED: &a" + (itemMap.isAnimated() + "").toUpperCase()), event -> {
			if (itemMap.isAnimated()) {
				itemMap.setAnimate(false);
			} else {
				itemMap.setAnimate(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "CHEST_MINECART" : "342"), 1, itemMap.isItemStore(), "&a&l&nItem Store", "&7", "&7*Prevents the storage of the item", 
				"&7in any object which can house an item.", "&9&lENABLED: &a" + (itemMap.isItemStore() + "").toUpperCase()), event -> {
			if (itemMap.isItemStore()) {
				itemMap.setItemStore(false);
			} else {
				itemMap.setItemStore(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("BOW", 1, itemMap.isCancelEvents(), "&a&l&nCancel Events", "&7", "&7*Prevents almost any event from executing", "&7when right-clicking the item.", "&9&lENABLED: &a" +
		(itemMap.isCancelEvents() + "").toUpperCase()), event -> {
			if (itemMap.isCancelEvents()) {
				itemMap.setCancelEvents(false);
			} else {
				itemMap.setCancelEvents(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "OAK_DOOR" : (ServerHandler.hasSpecificUpdate("1_8") ? "324" : "64")), 1, itemMap.isCountLock(), "&a&l&nCount Lock", "&7", "&7*Locks the items count which", 
				"&7allows infinite consumption of the item.", "&9&lENABLED: &a" + (itemMap.isCountLock() + "").toUpperCase()), event -> {
			if (itemMap.isCountLock()) {
				itemMap.setCountLock(false);
			} else {
				itemMap.setCountLock(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("BONE", 1, itemMap.isDeathDroppable(), "&a&l&nDeath Drops", "&7", "&7*Prevents the item from being dropped", "&7upon death, this removes the item.", "&9&lENABLED: &a" + 
		(itemMap.isDeathDroppable() + "").toUpperCase()), event -> {
			if (itemMap.isDeathDroppable()) {
				itemMap.setDeathDroppable(false);
			} else {
				itemMap.setDeathDroppable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("HOPPER", 1, itemMap.isSelfDroppable(), "&a&l&nSelf Drops", "&7", "&7*Prevents the item from being dropped", "&7by the player, returns back to their inventory.", 
				"&9&lENABLED: &a" + (itemMap.isSelfDroppable() + "").toUpperCase()), event -> {
			if (itemMap.isSelfDroppable()) {
				itemMap.setSelfDroppable(false);
			} else {
				itemMap.setSelfDroppable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("CACTUS", 1, itemMap.isDisposable(), "&a&l&nDisposable", "&7", "&7*When the item command is run,", "&7x1 of the item will be removed.", "&9&lENABLED: &a" + 
		(itemMap.isDisposable() + "").toUpperCase()), event -> {
			if (itemMap.isDisposable()) {
				itemMap.setDisposable(false);
			} else {
				itemMap.setDisposable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("FURNACE", 1, itemMap.isItemModify(), "&a&l&nItem Modifiable", "&7", "&7*Blocks the item from being", "&7repaired or enchanted in-game.", "&9&lENABLED: &a" + 
		(itemMap.isItemModify() + "").toUpperCase()), event -> {
			if (itemMap.isItemModify()) {
				itemMap.setItemModify(false);
			} else {
				itemMap.setItemModify(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("ANVIL", 1, itemMap.isItemRepairable(), "&a&l&nItem Repairable", "&7", "&7*Blocks the item from being", "&7used in an anvil or repaired.", "&9&lENABLED: &a" +
		(itemMap.isItemRepairable() + "").toUpperCase()), event -> {
			if (itemMap.isItemRepairable()) {
				itemMap.setItemRepairable(false);
			} else {
				itemMap.setItemRepairable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "CRAFTING_TABLE" : "58"), 1, itemMap.isItemCraftable(), "&a&l&nItem Craftable", "&7", "&7*Blocks the item from being", 
				"&7used in a crafting recipe.", "&9&lENABLED: &a" + (itemMap.isItemCraftable() + "").toUpperCase()), event -> {
			if (itemMap.isItemCraftable()) {
				itemMap.setItemCraftable(false);
			} else {
				itemMap.setItemCraftable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WHEAT_SEEDS" : "295"), 1, itemMap.isAlwaysGive(), "&a&l&nAlways Give", "&7", "&7*Gives the item every time the player", 
				"&7performs one of the triggers actions.", "&7regardless of already having the item", "&9&lENABLED: &a" + (itemMap.isAlwaysGive() + "").toUpperCase()), event -> {
			if (itemMap.isAlwaysGive()) {
				itemMap.setAlwaysGive(false);
			} else {
				itemMap.setAlwaysGive(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND_HELMET", 1, itemMap.isItemChangable(), "&a&l&nAllow Modifications", "&7", "&7*Allows the players to modify the item", "&7while retaining all properties.", 
				"&9&lENABLED: &a" + (itemMap.isItemChangable() + "").toUpperCase()), event -> {
			if (itemMap.isItemChangable()) {
				itemMap.setItemChangable(false);
			} else {
				itemMap.setItemChangable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("ITEM_FRAME", 1, itemMap.isGiveNext(), "&a&l&nGive Next", "&7", "&7*Gives the item to the next available slot", "&7only if the defined slot is full.", "&9&lENABLED: &a" +
		(itemMap.isGiveNext() + "").toUpperCase()), event -> {
			if (itemMap.isGiveNext()) {
				itemMap.setGiveNext(false);
			} else {
				itemMap.setGiveNext(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, itemMap.isDropFull(), "&a&l&nDrop Full", "&7", "&7*Drops the item on the ground if", "&7the players inventory is full.", "&9&lENABLED: &a" + 
		(itemMap.isDropFull() + "").toUpperCase()), event -> {
			if (itemMap.isDropFull()) {
				itemMap.setDropFull(false);
			} else {
				itemMap.setDropFull(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WRITABLE_BOOK" : "386"), 1, itemMap.isOverwritable(), "&a&l&nOverwrite", "&7", "&7*Allows the item to overwrite", 
				"&7any existing items in the defined slot.", "&9&lENABLED: &a" + (itemMap.isOverwritable() + "").toUpperCase()), event -> {
			if (itemMap.isOverwritable()) {
				itemMap.setOverwritable(false);
			} else {
				itemMap.setOverwritable(true);
			}
			this.flagPane(player, itemMap);
		}));
		flagPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
			this.setItemFlags(itemMap);this.creatingPane(player, itemMap);
		}));
		flagPane.addButton(new Button(this.fillerPaneBItem), 7);
		flagPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
			this.setItemFlags(itemMap);this.creatingPane(player, itemMap);
		}));
		flagPane.open(player);
	}
	
	private void setItemFlags(final ItemMap itemMap) {
		String itemflags = "";
		if (itemMap.isVanilla()) { itemflags += "VANILLA, "; }
		if (itemMap.isVanillaStatus()) { itemflags += "VANILLA-STATUS, "; }
		if (itemMap.isVanillaControl()) { itemflags += "VANILLA-CONTROL, "; }
		if (itemMap.isOnlyFirstJoin()) { itemflags += "FIRST-JOIN, "; }
		if (itemMap.isOnlyFirstWorld()) { itemflags += "FIRST-WORLD, "; }
		if (itemMap.isIpLimted()) { itemflags += "IP-LIMIT, "; }
		if (itemMap.isUnbreakable()) { itemflags += "UNBREAKABLE, "; }
		if (itemMap.isAttributesInfo()) { itemflags += "HIDE-ATTRIBUTES, "; }
		if (itemMap.isDurabilityBar()) { itemflags += "HIDE-DURABILITY, "; }
		if (itemMap.isPlaceable()) { itemflags += "PLACEMENT, "; }
		if (itemMap.isMovement()) { itemflags += "INVENTORY-MODIFY, "; }
		if (itemMap.isDynamic()) { itemflags += "DYNAMIC, "; }
		if (itemMap.isAnimated()) { itemflags += "ANIMATE, "; }
		if (itemMap.isItemStore()) { itemflags += "ITEM-STORE, "; }
		if (itemMap.isCancelEvents()) { itemflags += "CANCEL-EVENTS, "; }
		if (itemMap.isCountLock()) { itemflags += "COUNT-LOCK, "; }
		if (itemMap.isDeathDroppable()) { itemflags += "DEATH-DROPS, "; }
		if (itemMap.isSelfDroppable()) { itemflags += "SELF-DROPS, "; }
		if (itemMap.isDisposable()) { itemflags += "DISPOSABLE, "; }
		if (itemMap.isItemModify()) { itemflags += "ITEM-MODIFIABLE, "; }
		if (itemMap.isItemRepairable()) { itemflags += "ITEM-REPAIRABLE, "; }
		if (itemMap.isItemCraftable()) { itemflags += "ITEM-CRAFTABLE, "; }
		if (itemMap.isAlwaysGive()) { itemflags += "ALWAYS-GIVE, "; }
		if (itemMap.isItemChangable()) { itemflags += "ITEM-CHANGABLE, "; }
		if (itemMap.isGiveNext()) { itemflags += "GIVE-NEXT, "; }
		if (itemMap.isDropFull()) { itemflags += "DROP-FULL, "; }
		if (itemMap.isOverwritable()) { itemflags += "OVERWRITE, "; }
		if (itemMap.isOpBypass()) { itemflags += "ALLOWOPBYPASS, "; }
		if (itemMap.isCreativeBypass()) { itemflags += "ALLOWCREATIVEBYPASS, "; }
		if (itemflags.endsWith(", ")) { itemflags = itemflags.substring(0, itemflags.length() - 2); }
		itemMap.setItemFlags(itemflags);
	}
	
	private void triggerPane(final Player player, final ItemMap itemMap) {
		Interface triggerPane = new Interface(false, 2, this.GUIName);
		triggerPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, itemMap.isGiveOnDisabled(), "&c&l&nDISABLED", "&7", "&7*Prevents the item from given", "&7through the use of triggers.", "&7", "&7Useful to only get the item", 
				"&7using &l/itemjoin get <item>", "&9&lENABLED: &a" + (itemMap.isGiveOnDisabled() + "").toUpperCase()), event -> {
			if (itemMap.isGiveOnDisabled()) {
				itemMap.setGiveOnDisabled(false);
			} else {
				itemMap.setGiveOnJoin(false);
				itemMap.setOnlyFirstJoin(false);
				itemMap.setOnlyFirstWorld(false);
				itemMap.setGiveOnRespawn(false);
				itemMap.setGiveOnWorldChange(false);
				itemMap.setUseOnLimitSwitch(false);
				itemMap.setGiveOnRegionEnter(false);
				itemMap.setTakeOnRegionLeave(false);
				itemMap.setGiveOnDisabled(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "OAK_SIGN" : "SIGN"), 1, itemMap.isGiveOnJoin(), "&e&l&nJoin", "&7", "&7*Gives the item when the", "&7player logs into the server.", 
				"&9&lENABLED: &a" + (itemMap.isGiveOnJoin() + "").toUpperCase()), event -> {
			if (itemMap.isGiveOnJoin()) {
				itemMap.setGiveOnJoin(false);
			} else {
				itemMap.setGiveOnJoin(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "FILLED_MAP" : "MAP"), 1, itemMap.isOnlyFirstJoin(), "&e&l&nFirst Join", "&7", "&7*Gives the item when the", "&7player logs into the server.", 
				"&7for the first time only", "&9&lENABLED: &a" + (itemMap.isOnlyFirstJoin() + "").toUpperCase()), event -> {
			if (itemMap.isOnlyFirstJoin()) {
				itemMap.setOnlyFirstJoin(false);
			} else {
				itemMap.setOnlyFirstJoin(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, itemMap.isOnlyFirstWorld(), "&e&l&nFirst World", "&7", "&7*Gives the item when the", "&7player enters each of the defined.", "&7worlds for the first time", "&7", 
				"&7Disables the respawn trigger", "&7if needed use the itemflag", "&7for first world instead", "&9&lENABLED: &a" + (itemMap.isOnlyFirstWorld() + "").toUpperCase()), event -> {
			if (itemMap.isOnlyFirstWorld()) {
				itemMap.setOnlyFirstWorld(false);
			} else {
				itemMap.setOnlyFirstWorld(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.isGiveOnRespawn(), "&e&l&nRespawn", "&7", "&7*Gives the item when the", "&7player respawns from a death event.", "&9&lENABLED: &a" + 
		(itemMap.isGiveOnRespawn() + "").toUpperCase()), event -> {
			if (itemMap.isGiveOnRespawn()) {
				itemMap.setGiveOnRespawn(false);
			} else {
				itemMap.setGiveOnRespawn(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem("STONE_BUTTON", 1, itemMap.isGiveOnWorldChange(), "&e&l&nWorld Switch", "&7", "&7*Gives the item when the", "&7player teleports to one", "&7of the specified worlds.", 
				"&9&lENABLED: &a" + (itemMap.isGiveOnWorldChange() + "").toUpperCase()), event -> {
			if (itemMap.isGiveOnWorldChange()) {
				itemMap.setGiveOnWorldChange(false);
			} else {
				itemMap.setGiveOnWorldChange(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem("LEVER", 1, itemMap.isUseOnLimitSwitch(), "&e&l&nGamemode Switch", "&7", "&7*Gives the item when the", "&7player changes gamemodes to any", "&7of the defined limit-modes.", 
				"&9&lENABLED: &a" + (itemMap.isUseOnLimitSwitch() + "").toUpperCase()), event -> {
			if (itemMap.isUseOnLimitSwitch()) {
				itemMap.setUseOnLimitSwitch(false);
			} else {
				itemMap.setUseOnLimitSwitch(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, itemMap.isGiveOnRegionEnter(), "&e&l&nRegion Enter", "&7", "&7*Gives the item when the", "&7player enters any of the enabled-regions.", "&9&lENABLED: &a" +
		(itemMap.isGiveOnRegionEnter() + "").toUpperCase()), event -> {
			if (itemMap.isGiveOnRegionEnter()) {
				itemMap.setGiveOnRegionEnter(false);
			} else {
				itemMap.setGiveOnRegionEnter(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem("DIAMOND_PICKAXE", 1, itemMap.isTakeOnRegionLeave(), "&e&l&nRegion Leave", "&7", "&7*Removes the item when the", "&7player leaves any of the enabled-regions.", "&9&lENABLED: &a" +
		(itemMap.isTakeOnRegionLeave() + "").toUpperCase()), event -> {
			if (itemMap.isTakeOnRegionLeave()) {
				itemMap.setTakeOnRegionLeave(false);
			} else {
				itemMap.setTakeOnRegionLeave(true);
			}
			this.triggerPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
			this.setTriggers(itemMap);this.creatingPane(player, itemMap);
		}));
		triggerPane.addButton(new Button(this.fillerPaneBItem), 7);
		triggerPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu"), event -> {
			this.setTriggers(itemMap);this.creatingPane(player, itemMap);
		}));
		triggerPane.open(player);
	}
	
	private void setTriggers(final ItemMap itemMap) {
		String triggers = "";
		if (itemMap.isGiveOnDisabled()) { triggers += "DISABLED, "; }
		if (itemMap.isGiveOnJoin()) { triggers += "JOIN, "; }
		if (itemMap.isOnlyFirstJoin()) { triggers += "FIRST-JOIN, "; }
		if (itemMap.isOnlyFirstWorld()) { triggers += "FIRST-WORLD, "; }
		if (itemMap.isGiveOnRespawn()) { triggers += "RESPAWN, "; }
		if (itemMap.isGiveOnWorldChange()) { triggers += "WORLD-SWITCH, "; }
		if (itemMap.isUseOnLimitSwitch()) { triggers += "GAMEMODE-SWITCH, "; }
		if (itemMap.isGiveOnRegionEnter()) { triggers += "REGION-ENTER, "; }
		if (itemMap.isTakeOnRegionLeave()) { triggers += "REGION-REMOVE, "; }
		if (triggers.endsWith(", ")) { triggers = triggers.substring(0, triggers.length() - 2); }
		itemMap.setTriggers(triggers);
	}
	
	private void worldPane(final Player player, final ItemMap itemMap) {
		Interface worldPane = new Interface(false, 6, this.GUIName);
		List < String > enabledWorlds = itemMap.getEnabledWorlds();
		int i = 44;
		worldPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, itemMap.containsWorld("ALL", itemMap), "&a&l&nGLOBAL", "&7", "&7*Click to enable the", "&7custom item in &lALL WORLDS.", "&9&lENABLED: &a" + 
		(itemMap.containsWorld("ALL", itemMap) + "").toUpperCase()), event -> {
			if (itemMap.containsWorld("ALL", itemMap)) {
				enabledWorlds.remove("GLOBAL");
				enabledWorlds.remove("ALL");
			} else {
				enabledWorlds.add("GLOBAL");
			}
			itemMap.setEnabledWorlds(enabledWorlds);this.worldPane(player, itemMap);
		}));
		for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
			String worldMaterial = (ServerHandler.hasAquaticUpdate() ? "GRASS_BLOCK" : "2");
			i--;
			if (world.getEnvironment().equals(Environment.NETHER)) {
				worldMaterial = "NETHERRACK";
			} else if (world.getEnvironment().equals(Environment.THE_END)) {
				worldMaterial = (ServerHandler.hasAquaticUpdate() ? "END_STONE" : "121");
			}
			worldPane.addButton(new Button(ItemHandler.getItem(worldMaterial, 1, itemMap.containsWorld(world.getName(), itemMap), "&f&l" + world.getName(), "&7", "&7*Click to enable the", "&7custom item in this world.", 
					"&9&lENABLED: &a" + (itemMap.containsWorld(world.getName(), itemMap) + "").toUpperCase()), event -> {
				if (itemMap.containsWorld(world.getName(), itemMap)) {
					enabledWorlds.remove(world.getName());
				} else {
					enabledWorlds.add(world.getName());
				}
				itemMap.setEnabledWorlds(enabledWorlds);this.worldPane(player, itemMap);
			}));
		}
		worldPane.addButton(new Button(ItemHandler.getItem("AIR", 1, false, "")), i);
		worldPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		worldPane.addButton(new Button(this.fillerPaneBItem), 7);
		worldPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		worldPane.open(player);
	}
	
	private void regionPane(final Player player, final ItemMap itemMap) {
		Interface regionPane = new Interface(true, 6, this.GUIName);
		List < String > enabledRegions = itemMap.getEnabledRegions();
		regionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
			this.creatingPane(player, itemMap);
		}));
		regionPane.addButton(new Button(ItemHandler.getItem("OBSIDIAN", 1, itemMap.containsRegion("UNDEFINED", itemMap), "&c&l&nUNDEFINED", "&7", "&7*Click to enable the", "&7custom item in &lALL REGIONS.", "&9&lENABLED: &a" + 
		(itemMap.containsRegion("UNDEFINED", itemMap) + "").toUpperCase()), event -> {
			if (itemMap.containsRegion("UNDEFINED", itemMap)) {
				enabledRegions.remove("UNDEFINED");
			} else {
				enabledRegions.add("UNDEFINED");
			}
			itemMap.setEnabledRegions(enabledRegions);this.regionPane(player, itemMap);
		}));
		for (World world: ItemJoin.getInstance().getServer().getWorlds()) {
			for (String region: ConfigHandler.getDepends().getGuard().getRegions(world).keySet()) {
				String regionMaterial = (ServerHandler.hasAquaticUpdate() ? "GRASS_BLOCK" : "2");
				if (world.getEnvironment().equals(Environment.NETHER)) {
					regionMaterial = "NETHERRACK";
				} else if (world.getEnvironment().equals(Environment.THE_END)) {
					regionMaterial = (ServerHandler.hasAquaticUpdate() ? "END_STONE" : "121");
				}
				regionPane.addButton(new Button(ItemHandler.getItem(regionMaterial, 1, itemMap.containsRegion(region, itemMap), "&f&l" + region, "&7", "&a&lWORLD: &f" + world.getName(), "&7", "&7*Click to enable the", 
						"&7custom item in this region.", "&9&lENABLED: &a" + (itemMap.containsRegion(region, itemMap) + "").toUpperCase()), event -> {
					if (itemMap.containsRegion(region, itemMap)) {
						enabledRegions.remove(region);
					} else {
						enabledRegions.add(region);
					}
					itemMap.setEnabledRegions(enabledRegions);this.regionPane(player, itemMap);
				}));
			}
		}
		regionPane.open(player);
	}
	
	private void lorePane(final Player player, final ItemMap itemMap) {
		Interface lorePane = new Interface(true, 2, this.GUIName);
		lorePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		lorePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Lore Line", "&7", "&7*Add a new lore line", "&7to the item lore."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "LORE LINE";
			placeHolders[15] = "&bThis is a new lore line.";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			String[] placeHolders = Language.newString();
			placeHolders[14] = "LORE LINE";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			List<String> lore = new ArrayList<String>(); 
			if (itemMap.getCustomLore() != null) {
				lore = itemMap.getCustomLore(); 
				lore.add(event.getMessage()); 
				itemMap.setCustomLore(lore); 
			} else { 
				lore.add(event.getMessage()); 
				itemMap.setCustomLore(lore);
			}
			this.lorePane(player, itemMap);
		}));
		if (itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
			for (int i = 1; i <= itemMap.getCustomLore().size(); i++) {
				final int k = i;
				lorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&fLore " + k, "&7", "&7*Click modify this lore line.", "&9&lLore: &a" + itemMap.getCustomLore().get(k - 1)), event -> this.modifyLoreLinePane(player, itemMap, k - 1)));
			}
		}
		lorePane.open(player);
	}
	
	private void modifyLoreLinePane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyLorePane = new Interface(false, 2, this.GUIName);
		modifyLorePane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&e&l&nModify", "&7", "&7*Change the lore line.", "&9&lLore: &a" + itemMap.getCustomLore().get(position)), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "LORE LINE";
			placeHolders[15] = "&bThis is a new lore line.";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			List <  String> lore = itemMap.getCustomLore();
			lore.set(position, event.getMessage());
			itemMap.setCustomLore(lore);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "LORE LINE";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.modifyLoreLinePane(event.getPlayer(), itemMap, position);
		}));
		modifyLorePane.addButton(new Button(this.fillerPaneGItem));
		modifyLorePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this lore line."), event -> {
			List < String > lore = itemMap.getCustomLore();
			lore.remove(position);
			itemMap.setCustomLore(lore);
			this.lorePane(player, itemMap);
		}));
		modifyLorePane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.lorePane(player, itemMap)));
		modifyLorePane.addButton(new Button(this.fillerPaneBItem), 7);
		modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.lorePane(player, itemMap)));
		modifyLorePane.open(player);
	}
	
//  ============================================== //
//           Animation Definition Menus            //
//  ============================================== //
	
	private void animateMaterialPane(final Player player, final ItemMap itemMap) {
		Interface animateMaterialPane = new Interface(true, 2, this.GUIName);
		animateMaterialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
			if (!itemMap.getDynamicMaterials().isEmpty()) {
				itemMap.setAnimate(true);
				if (ItemHandler.purgeDelay(itemMap.getDynamicMaterials().get(0)).contains(":")) {
					String[] material = ItemHandler.purgeDelay(itemMap.getDynamicMaterials().get(0)).split(":");
					itemMap.setMaterial(ItemHandler.getMaterial(material[0], null));
					itemMap.setDataValue((short)Integer.parseInt(material[1]));
				} else {
					itemMap.setMaterial(ItemHandler.getMaterial(ItemHandler.purgeDelay(itemMap.getDynamicMaterials().get(0)), null));
				}
			}
			this.animationPane(player, itemMap);
		}));
		animateMaterialPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Material", "&7", "&7*Add a new material", "&7to be animated between."), event -> this.selectMaterialPane(player, itemMap, 0, true)));
		for (int i = 1; i <= itemMap.getDynamicMaterials().size(); i++) {
			final int k = i;
			animateMaterialPane.addButton(new Button(ItemHandler.getItem(ItemHandler.purgeDelay(itemMap.getDynamicMaterials().get(k - 1)), 1, false, "&fMaterial " + k, "&7", "&7*Click modify this animated material.", 
					"&9&lAnimation Ticks: &a" + Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicMaterials().get(k - 1)))), event -> this.modifyMaterialPane(player, itemMap, k - 1)));
		}
		animateMaterialPane.open(player);
	}
	
	private void selectMaterialPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew) {
		Interface selectMaterialPane = new Interface(true, 6, this.GUIName);
		selectMaterialPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> {
			this.animateMaterialPane(player, itemMap);
		}));
		selectMaterialPane.addButton(new Button(ItemHandler.getItem("STICK", 1, true, "&b&lBukkit Material", "&7", "&7*If you know the name", "&7of the BUKKIT material type", "&7simply click and type it."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "BUKKIT MATERIAL";
			placeHolders[15] = "IRON_SWORD";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (ItemHandler.getMaterial(event.getMessage(), null) != null) {
				if (isNew) {
					this.durationMaterialPane(player, itemMap, position, isNew, event.getMessage().toUpperCase());
				} else {
					List < String > mats = itemMap.getDynamicMaterials();
					mats.set(position, "<delay:" + Utils.returnInteger(ItemHandler.getDelay(mats.get(position))) + ">" + event.getMessage().toUpperCase());
					itemMap.setDynamicMaterials(mats);
					this.modifyMaterialPane(player, itemMap, position);
				}
				String[] placeHolders = Language.newString();
				placeHolders[14] = "BUKKIT MATERIAL";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.noMatch", player, placeHolders);
				this.selectMaterialPane(player, itemMap, position, isNew);
			}
		}));
		Inventory inventoryCheck = ItemJoin.getInstance().getServer().createInventory(null, 9, this.GUIName);
		for (Material material: Material.values()) {
			if (!material.name().contains("LEGACY") && material.name() != "AIR" && this.safeMaterial(ItemHandler.getItem(material.toString(), 1, false, "", ""), inventoryCheck)) {
				
				if (!ServerHandler.hasAquaticUpdate() && Legacy.getDataValue(material) != 0) {
					for (int i = 0; i <= Legacy.getDataValue(material); i++) {
						if (!material.toString().equalsIgnoreCase("STEP") || material.toString().equalsIgnoreCase("STEP") && i != 2) {
							final int dataValue = i;
							selectMaterialPane.addButton(new Button(ItemHandler.getItem(material.toString() + ":" + dataValue, 1, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
								if (isNew) {
									if (dataValue != 0) { this.durationMaterialPane(player, itemMap, position, isNew, material.name() + ":" + dataValue); }
									else { this.durationMaterialPane(player, itemMap, position, isNew, material.name()); }
								} else {
									List < String > mats = itemMap.getDynamicMaterials();
									if (dataValue != 0) { mats.set(position, "<delay:" + Utils.returnInteger(ItemHandler.getDelay(mats.get(position))) + ">" + material.name() + ":" + dataValue); }
									else { mats.set(position, "<delay:" + Utils.returnInteger(ItemHandler.getDelay(mats.get(position))) + ">" + material.name()); }
									itemMap.setDynamicMaterials(mats);
									this.modifyMaterialPane(player, itemMap, position);
								}
							}));
						}
					}
				} else {
					selectMaterialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, false, "", "&7", "&7*Click to set the", "&7material of the item."), event -> {
						if (isNew) {
							this.durationMaterialPane(player, itemMap, position, isNew, material.name());
						} else {
							List < String > mats = itemMap.getDynamicMaterials();
							mats.set(position, "<delay:" + Utils.returnInteger(ItemHandler.getDelay(mats.get(position))) + ">" + material.name());
							itemMap.setDynamicMaterials(mats);
							this.modifyMaterialPane(player, itemMap, position);
						}
					}));
				}
			}
		}
		inventoryCheck.clear();
		selectMaterialPane.open(player);
	}
	
	private void durationMaterialPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
		Interface durationPane = new Interface(true, 6, this.GUIName);
		durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> this.animateMaterialPane(player, itemMap)));
		durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATION DURATION";
			placeHolders[15] = "110";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				String[] placeHolders = Language.newString();
				placeHolders[14] = "ANIMATION DURATION";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				List < String > mats = itemMap.getDynamicMaterials();
				if (isNew) {
					if (itemMap.getDynamicMaterials().isEmpty()) {
						mats.add("<delay:" + Integer.parseInt(event.getMessage()) + ">" + itemMap.getMaterial());
					}
					mats.add("<delay:" + Integer.parseInt(event.getMessage()) + ">" + value);
				} else {
					mats.set(position, "<delay:" + Integer.parseInt(event.getMessage()) + ">" + ItemHandler.purgeDelay(mats.get(position)));
				}
				itemMap.setDynamicMaterials(mats);
				if (isNew) {
					this.animateMaterialPane(player, itemMap);
				} else {
					this.modifyMaterialPane(player, itemMap, position);
				}
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
				this.durationMaterialPane(player, itemMap, position, isNew, value);
			}
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
				List < String > mats = itemMap.getDynamicMaterials();
				if (isNew) {
					if (itemMap.getDynamicMaterials().isEmpty()) {
						mats.add("<delay:" + k + ">" + itemMap.getMaterial());
					}
					mats.add("<delay:" + k + ">" + value);
				} else {
					mats.set(position, "<delay:" + k + ">" + ItemHandler.purgeDelay(mats.get(position)));
				}
				itemMap.setDynamicMaterials(mats);
				if (isNew) {
					this.animateMaterialPane(player, itemMap);
				} else {
					this.modifyMaterialPane(player, itemMap, position);
				}
			}));
		}
		durationPane.open(player);
	}
	
	private void modifyMaterialPane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyMaterialPane = new Interface(false, 2, this.GUIName);
		modifyMaterialPane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyMaterialPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&a&l&nMaterial", "&7", "&7*Change the animated material type.", "&9&lMaterial: &a" + ItemHandler.purgeDelay(itemMap.getDynamicMaterials().get(position))),
				event -> this.selectMaterialPane(player, itemMap, position, false)));
		modifyMaterialPane.addButton(new Button(ItemHandler.getItem("CLOCK", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
		Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicMaterials().get(position)))), event -> this.durationMaterialPane(player, itemMap, position, false, ItemHandler.purgeDelay(itemMap.getDynamicMaterials().get(position)))));
		modifyMaterialPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated material."), event -> {
			List < String > mats = itemMap.getDynamicMaterials();mats.remove(position);
			itemMap.setDynamicMaterials(mats);
			this.animateMaterialPane(player, itemMap);
		}));
		modifyMaterialPane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyMaterialPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> this.animateMaterialPane(player, itemMap)));
		modifyMaterialPane.addButton(new Button(this.fillerPaneBItem), 7);
		modifyMaterialPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated material menu."), event -> this.animateMaterialPane(player, itemMap)));
		modifyMaterialPane.open(player);
	}
	
	private void animatedNamePane(final Player player, final ItemMap itemMap) {
		Interface animatedNamePane = new Interface(true, 2, this.GUIName);
		animatedNamePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
			if (!itemMap.getDynamicNames().isEmpty()) {
				itemMap.setAnimate(true);
				itemMap.setCustomName(ItemHandler.purgeDelay(itemMap.getDynamicNames().get(0)));
			}
			this.animationPane(player, itemMap);
		}));
		animatedNamePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Name Line", "&7", "&7*Add a new name line", "&7to be animated between."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "NAME";
			placeHolders[15] = "&bUltimate Sword";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			this.durationNamePane(player, itemMap, 0, true, event.getMessage());
			String[] placeHolders = Language.newString();
			placeHolders[14] = "NAME";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
		}));
		for (int i = 1; i <= itemMap.getDynamicNames().size(); i++) {
			final int k = i;
			animatedNamePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&fName " + k, "&7", "&7*Click modify this animated name.", "&9&lName: &a" + ItemHandler.purgeDelay(itemMap.getDynamicNames().get(k - 1)), 
					"&9&lAnimation Ticks: &a" + Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicNames().get(k - 1)))), event -> this.modifyNamePane(player, itemMap, k - 1)));
		}
		animatedNamePane.open(player);
	}
	
	private void durationNamePane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
		Interface durationPane = new Interface(true, 6, this.GUIName);
		durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> this.animatedNamePane(player, itemMap)));
		durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATION DURATION";
			placeHolders[15] = "110";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				String[] placeHolders = Language.newString();
				placeHolders[14] = "ANIMATION DURATION";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				List < String > names = itemMap.getDynamicNames();
				if (isNew) {
					if (itemMap.getDynamicNames().isEmpty() && itemMap.getCustomName() != null && !itemMap.getCustomName().isEmpty()) {
						names.add("<delay:" + Integer.parseInt(event.getMessage()) + ">" + itemMap.getCustomName());
					}
					names.add("<delay:" + Integer.parseInt(event.getMessage()) + ">" + value);
				} else {
					names.set(position, "<delay:" + Integer.parseInt(event.getMessage()) + ">" + ItemHandler.purgeDelay(names.get(position)));
				}
				itemMap.setDynamicNames(names);
				if (isNew) {
					this.animatedNamePane(player, itemMap);
				} else {
					this.modifyNamePane(player, itemMap, position);
				}
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
				this.durationNamePane(player, itemMap, position, isNew, value);
			}
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
				List < String > names = itemMap.getDynamicNames();
				if (isNew) {
					if (itemMap.getDynamicNames().isEmpty() && itemMap.getCustomName() != null && !itemMap.getCustomName().isEmpty()) {
						names.add("<delay:" + k + ">" + itemMap.getCustomName());
					}
					names.add("<delay:" + k + ">" + value);
				} else {
					names.set(position, "<delay:" + k + ">" + ItemHandler.purgeDelay(names.get(position)));
				}
				itemMap.setDynamicNames(names);
				if (isNew) {
					this.animatedNamePane(player, itemMap);
				} else {
					this.modifyNamePane(player, itemMap, position);
				}
			}));
		}
		durationPane.open(player);
	}
	
	private void modifyNamePane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyNamePane = new Interface(false, 2, this.GUIName);
		modifyNamePane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyNamePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&a&l&nName", "&7", "&7*Change the animated name line.", "&9&lName: &a" + ItemHandler.purgeDelay(itemMap.getDynamicNames().get(position))), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "NAME";
			placeHolders[15] = "&bUltimate Sword";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			List < String > names = itemMap.getDynamicNames();
			names.set(position, "<delay:" + Utils.returnInteger(ItemHandler.getDelay(names.get(position))) + ">" + event.getMessage());
			itemMap.setDynamicNames(names)
			;String[] placeHolders = Language.newString();
			placeHolders[14] = "NAME";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.modifyNamePane(event.getPlayer(), itemMap, position);
		}));
		modifyNamePane.addButton(new Button(ItemHandler.getItem("CLOCK", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
		Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicNames().get(position)))), event -> this.durationNamePane(player, itemMap, position, false, ItemHandler.purgeDelay(itemMap.getDynamicNames().get(position)))));
		modifyNamePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated name."), event -> {
			List < String > names = itemMap.getDynamicNames();
			names.remove(position);
			itemMap.setDynamicNames(names);
			this.animatedNamePane(player, itemMap);
		}));
		modifyNamePane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyNamePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated name menu."), event -> this.animatedNamePane(player, itemMap)));
		modifyNamePane.addButton(new Button(this.fillerPaneBItem), 7);
		modifyNamePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated name menu."), event -> this.animatedNamePane(player, itemMap)));
		modifyNamePane.open(player);
	}
	
	private void animatedLorePane(final Player player, final ItemMap itemMap) {
		Interface animatedLorePane = new Interface(true, 2, this.GUIName);
		animatedLorePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
			if (!itemMap.getDynamicLores().isEmpty()) {
				itemMap.setAnimate(true);
				itemMap.setCustomLore(ItemHandler.purgeDelay(itemMap.getDynamicLores().get(0)));
			}
			this.animationPane(player, itemMap);
		}));
		animatedLorePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Lore Line", "&7", "&7*Add a new lore line", "&7to be animated between."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATED LORE";
			placeHolders[15] = "&bThis is line 1, &cThis is line 2, &6This is line 3";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATED LORE";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.durationLorePane(event.getPlayer(), itemMap, 0, true, event.getMessage());
		}));
		for (int i = 1; i <= itemMap.getDynamicLores().size(); i++) {
			final int k = i;
			animatedLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&fLore " + k, "&7", "&7*Click modify this animated lore.", "&9&lLore: &a" + ItemHandler.purgeDelay(itemMap.getDynamicLores().get(k - 1)), 
					"&9&lAnimation Ticks: &a" + Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicLores().get(k - 1).get(0)))), event -> this.modifyLorePane(player, itemMap, k - 1)));
		}
		animatedLorePane.open(player);
	}
	
	private void durationLorePane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value) {
		Interface durationPane = new Interface(true, 6, this.GUIName);
		durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> this.animatedLorePane(player, itemMap)));
		durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATION DURATION";
			placeHolders[15] = "110";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				String[] placeHolders = Language.newString();
				placeHolders[14] = "ANIMATION DURATION";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				List < List < String >> lores = itemMap.getDynamicLores();
				if (isNew) {
					if (itemMap.getDynamicLores().isEmpty() && itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
						List < String > loreCut = itemMap.getCustomLore();
						loreCut.set(0, "<delay:" + Integer.parseInt(event.getMessage()) + ">" + loreCut.get(0));
						lores.add(loreCut);
					}
					lores.add(Utils.split("<delay:" + Integer.parseInt(event.getMessage()) + ">" + value));
				} else {
					List < String > loreCut = ItemHandler.purgeDelay(lores.get(position));
					loreCut.set(0, "<delay:" + Integer.parseInt(event.getMessage()) + ">" + loreCut.get(0));
					lores.set(position, loreCut);
				}
				itemMap.setDynamicLores(lores);
				if (isNew) {
					this.animatedLorePane(player, itemMap);
				} else {
					this.modifyLorePane(player, itemMap, position);
				}
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
				this.durationLorePane(player, itemMap, position, isNew, value);
			}
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
				List < List < String >> lores = itemMap.getDynamicLores();
				if (isNew) {
					if (itemMap.getDynamicLores().isEmpty() && itemMap.getCustomLore() != null && !itemMap.getCustomLore().isEmpty()) {
						List < String > loreCut = itemMap.getCustomLore();
						loreCut.set(0, "<delay:" + k + ">" + loreCut.get(0));
						lores.add(loreCut);
					}
					lores.add(Utils.split("<delay:" + k + ">" + value));
				} else {
					List < String > loreCut = ItemHandler.purgeDelay(lores.get(position));
					loreCut.set(0, "<delay:" + k + ">" + loreCut.get(0));
					lores.set(position, loreCut);
				}
				itemMap.setDynamicLores(lores);
				if (isNew) {
					this.animatedLorePane(player, itemMap);
				} else {
					this.modifyLorePane(player, itemMap, position);
				}
			}));
		}
		durationPane.open(player);
	}
	
	private void modifyLorePane(final Player player, final ItemMap itemMap, final int position) {
		Interface modifyLorePane = new Interface(false, 2, this.GUIName);
		modifyLorePane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyLorePane.addButton(new Button(ItemHandler.getItem("WRITABLE_BOOK", 1, false, "&a&l&nLore", "&7", "&7*Change the animated lore line.", "&9&lLore: &a" + ItemHandler.purgeDelay(itemMap.getDynamicLores().get(position))), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATED LORE";
			placeHolders[15] = "&bThis is line 1, &cThis is line 2, &6This is line 3";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			List < List < String >> lores = itemMap.getDynamicLores();
			lores.set(position, Utils.split("<delay:" + Utils.returnInteger(ItemHandler.getDelay(lores.get(position).get(0))) + ">" + event.getMessage()));
			itemMap.setDynamicLores(lores);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATED LORE";Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.modifyLorePane(event.getPlayer(), itemMap, position);
		}));
		modifyLorePane.addButton(new Button(ItemHandler.getItem("CLOCK", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
		Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicLores().get(position).get(0)))), event -> this.durationLorePane(player, itemMap, position, false, "")));
		modifyLorePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated lore."), event -> {
			List < List < String >> lores = itemMap.getDynamicLores();
			lores.remove(position);
			itemMap.setDynamicLores(lores);
			this.animatedLorePane(player, itemMap);
		}));
		modifyLorePane.addButton(new Button(this.fillerPaneGItem), 3);
		modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated lore menu."), event -> this.animatedLorePane(player, itemMap)));
		modifyLorePane.addButton(new Button(this.fillerPaneBItem), 7);
		modifyLorePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated lore menu."), event -> this.animatedLorePane(player, itemMap)));
		modifyLorePane.open(player);
	}
	
	private void animatedSkullPane(final Player player, final ItemMap itemMap, boolean owner) {
		Interface animatedSkullPane = new Interface(true, 2, this.GUIName);
		if (owner) {
			animatedSkullPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
				if (!itemMap.getDynamicOwners().isEmpty()) {
					itemMap.setAnimate(true);
					itemMap.setSkullTexture("");
					itemMap.setDynamicTextures(new ArrayList < String > ());
					itemMap.setSkull(ItemHandler.purgeDelay(itemMap.getDynamicOwners().get(0)));
				}
				this.animationPane(player, itemMap);
			}));
			animatedSkullPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Skull Owner", "&7", "&7*Add a new skull owner", "&7to be animated between."), event -> {
				player.closeInventory();
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL OWNER";
				placeHolders[15] = "RockinChaos";
				Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
				Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
			}, event -> {
				this.durationSkullPane(player, itemMap, 0, true, event.getMessage(), owner);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL OWNER";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			}));
			for (int i = 1; i <= itemMap.getDynamicOwners().size(); i++) {
				final int k = i;
				animatedSkullPane.addButton(new Button(ItemHandler.getItem("GOLDEN_HELMET", 1, false, "&fSkull Owner " + k, "&7", "&7*Click modify this animated skull owner.", "&9&lSkull Owner: &a" + 
				ItemHandler.purgeDelay(itemMap.getDynamicOwners().get(k - 1)), "&9&lAnimation Ticks: &a" + Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicOwners().get(k - 1)))), event -> this.modifySkullPane(player, itemMap, k - 1, owner)));
			}
		} else {
			animatedSkullPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animation menu."), event -> {
				if (!itemMap.getDynamicTextures().isEmpty()) {
					itemMap.setAnimate(true);
					itemMap.setSkull("");
					itemMap.setDynamicOwners(new ArrayList < String > ());
					itemMap.setSkullTexture(ItemHandler.purgeDelay(itemMap.getDynamicTextures().get(0)));
				}
				this.animationPane(player, itemMap);
			}));
			animatedSkullPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&eNew Skull Texture", "&7", "&7*Add a new skull texture", "&7to be animated between."), event -> {
				player.closeInventory();
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL TEXTURE";
				placeHolders[15] = "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=";
				Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
				Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
			}, event -> {
				this.durationSkullPane(player, itemMap, 0, true, event.getMessage(), owner);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL TEXTURE";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			}));
			for (int i = 1; i <= itemMap.getDynamicTextures().size(); i++) {
				final int k = i;
				animatedSkullPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&fSkull Texture " + k, "&7", "&7*Click modify this animated skull texture.", "&9&lSkull Texture: &a" + 
				(ItemHandler.purgeDelay(itemMap.getDynamicTextures().get(k - 1)).length() > 40 ? ItemHandler.purgeDelay(itemMap.getDynamicTextures().get(k - 1)).substring(0, 40) : ItemHandler.purgeDelay(itemMap.getDynamicTextures().get(k - 1))),
				"&9&lAnimation Ticks: &a" + Utils.returnInteger(ItemHandler.getDelay(itemMap.getDynamicTextures().get(k - 1)))), event -> this.modifySkullPane(player, itemMap, k - 1, owner)));
			}
		}
		animatedSkullPane.open(player);
	}
	
	private void durationSkullPane(final Player player, final ItemMap itemMap, final int position, final boolean isNew, final String value, boolean owner) {
		Interface durationPane = new Interface(true, 6, this.GUIName);
		durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated menu."), event -> this.animatedSkullPane(player, itemMap, owner)));
		durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the animation."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "ANIMATION DURATION";
			placeHolders[15] = "110";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				String[] placeHolders = Language.newString();
				placeHolders[14] = "ANIMATION DURATION";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				List < String > skulls = itemMap.getDynamicOwners();
				if (!owner) {
					skulls = itemMap.getDynamicTextures();
				}
				if (isNew) {
					if (itemMap.getDynamicOwners().isEmpty() && owner && itemMap.getSkull() != null && !itemMap.getSkull().isEmpty()) {
						skulls.add("<delay:" + Integer.parseInt(event.getMessage()) + ">" + itemMap.getSkull());
					} else if (itemMap.getDynamicTextures().isEmpty() && !owner && itemMap.getSkullTexture() != null && !itemMap.getSkullTexture().isEmpty()) {
						skulls.add("<delay:" + Integer.parseInt(event.getMessage()) + ">" + itemMap.getSkullTexture());
					}
					skulls.add("<delay:" + Integer.parseInt(event.getMessage()) + ">" + value);
				} else {
					skulls.set(position, "<delay:" + Integer.parseInt(event.getMessage()) + ">" + ItemHandler.purgeDelay(skulls.get(position)));
				}
				if (owner) {
					itemMap.setDynamicOwners(skulls);
				} else {
					itemMap.setDynamicTextures(skulls);
				}
				if (isNew) {
					this.animatedSkullPane(player, itemMap, owner);
				} else {
					this.modifySkullPane(player, itemMap, position, owner);
				}
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
				this.durationSkullPane(player, itemMap, position, isNew, value, owner);
			}
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Ticks(s)", "&7", "&7*Click to set the", "&7duration of the animation."), event -> {
				List < String > skulls = itemMap.getDynamicOwners();
				if (!owner) {
					skulls = itemMap.getDynamicTextures();
				}
				if (isNew) {
					if (itemMap.getDynamicOwners().isEmpty() && owner && itemMap.getSkull() != null && !itemMap.getSkull().isEmpty()) {
						skulls.add("<delay:" + k + ">" + itemMap.getSkull());
					} else if (itemMap.getDynamicTextures().isEmpty() && !owner && itemMap.getSkullTexture() != null && !itemMap.getSkullTexture().isEmpty()) {
						skulls.add("<delay:" + k + ">" + itemMap.getSkullTexture());
					}
					skulls.add("<delay:" + k + ">" + value);
				} else {
					skulls.set(position, "<delay:" + k + ">" + ItemHandler.purgeDelay(skulls.get(position)));
				}
				if (owner) {
					itemMap.setDynamicOwners(skulls);
				} else {
					itemMap.setDynamicTextures(skulls);
				}
				if (isNew) {
					this.animatedSkullPane(player, itemMap, owner);
				} else {
					this.modifySkullPane(player, itemMap, position, owner);
				}
			}));
		}
		durationPane.open(player);
	}
	
	private void modifySkullPane(final Player player, final ItemMap itemMap, final int position, boolean owner) {
		Interface modifySkullPane = new Interface(false, 2, this.GUIName);
		modifySkullPane.addButton(new Button(this.fillerPaneGItem), 3);
		if (owner) {
			modifySkullPane.addButton(new Button(ItemHandler.getItem("GOLDEN_HELMET", 1, false, "&a&l&nSkull Owner", "&7", "&7*Change the animated skull owner.", "&9&lSkull Owner: &a" + 
		ItemHandler.purgeDelay(itemMap.getDynamicOwners().get(position))), event -> {
				player.closeInventory();
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL OWNER";
				placeHolders[15] = "RockinChaos";
				Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
				Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
			}, event -> {
				List < String > skulls = itemMap.getDynamicOwners();
				skulls.set(position, "<delay:" + Utils.returnInteger(ItemHandler.getDelay(skulls.get(position))) + ">" + event.getMessage());
				itemMap.setDynamicOwners(skulls);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL OWNER";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.modifySkullPane(event.getPlayer(), itemMap, position, owner);
			}));
		} else {
			modifySkullPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&a&l&nSkull Texture", "&7", "&7*Change the animated skull texture.", "&9&lSkull Texture: &a" + 
		ItemHandler.purgeDelay(itemMap.getDynamicTextures().get(position))), event -> {
				player.closeInventory();
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL TEXTURE";
				placeHolders[15] = "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=";
				Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
				Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
			}, event -> {
				List < String > skulls = itemMap.getDynamicTextures();
				skulls.set(position, "<delay:" + Utils.returnInteger(ItemHandler.getDelay(skulls.get(position))) + ">" + event.getMessage());
				itemMap.setDynamicTextures(skulls);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL TEXTURE";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.modifySkullPane(event.getPlayer(), itemMap, position, owner);
			}));
		}
		modifySkullPane.addButton(new Button(ItemHandler.getItem("CLOCK", 1, false, "&e&l&nDuration", "&7", "&7*Change the duration of the animation.", "&9&lAnimation Ticks: &a" + 
		Utils.returnInteger(ItemHandler.getDelay((owner ? itemMap.getDynamicOwners().get(position) : itemMap.getDynamicTextures().get(position))))), 
				event -> this.durationSkullPane(player, itemMap, position, false, ItemHandler.purgeDelay((owner ? itemMap.getDynamicOwners().get(position) : itemMap.getDynamicTextures().get(position))), owner)));
		modifySkullPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this animated skull " + (owner ? "owner." : "texture.")), event -> {
			List < String > skulls = itemMap.getDynamicOwners();
			if (!owner) {
				skulls = itemMap.getDynamicTextures();
			}
			skulls.remove(position);
			if (owner) {
				itemMap.setDynamicOwners(skulls);
			} else {
				itemMap.setDynamicTextures(skulls);
			}
			this.animatedSkullPane(player, itemMap, owner);
		}));
		modifySkullPane.addButton(new Button(this.fillerPaneGItem), 3);
		modifySkullPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated skull menu."), event -> this.animatedSkullPane(player, itemMap, owner)));
		modifySkullPane.addButton(new Button(this.fillerPaneBItem), 7);
		modifySkullPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the animated skull menu."), event -> this.animatedSkullPane(player, itemMap, owner)));
		modifySkullPane.open(player);
	}
	
	private void animationPane(final Player player, final ItemMap itemMap) {
		Interface animationPane = new Interface(false, 2, this.GUIName);
		if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
			animationPane.addButton(new Button(this.fillerPaneGItem), 2);
		} else {
			animationPane.addButton(new Button(this.fillerPaneGItem), 3);
		}
		animationPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString(), 1, false, "&c&l&nMaterial", "&7", "&7*Add additional material types", "&7to have the item change between.", "&9&lAnimated Materials: &a" + 
		(Utils.nullCheck(itemMap.getDynamicMaterials() + "") != "NONE" ? "YES" : "NONE")), event -> this.animateMaterialPane(player, itemMap)));
		animationPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&a&l&nName", "&7", "&7*Add additional custom names", "&7to have the item change between.", "&9&lAnimated Names: &a" + 
		(Utils.nullCheck(itemMap.getDynamicNames() + "") != "NONE" ? "YES" : "NONE")), event -> this.animatedNamePane(player, itemMap)));
		animationPane.addButton(new Button(ItemHandler.getItem("386", 1, false, "&b&l&nLore", "&7", "&7*Add additional custom lores", "&7to have the item change between.", "&9&lAnimated Lores: &a" + 
		(Utils.nullCheck(itemMap.getDynamicLores() + "") != "NONE" ? "YES" : "NONE")), event -> this.animatedLorePane(player, itemMap)));
		if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
			animationPane.addButton(new Button(ItemHandler.getItem("GOLDEN_HELMET", 1, false, "&a&lSkull Owner", "&7", "&7*Add additional skull owners", "&7to have the item change between.", "&7", "&7You can only define skull owner", 
					"&7or skull texture, this will", "&7remove any skull textures.", "&9&lAnimated Owners: &a" + (Utils.nullCheck(itemMap.getDynamicOwners() + "") != "NONE" ? "YES" : "NONE")), event -> this.animatedSkullPane(player, itemMap, true)));
			animationPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&b&lSkull Texture", "&7", "&7*Add additional skull textures", "&7to have the item change between.", "&7", "&7You can only define skull texture", 
					"&7or skull owner, this will", "&7remove any skull owners.", "&7", "&7Skull textures can be found", "&7at websites like &aminecraft-heads.com", "&7and the value is listed under", "&7the OTHER section.", 
					"&9&lAnimated Textures: &a" + (Utils.nullCheck(itemMap.getDynamicTextures() + "") != "NONE" ? "YES" : "NONE")), event -> this.animatedSkullPane(player, itemMap, false)));
			animationPane.addButton(new Button(this.fillerPaneGItem), 2);
		} else {
			animationPane.addButton(new Button(this.fillerPaneGItem), 3);
		}
		animationPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		animationPane.addButton(new Button(this.fillerPaneBItem), 7);
		animationPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		animationPane.open(player);
	}

//  ===========================================================================================================================================================================================================================================================
	
	private void limitPane(final Player player, final ItemMap itemMap) {
		Interface limitPane = new Interface(false, 2, this.GUIName);
		List < String > limitModes = new ArrayList < String > ();
		if (Utils.containsIgnoreCase(itemMap.getLimitModes(), "ADVENTURE")) {
			limitModes.add("ADVENTURE");
		}
		if (Utils.containsIgnoreCase(itemMap.getLimitModes(), "SURVIVAL")) {
			limitModes.add("SURVIVAL");
		}
		if (Utils.containsIgnoreCase(itemMap.getLimitModes(), "CREATIVE")) {
			limitModes.add("CREATIVE");
		}
		limitPane.addButton(new Button(this.fillerPaneGItem), 3);
		limitPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "FILLED_MAP" : "MAP"), 1, limitModes.contains("ADVENTURE"), "&a&l&nADVENTURE", "&7", "&7*Limits the item to ADVENTURE mode.", "&9&lENABLED: &a" + 
		(limitModes.contains("ADVENTURE") + "").toUpperCase()), event -> {
			if (limitModes.contains("ADVENTURE")) {
				limitModes.remove("ADVENTURE");
			} else {
				limitModes.add("ADVENTURE");
			}
			itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));this.limitPane(player, itemMap);
		}));
		limitPane.addButton(new Button(ItemHandler.getItem("STONE_SWORD", 1, limitModes.contains("SURVIVAL"), "&b&l&nSURVIVAL", "&7", "&7*Limits the item to SURVIVAL mode.", "&9&lENABLED: &a" + (limitModes.contains("SURVIVAL") + "").toUpperCase()), 
				event -> {
			if (limitModes.contains("SURVIVAL")) {
				limitModes.remove("SURVIVAL");
			} else {
				limitModes.add("SURVIVAL");
			}
			itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));this.limitPane(player, itemMap);
		}));
		limitPane.addButton(new Button(ItemHandler.getItem(limitModes.contains("CREATIVE") ? "ENCHANTED_GOLDEN_APPLE" : "GOLDEN_APPLE", 1, false, "&6&l&nCREATIVE", "&7", "&7*Limits the item to CREATIVE mode.", "&9&lENABLED: &a" + 
		(limitModes.contains("CREATIVE") + "").toUpperCase()), event -> {
			if (limitModes.contains("CREATIVE")) {
				limitModes.remove("CREATIVE");
			} else {
				limitModes.add("CREATIVE");
			}
			itemMap.setLimitModes(limitModes.toString().substring(0, limitModes.toString().length() - 1).substring(1));this.limitPane(player, itemMap);
		}));
		limitPane.addButton(new Button(this.fillerPaneGItem), 3);
		limitPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		limitPane.addButton(new Button(this.fillerPaneBItem), 7);
		limitPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		limitPane.open(player);
	}
	
	private void probabilityPane(final Player player, final ItemMap itemMap) {
		Interface probabilityPane = new Interface(true, 6, this.GUIName);
		probabilityPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
			this.creatingPane(player, itemMap);
		}));
		for (int i = 1; i <= 100; i++) {
			final int k = i;
			probabilityPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", 1, false, "&9&lChance: &a&l" + k + "%", "&7", "&7*Click to set the", "&7probability of the item."), event -> {
				itemMap.setProbability(k);this.creatingPane(player, itemMap);
			}));
		}
		probabilityPane.open(player);
	}
	
	private void usePane(final Player player, final ItemMap itemMap) {
		Interface usePane = new Interface(true, 6, this.GUIName);
		usePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> {
			this.creatingPane(player, itemMap);
		}));
		usePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Usage", "&7", "&7*Click to set a custom usage cooldown", "&7value for the item."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "USAGE COOLDOWN";
			placeHolders[15] = "120";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				itemMap.setInteractCooldown(Integer.parseInt(event.getMessage()));
				String[] placeHolders = Language.newString();
				placeHolders[14] = "USAGE COOLDOWN";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.creatingPane(event.getPlayer(), itemMap);
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			usePane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7use-cooldown of the item."), event -> {
				itemMap.setInteractCooldown(k);
				this.creatingPane(player, itemMap);
			}));
		}
		usePane.open(player);
	}
	
	private void bannerPane(final Player player, final ItemMap itemMap) {
		Interface bannerPane = new Interface(true, 6, this.GUIName);
		bannerPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		for (PatternType pattern: PatternType.values()) {
			String patternString = "NONE";
			if (Utils.nullCheck(itemMap.getBannerPatterns().toString()) != "NONE") {
				for (Pattern patterns: itemMap.getBannerPatterns()) {
					if (patterns.getPattern() == pattern) {
						patternString = patterns.getColor() + ":" + patterns.getPattern().name().toUpperCase();
						break;
					}
				}
			}
			final String checkPattern = patternString;
			bannerPane.addButton(new Button(ItemHandler.getItem("PAPER", 1, (checkPattern != "NONE" ? true : false), "&f" + pattern.name(), "&7", "&7*Click to add this as", "&7a banner pattern.", (checkPattern != "NONE" ? 
					"&9&lInformation: &a" + checkPattern : "")), event -> {
				if (checkPattern != "NONE") {
					List < Pattern > patternList = itemMap.getBannerPatterns();
					if (Utils.nullCheck(itemMap.getBannerPatterns().toString()) != "NONE") {
						for (Pattern patterns: patternList) {
							if (patterns.getPattern() == pattern) {
								patternList.remove(patterns);
								itemMap.setBannerPatterns(patternList);
								break;
							}
						}
					}
					this.bannerPane(player, itemMap);
				} else {
					this.patternPane(player, itemMap, pattern);
				}
			}));
		}
		bannerPane.open(player);
	}
	
	private void patternPane(final Player player, final ItemMap itemMap, final PatternType pattern) {
		Interface colorPane = new Interface(true, 6, this.GUIName);
		colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the banner patterns menu."), event -> this.bannerPane(player, itemMap)));
		for (DyeColor color: DyeColor.values()) {
			colorPane.addButton(new Button(ItemHandler.getItem("GRAY_DYE", 1, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your banner pattern."), event -> {
				List < Pattern > patterns = itemMap.getBannerPatterns();patterns.add(new Pattern(color, pattern));itemMap.setBannerPatterns(patterns);this.bannerPane(player, itemMap);
			}));
		}
		colorPane.open(player);
	}
	
	private void potionPane(final Player player, final ItemMap itemMap) {
		Interface potionPane = new Interface(true, 6, this.GUIName);
		potionPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> this.creatingPane(player, itemMap)));
		for (PotionEffectType potion: PotionEffectType.values()) {
			if (potion != null) {
				String potionString = "NONE";
				if (Utils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
					for (PotionEffect potions: itemMap.getPotionEffect()) {
						if (potions.getType() == potion) {
							potionString = potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + potions.getDuration() / 160;
							break;
						}
					}
				}
				final String checkPotion = potionString;
				potionPane.addButton(new Button(ItemHandler.getItem("GLASS_BOTTLE", 1, (checkPotion != "NONE" ? true : false), "&f" + potion.getName(), "&7", "&7*Add this potion effect", "&7to the item.", 
						(checkPotion != "NONE" ? "&9&lInformation: &a" + checkPotion : "")), event -> {
					if (checkPotion != "NONE") {
						List < PotionEffect > potionEffects = itemMap.getPotionEffect();
						if (Utils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
							for (PotionEffect potions: potionEffects) {
								if (potions.getType() == potion) {
									potionEffects.remove(potions);
									itemMap.setPotionEffect(potionEffects);
									break;
								}
							}
						}
						this.potionPane(player, itemMap);
					} else {
						this.levelPane(player, itemMap, potion);
					}
				}));
			}
		}
		potionPane.open(player);
	}
	
	private void levelPane(final Player player, final ItemMap itemMap, final PotionEffectType potion) {
		Interface levelPane = new Interface(true, 6, this.GUIName);
		levelPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the potion effect menu."), event -> this.potionPane(player, itemMap)));
		levelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Level", "&7", "&7*Click to set a custom level (strength)", "&7value for the potion effect."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "EFFECT LEVEL";
			placeHolders[15] = "16";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				String[] placeHolders = Language.newString();
				placeHolders[14] = "EFFECT LEVEL";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.durationPane(event.getPlayer(), itemMap, potion, Integer.parseInt(event.getMessage()));
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
				this.levelPane(event.getPlayer(), itemMap, potion);
			}
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			levelPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:6", k, false, "&d&lLevel: &a&l" + k + "", "&7", "&7*Click to set the", "&7level (strength) of the potion effect."), event -> {
				itemMap.setInteractCooldown(k);this.durationPane(player, itemMap, potion, k);
			}));
		}
		levelPane.open(player);
	}
	
	private void durationPane(final Player player, final ItemMap itemMap, final PotionEffectType potion, int level) {
		Interface durationPane = new Interface(true, 6, this.GUIName);
		durationPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the potion effect menu."), event -> this.potionPane(player, itemMap)));
		durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Duration", "&7", "&7*Click to set a custom duration", "&7value for the potion effect."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "EFFECT DURATION";
			placeHolders[15] = "110";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				String[] placeHolders = Language.newString();
				placeHolders[14] = "EFFECT DURATION";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.potionPane(event.getPlayer(), itemMap);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
				this.durationPane(event.getPlayer(), itemMap, potion, level);
			}
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			durationPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lDuration: &a&l" + k + " Second(s)", "&7", "&7*Click to set the", "&7duration of the potion effect."), event -> {
				List < PotionEffect > effects = itemMap.getPotionEffect();
				effects.add(new PotionEffect(potion, k * 160, level));
				itemMap.setPotionEffect(effects);this.potionPane(player, itemMap);
			}));
		}
		durationPane.open(player);
	}
	
	private void powerPane(final Player player, final ItemMap itemMap) {
		Interface powerPane = new Interface(true, 6, this.GUIName);
		powerPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> this.otherPane(player, itemMap)));
		powerPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:4", 1, false, "&e&lCustom Power", "&7", "&7*Click to set a custom power", "&7value for the firework."), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "FIREWORK POWER";
			placeHolders[15] = "96";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			if (Utils.isInt(event.getMessage())) {
				itemMap.setFireworkPower(Integer.parseInt(event.getMessage()));
				String[] placeHolders = Language.newString();
				placeHolders[14] = "FIREWORK POWER";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			} else {
				String[] placeHolders = Language.newString();
				placeHolders[16] = event.getMessage();
				Language.sendLangMessage("Commands.UI.notInteger", player, placeHolders);
			}
			this.otherPane(player, itemMap);
		}));
		for (int i = 1; i <= 64; i++) {
			final int k = i;
			powerPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", k, false, "&9&lPower Level: &a&l" + k, "&7", "&7*Click to set the", "&7power level of the firework."), event -> {
				itemMap.setFireworkPower(k);
				this.otherPane(player, itemMap);
			}));
		}
		powerPane.open(player);
	}
	
	private void colorPane(final Player player, final ItemMap itemMap) {
		Interface colorPane = new Interface(true, 6, this.GUIName);
		colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> this.otherPane(player, itemMap)));
		for (DyeColor color: DyeColor.values()) {
			colorPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "GRAY_DYE" : "351:8"), 1, Utils.containsValue(itemMap.getFireworkColor(), color.name()), "&f" + color.name(), 
					"&7", "&7*This will be the color", "&7of your firework charge.", "&9&lENABLED: &a" + (Utils.containsValue(itemMap.getFireworkColor(), color.name()) + "").toUpperCase()), event -> {
				List < DyeColor > colors = itemMap.getFireworkColor();
				if (Utils.containsIgnoreCase(itemMap.getFireworkColor().toString(), color.name())) {
					colors.remove(color);
				} else {
					colors.add(color);
					itemMap.setFireworkColor(colors);
				}
				this.colorPane(player, itemMap);
			}));
		}
		colorPane.open(player);
	}
	
	private void designPane(final Player player, final ItemMap itemMap) {
		Interface designPane = new Interface(true, 2, this.GUIName);
		designPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> this.otherPane(player, itemMap)));
		for (Type type: Type.values()) {
			designPane.addButton(new Button(ItemHandler.getItem("EGG", 1, false, "&f" + type.name(), "&7", "&7*This will be the type (pattern)", "&7of your firework."), event -> {
				itemMap.setFireworkType(type);
				this.otherPane(player, itemMap);
			}));
		}
		designPane.open(player);
	}
	
//  ============================================== //
//                Book Pages Menus      	       //
//  ============================================== //
	
	private void pagePane(final Player player, final ItemMap itemMap) {
		Interface pagePane = new Interface(true, 2, this.GUIName);
		pagePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> {
			this.otherPane(player, itemMap);
		}));
		pagePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&l&nNew Page", "&7", "&7*Add a new page to the book."), event -> this.linePane(player, itemMap, true, itemMap.getListPages().size())));
		for (int i = 1; i <= itemMap.getListPages().size(); i++) {
			final int k = i;
			pagePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&b&lPage " + i, "&7", "&7*Click to modify the contents", "&7of this book page."), event -> this.linePane(player, itemMap, false, k - 1)));
		}
		pagePane.open(player);
	}
	
	private void linePane(final Player player, final ItemMap itemMap, final boolean isNew, final int page) {
		Interface linePane = new Interface(true, 2, this.GUIName);
		List < List < String > > pages = itemMap.getListPages();
		linePane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the book pages menu."), event -> this.pagePane(player, itemMap)));
		if (isNew) {
			linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&l&nNew Line", "&7", "&7*Add a new line to the book page.", "&7", "&9&lPage: &a" + (page + 1)), event -> {
				player.closeInventory();
				String[] placeHolders = Language.newString();
				placeHolders[14] = "PAGE LINE";
				placeHolders[15] = "&eWelcome to the Server!";
				Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
				Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
			}, event -> {
				List < String > newPage = new ArrayList < String > ();
				newPage.add(event.getMessage());pages.add(newPage);
				itemMap.setListPages(pages);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "PAGE LINE";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.linePane(event.getPlayer(), itemMap, false, page);
			}));
		} else {
			List < String > selectPage = pages.get(page);
			linePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this page from the book.", "&7", "&9&lPage: &a" + (page + 1)), event -> {
				pages.remove(page);
				itemMap.setListPages(pages);
				this.pagePane(player, itemMap);
			}));
			if (selectPage.size() < 14) {
				linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, true, "&e&l&nNew Line", "&7", "&7*Add a new line to the book page.", "&7", "&9&lLine: &a" + (selectPage.size() + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
					player.closeInventory();
					String[] placeHolders = Language.newString();
					placeHolders[14] = "PAGE LINE";
					placeHolders[15] = "&eWelcome to the Server!";
					Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
					Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
				}, event -> {
					selectPage.add(event.getMessage());
					pages.set(page, selectPage);
					itemMap.setListPages(pages);
					String[] placeHolders = Language.newString();
					placeHolders[14] = "PAGE LINE";
					Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
					this.linePane(event.getPlayer(), itemMap, false, page);
				}));
			}
			for (int i = 1; i <= selectPage.size(); i++) {
				final int k = i;
				linePane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&f" + selectPage.get(k - 1), "&7", "&7*Click to modify or delete", "&7this line in the book page.", "&7", "&9&lLine: &a" + k + 
						"    &9&lPage: &a" + (page + 1)), event -> this.modifyPagesPane(player, itemMap, k - 1, page)));
			}
		}
		linePane.open(player);
	}
	
	private void modifyPagesPane(final Player player, final ItemMap itemMap, final int line, final int page) {
		Interface linePane = new Interface(false, 2, this.GUIName);
		List < List < String > > pages = itemMap.getListPages();
		List < String > selectPage = pages.get(page);
		linePane.addButton(new Button(this.fillerPaneGItem), 3);
		linePane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, false, "&c&l&nModify", "&7", "&7*Modify this line in the page.", "&7", "&9&lLine: &a" + (line + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
			player.closeInventory();
			String[] placeHolders = Language.newString();
			placeHolders[14] = "PAGE LINE";
			placeHolders[15] = "&eWelcome to the Server!";
			Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
			Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
		}, event -> {
			selectPage.set(line, event.getMessage());
			pages.set(page, selectPage);
			itemMap.setListPages(pages);
			String[] placeHolders = Language.newString();
			placeHolders[14] = "PAGE LINE";
			Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
			this.linePane(event.getPlayer(), itemMap, false, page);
		}));
		linePane.addButton(new Button(this.fillerPaneGItem));
		linePane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, false, "&c&l&nDelete", "&7", "&7*Delete this line in the page.", "&7", "&9&lLine: &a" + (line + 1) + "    &9&lPage: &a" + (page + 1)), event -> {
			selectPage.remove(selectPage.get(line));
			pages.set(page, selectPage);
			itemMap.setListPages(pages);
			this.linePane(player, itemMap, false, page);
		}));
		linePane.addButton(new Button(this.fillerPaneGItem), 3);
		linePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the book lines menu."), event -> this.linePane(player, itemMap, false, page)));
		linePane.addButton(new Button(this.fillerPaneBItem), 7);
		linePane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the book lines menu."), event -> this.linePane(player, itemMap, false, page)));
		linePane.open(player);
	}
	
//  ==========================================================================================================================================================================================================================================================
	
	private void otherPane(final Player player, final ItemMap itemMap) {
		Interface otherPane = new Interface(false, 3, this.GUIName);
		otherPane.addButton(new Button(this.fillerPaneGItem), 4);
		otherPane.addButton(new Button(this.headerStack(player, itemMap)));
		otherPane.addButton(new Button(this.fillerPaneGItem), 4);
		if (itemMap.getMaterial().toString().contains("WRITTEN_BOOK")) {
			otherPane.addButton(new Button(this.fillerPaneGItem), 3);
			otherPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WRITABLE_BOOK" : "386"), 1, false, "&e&lPages", "&7", "&7*Define custom pages for the book.", 
					"&9&lPages: &a" + (Utils.nullCheck(itemMap.getPages() + "") != "NONE" ? "YES" : "NONE")), event -> this.pagePane(player, itemMap)));
			otherPane.addButton(new Button(this.fillerPaneGItem));
			otherPane.addButton(new Button(ItemHandler.getItem("FEATHER", 1, false, "&a&lAuthor", "&7", "&7*Define the author of the book.", "&9&lAuthor: &a" + Utils.nullCheck(itemMap.getAuthor())), event -> {
				if (Utils.nullCheck(itemMap.getAuthor()) != "NONE") {
					itemMap.setAuthor(null);
					this.otherPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = Language.newString();
					placeHolders[14] = "AUTHOR";
					placeHolders[15] = "RockinChaos";
					Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
					Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
				}
			}, event -> {
				itemMap.setAuthor(event.getMessage());
				String[] placeHolders = Language.newString();
				placeHolders[14] = "AUTHOR";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.otherPane(event.getPlayer(), itemMap);
			}));
			otherPane.addButton(new Button(this.fillerPaneGItem), 3);
		} else if (itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM")) {
			otherPane.addButton(new Button(this.fillerPaneGItem), 3);
			otherPane.addButton(new Button(ItemHandler.getItem("GOLDEN_HELMET", 1, false, "&b&lSkull Owner", "&7", "&7*Define a skull owner for the", "&7head adding that persons skin.", "&7", "&7You can only define skull owner", 
					"&7or skull texture, this will", "&7remove any skull textures.", "&9&lSkull-Owner: &a" + Utils.nullCheck(itemMap.getSkull())), event -> {
				if (itemMap.getDynamicOwners() != null && !itemMap.getDynamicOwners().isEmpty()) {
					this.animatedSkullPane(player, itemMap, true);
				} else {
					if (Utils.nullCheck(itemMap.getSkull()) != "NONE") {
						itemMap.setSkull(null);
						this.otherPane(player, itemMap);
					} else {
						player.closeInventory();
						String[] placeHolders = Language.newString();
						placeHolders[14] = "SKULL OWNER";
						placeHolders[15] = "RockinChaos";
						Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
						Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
					}
				}
			}, event -> {
				itemMap.setSkull(event.getMessage());
				itemMap.setSkullTexture(null);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL OWNER";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.otherPane(event.getPlayer(), itemMap);
			}));
			otherPane.addButton(new Button(this.fillerPaneGItem));
			otherPane.addButton(new Button(ItemHandler.getItem("STRING", 1, false, "&a&lSkull Texture", "&7", "&7*Add a skull texture for the", "&7head as a custom skin.", "&7", "&7You can only define skull texture", 
					"&7or skull owner, this will", "&7remove any skull owners.", "&7", "&7Skull textures can be found", "&7at websites like &aminecraft-heads.com", "&7and the value is listed under", "&7the OTHER section.", "&9&lSkull-Texture: &a" + 
			(Utils.nullCheck(itemMap.getSkullTexture()) != "NONE" ? (itemMap.getSkullTexture().length() > 40 ? itemMap.getSkullTexture().substring(0, 40) : itemMap.getSkullTexture()) : "")), event -> {
				if (itemMap.getDynamicTextures() != null && !itemMap.getDynamicTextures().isEmpty()) {
					this.animatedSkullPane(player, itemMap, false);
					} else {
					if (Utils.nullCheck(itemMap.getSkullTexture()) != "NONE") {
						itemMap.setSkullTexture(null);
						this.otherPane(player, itemMap);
					} else {
						player.closeInventory();
						String[] placeHolders = Language.newString();
						placeHolders[14] = "SKULL TEXTURE";
						placeHolders[15] = "eyJ0ZXh0dYMGQVlN2FjZmU3OSJ9fX0=";
						Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
						Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
					}
				}
			}, event -> {
				itemMap.setSkullTexture(event.getMessage());
				itemMap.setSkull(null);
				String[] placeHolders = Language.newString();
				placeHolders[14] = "SKULL TEXTURE";
				Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
				this.otherPane(event.getPlayer(), itemMap);
			}));
			otherPane.addButton(new Button(this.fillerPaneGItem), 3);
		} else if (itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
			String colorList = "";
			if (Utils.nullCheck(itemMap.getFireworkColor().toString()) != "NONE") {
				for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getFireworkColor().toString()))) {
					colorList += "&a" + split + " /n ";
				}
			}
			otherPane.addButton(new Button(this.fillerPaneGItem), 2);
			otherPane.addButton(new Button(ItemHandler.getItem("EGG", 1, false, "&a&lType", "&7", "&7*Set the style of the explosion.", "&9&lType: &a" + Utils.nullCheck(itemMap.getFireworkType() + "")), event -> {
				if (Utils.nullCheck(itemMap.getFireworkType() + "") != "NONE") {
					itemMap.setFireworkType(null);
					this.otherPane(player, itemMap);
				} else {
					this.designPane(player, itemMap);
				}
			}));
			otherPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, itemMap.getFireworkFlicker(), "&a&lFlicker", "&7", "&7*Show the flicker effect as", "&7the firework particles dissipate", "&7after the explosion.", 
					"&9&lENABLED: &a" + itemMap.getFireworkFlicker()), event -> {
				if (itemMap.getFireworkFlicker()) {
					itemMap.setFireworkFlicker(false);
				} else {
					itemMap.setFireworkFlicker(true);
				}
				this.otherPane(player, itemMap);
			}));
			otherPane.addButton(new Button(ItemHandler.getItem("EMERALD", 1, itemMap.getFireworkTrail(), "&a&lTrail", "&7", "&7*Show the trail (smoke) of", "&7the firework when launched.", "&9&lENABLED: &a" + itemMap.getFireworkTrail()), event -> {
				if (itemMap.getFireworkTrail()) {
					itemMap.setFireworkTrail(false);
				} else {
					itemMap.setFireworkTrail(true);
				}
				this.otherPane(player, itemMap);
			}));
			otherPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, false, "&a&lPower", "&7", "&7*Set the power (distance)", "&7that the firework travels.", "&9&lPower: &a" + Utils.nullCheck(itemMap.getFireworkPower() + "&7")), event -> {
				if (Utils.nullCheck(itemMap.getFireworkPower() + "&7") != "NONE") {
					itemMap.setFireworkPower(0);
					this.otherPane(player, itemMap);
				} else {
					this.powerPane(player, itemMap);
				}
			}));
			otherPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "LIME_DYE" : "351:10"), 1, false, "&a&lColor(s)", "&7", "&7*Define the individual colors of the", "&7firework effect type.", 
					"&9&lColor(s): &a" + (Utils.nullCheck(colorList) != "NONE" ? colorList : "NONE")), event -> this.colorPane(player, itemMap)));
			otherPane.addButton(new Button(this.fillerPaneGItem), 2);
		} else if (itemMap.getMaterial().toString().contains("LEATHER_")) {
			Interface colorPane = new Interface(true, 6, this.GUIName);
			colorPane.setReturnButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the special settings menu."), event -> this.otherPane(player, itemMap)));
			for (DyeColor color: DyeColor.values()) {
				colorPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "GRAY_DYE" : "351:8"), 1, false, "&f" + color.name(), "&7", "&7*This will be the color", "&7of your leather armor."), event -> {
					itemMap.setLeatherColor(color.name());itemMap.setLeatherHex(null);this.otherPane(player, itemMap);
				}));
			}
			otherPane.addButton(new Button(this.fillerPaneGItem), 3);
			otherPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "YELLOW_DYE" : "351:11"), 1, false, "&a&lDye", "&7", "&7*Add a custom color to", "&7your leather armor.", "&9&lLeather-Color: &a" +
			(Utils.nullCheck(itemMap.getLeatherColor()) != "NONE" ? Utils.nullCheck(itemMap.getLeatherColor()) : Utils.nullCheck(itemMap.getLeatherHex()))), event -> {
				if (itemMap.getLeatherColor() != null) {
					itemMap.setLeatherColor(null);
					this.otherPane(player, itemMap);
				} else {
					colorPane.open(player);
				}
			}));
			otherPane.addButton(new Button(this.fillerPaneGItem));
			otherPane.addButton(new Button(ItemHandler.getItem((ServerHandler.hasAquaticUpdate() ? "WRITABLE_BOOK" : "386"), 1, false, "&a&lHex Color", "&7", "&7*Add a custom hex color", "&7to your leather armor.", "&9&lLeather-Color: &a" + 
			(Utils.nullCheck(itemMap.getLeatherHex()) != "NONE" ? Utils.nullCheck(itemMap.getLeatherHex()) : Utils.nullCheck(itemMap.getLeatherColor()))), event -> {
				if (itemMap.getLeatherHex() != null) {
					itemMap.setLeatherHex(null);
					this.otherPane(player, itemMap);
				} else {
					player.closeInventory();
					String[] placeHolders = Language.newString();
					placeHolders[14] = "HEX COLOR";
					placeHolders[15] = "#033dfc";
					Language.sendLangMessage("Commands.UI.inputType", player, placeHolders);
					Language.sendLangMessage("Commands.UI.normalExample", player, placeHolders);
				}
			}, event -> {
				if (itemMap.getLeatherHex() == null) {
					itemMap.setLeatherHex(event.getMessage());
					itemMap.setLeatherColor(null);
					String[] placeHolders = Language.newString();
					placeHolders[14] = "HEX COLOR";
					Language.sendLangMessage("Commands.UI.inputSet", player, placeHolders);
					this.otherPane(event.getPlayer(), itemMap);
				}
			}));
			otherPane.addButton(new Button(this.fillerPaneGItem), 3);
		}
		otherPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		otherPane.addButton(new Button(this.fillerPaneBItem), 7);
		otherPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, false, "&c&l&nReturn", "&7", "&7*Returns you to the item definition menu."), event -> this.creatingPane(player, itemMap)));
		otherPane.open(player);
	}
	
	private ItemStack headerStack(final Player player, final ItemMap itemMap) {
		String slotList = "";
		String slotString = "";
		if (Utils.nullCheck(itemMap.getMultipleSlots().toString()) != "NONE") {
			for (String slot: itemMap.getMultipleSlots()) {
				slotString += slot + ", ";
			}
			for (String split: Utils.softSplit(Utils.nullCheck(slotString.substring(0, slotString.length() - 2)))) {
				slotList += "&a" + split + " /n ";
			}
		}
		String itemflagsList = "";
		if (Utils.nullCheck(itemMap.getItemFlags()) != "NONE") {
			for (String split: Utils.softSplit(itemMap.getItemFlags())) {
				itemflagsList += "&a" + split + " /n ";
			}
		}
		String triggersList = "";
		if (Utils.nullCheck(itemMap.getTriggers()) != "NONE") {
			for (String split: Utils.softSplit(itemMap.getTriggers())) {
				triggersList += "&a" + split + " /n ";
			}
		}
		String worldList = "";
		if (Utils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE") {
			for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getEnabledWorlds().toString()))) {
				worldList += "&a" + split + " /n ";
			}
		}
		String regionList = "";
		if (Utils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE") {
			for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getEnabledRegions().toString()))) {
				regionList += "&a" + split + " /n ";
			}
		}
		String enchantList = "";
		if (Utils.nullCheck(itemMap.getEnchantments().toString()) != "NONE") {
			for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getEnchantments().toString()))) {
				enchantList += "&a" + split + " /n ";
			}
		}
		String potionList = "";
		String potionString = "";
		if (Utils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
			for (PotionEffect potions: itemMap.getPotionEffect()) {
				potionString += potions.getType().getName().toUpperCase() + ":" + potions.getAmplifier() + ":" + potions.getDuration() / 160 + ", ";
			}
			for (String split: Utils.softSplit(Utils.nullCheck(potionString.substring(0, potionString.length() - 2)))) {
				potionList += "&a" + split + " /n ";
			}
		}
		String patternList = "";
		String patternString = "";
		if (Utils.nullCheck(itemMap.getPotionEffect().toString()) != "NONE") {
			for (Pattern patterns: itemMap.getBannerPatterns()) {
				patternString += patterns.getColor() + ":" + patterns.getPattern().name().toUpperCase() + ", ";
			}
			for (String split: Utils.softSplit(Utils.nullCheck(patternString.substring(0, patternString.length() - 2)))) {
				patternList += "&a" + split + " /n ";
			}
		}
		String colorList = "";
		if (Utils.nullCheck(itemMap.getFireworkColor().toString()) != "NONE") {
			for (String split: Utils.softSplit(Utils.nullCheck(itemMap.getFireworkColor().toString()))) {
				colorList += "&a" + split + " /n ";
			}
		}
		
		ItemStack item = ItemHandler.getItem(itemMap.getMaterial().toString() + ":" + itemMap.getDataValue(), 1, false, "&7*&6&l&nItem Information", "&7", "&9&lNode: &a" + itemMap.getConfigName(), "&9&lMaterial: &a" 
		+ itemMap.getMaterial().toString() + (itemMap.getDataValue() != 0 ? ":" + itemMap.getDataValue() : ""), 
				(itemMap.getMultipleSlots() != null && !itemMap.getMultipleSlots().isEmpty() ? "&9&lSlot(s): &a" + slotList : "&9&lSlot: &a" + itemMap.getSlot().toUpperCase()), "&9&lCount: &a" + itemMap.getCount(), 
				(Utils.nullCheck(itemMap.getCustomName()) != "NONE" ? "&9&lName: &a" + itemMap.getCustomName() : ""), (Utils.nullCheck(itemMap.getCustomLore().toString()) != "NONE" ? "&9&lLore: &a" + (Utils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",").length() > 40 ? Utils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",").substring(0, 40) : Utils.nullCheck(itemMap.getCustomLore().toString()).replace(",,", ",").replace(", ,", ",")) : ""), 
				(Utils.nullCheck(itemMap.getDurability() + "&7") != "NONE" ? "&9&lDurability: &a" + itemMap.getDurability() : ""), (itemMap.getCommands().length != 0 ? "&9&lCommands: &aYES" : ""), 
				(Utils.nullCheck(itemMap.getCommandCost() + "&7") != "NONE" ? "&9&lCommands-Cost: &a" + itemMap.getCommandCost() : ""), (Utils.nullCheck(itemMap.getCommandType() + "") != "NONE" ? "&9&lCommands-Type: &a" + itemMap.getCommandType() : ""), 
				(Utils.nullCheck(itemMap.getCommandSequence() + "") != "NONE" ? "&9&lCommands-Sequence: &a" + itemMap.getCommandSequence() : ""), 
				(Utils.nullCheck(itemMap.getCommandCooldown() + "&7") != "NONE" ? "&9&lCommands-Cooldown: &a" + itemMap.getCommandCooldown() + " second(s)" : ""), 
				(Utils.nullCheck(itemMap.getCooldownMessage()) != "NONE" ? "&9&lCooldown-Message: &a" + itemMap.getCooldownMessage() : ""), (Utils.nullCheck(itemMap.getCommandSound() + "") != "NONE" ? "&9&lCommands-Sound: &a" + itemMap.getCommandSound() : ""), 
				(Utils.nullCheck(itemMap.getCommandParticle() + "") != "NONE" ? "&9&lCommands-Particle: &a" + itemMap.getCommandParticle() : ""), (Utils.nullCheck(itemMap.getEnchantments().toString()) != "NONE" ? "&9&lEnchantments: &a" + enchantList : ""), 
				(Utils.nullCheck(itemMap.getItemFlags()) != "NONE" ? "&9&lItemflags: &a" + itemflagsList : ""), (Utils.nullCheck(itemMap.getTriggers()) != "NONE" ? "&9&lTriggers: &a" + triggersList : ""), 
				(Utils.nullCheck(itemMap.getPermissionNode()) != "NONE" ? "&9&lPermission Node: &a" + itemMap.getPermissionNode() : ""), (Utils.nullCheck(itemMap.getEnabledWorlds().toString()) != "NONE" ? "&9&lEnabled Worlds: &a" + worldList : ""), 
				(Utils.nullCheck(itemMap.getEnabledRegions().toString()) != "NONE" ? "&9&lEnabled Regions: &a" + regionList : ""), (!itemMap.getDynamicMaterials().isEmpty() ? "&9&lMaterial Animations: &aYES" : ""), 
				(!itemMap.getDynamicNames().isEmpty() ? "&9&lName Animations: &aYES" : ""), (!itemMap.getDynamicLores().isEmpty() ? "&9&lLore Animations: &aYES" : ""), 
				(!itemMap.getDynamicOwners().isEmpty() || !itemMap.getDynamicTextures().isEmpty() ? "&9&lSkull Animations: &aYES" : ""), (Utils.nullCheck(itemMap.getLimitModes()) != "NONE" ? "&9&lLimit-Modes: &a" + itemMap.getLimitModes() : ""), 
				(Utils.nullCheck(itemMap.getProbability() + "&a%") != "NONE" ? "&9&lProbability: &a" + itemMap.getProbability() + "%" : ""), 
				(Utils.nullCheck(itemMap.getInteractCooldown() + "&7") != "NONE" ? "&9&lUse-Cooldown: &a" + itemMap.getInteractCooldown() : ""), 
				(Utils.nullCheck(itemMap.getLeatherColor()) != "NONE" ? "&9&lLeather Color: &a" + itemMap.getLeatherColor() : ""), (Utils.nullCheck(itemMap.getLeatherHex()) != "NONE" ? "&9&lLeather Color: &a" + itemMap.getLeatherHex() : ""),
				(Utils.nullCheck(itemMap.getMapImage()) != "NONE" ? "&9&lMap-Image: &a" + itemMap.getMapImage() : ""), (Utils.nullCheck(itemMap.getChargeColor() + "") != "NONE" ? "&9&lCharge Color: &a" + itemMap.getChargeColor() : ""),
				(Utils.nullCheck(patternList) != "NONE" ? "&9&lBanner Meta: &a" + patternList : ""), (Utils.nullCheck(potionList) != "NONE" ? "&9&lPotion-Effects: &a" + potionList : ""), 
				(Utils.nullCheck(itemMap.getPages() + "") != "NONE" ? "&9&lBook Pages: &aYES" : ""),
				(Utils.nullCheck(itemMap.getAuthor()) != "NONE" ? "&9&lBook Author: &a" + itemMap.getAuthor() : ""), (Utils.nullCheck(itemMap.getSkull()) != "NONE" ? "&9&lSkull-Owner: &a" + itemMap.getSkull() : ""), 
				(Utils.nullCheck(itemMap.getSkullTexture()) != "NONE" ? "&9&lSkull-Texture: &a" + (itemMap.getSkullTexture().length() > 40 ? itemMap.getSkullTexture().substring(0, 40) : itemMap.getSkullTexture()) : ""), 
				(Utils.nullCheck(itemMap.getFireworkType() + "") != "NONE" ? "&9&lFirework Type: &a" + itemMap.getFireworkType().name() : ""), 
				(Utils.nullCheck(itemMap.getFireworkPower() + "&7") != "NONE" ? "&9&lFirework Power: &a" + itemMap.getFireworkPower() : ""), (Utils.nullCheck(colorList) != "NONE" ? "&9&lFirework Color(s): &a" + colorList : ""), 
				(itemMap.getFireworkTrail() ? "&9&lFirework Trail: &aENABLED" : ""), (itemMap.getFireworkFlicker() ? "&9&lFirework Flicker: &aENABLED" : ""));
		if (ItemHandler.isSkull(itemMap.getMaterial())) {
			ItemMeta itemMeta = item.getItemMeta();
			if (itemMap.getSkull() != null) {
				itemMeta = ItemHandler.setSkullOwner(itemMeta, Utils.translateLayout(itemMap.getSkull(), player));
			} else if (itemMap.getSkullTexture() != null && !itemMap.isHeadDatabase()) {
				try {
					GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
					gameProfile.getProperties().put("textures", new Property("textures", new String(itemMap.getSkullTexture())));
					Field declaredField = itemMeta.getClass().getDeclaredField("profile");
					declaredField.setAccessible(true);
					declaredField.set(itemMeta, gameProfile);
				} catch (Exception e) { ServerHandler.sendDebugTrace(e); }
			} else if (itemMap.isHeadDatabase() && itemMap.getSkullTexture() != null) {
				HeadDatabaseAPI api = new HeadDatabaseAPI();
				ItemStack sk = api.getItemHead(itemMap.getSkullTexture());
				item = (sk != null ? sk : item.clone());
			}
			item.setItemMeta(itemMeta);
		}
		return item;
	}
	
	private boolean specialItem(final ItemMap itemMap) {
		if (itemMap.getMaterial().toString().contains("WRITTEN_BOOK") || itemMap.getMaterial().toString().contains("PLAYER_HEAD") || itemMap.getMaterial().toString().contains("SKULL_ITEM") 
				|| itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET") || itemMap.getMaterial().toString().contains("LEATHER_")) {
			return true;
		}
		return false;
	}
	
//  ==============================================================================================================================================================================================================================================================
	
	public void setModifyMenu(boolean bool, Player player) {
		if (bool) { this.modifyMenu.add(player); } 
		else { this.modifyMenu.remove(player); }
	}
	
	public boolean modifyMenu(Player player) {
		return this.modifyMenu.contains(player);
	}
	
	public boolean isOpen(Player player) {
		if (player.getOpenInventory().getTitle().toString().equalsIgnoreCase(Utils.colorFormat(this.GUIName))) {
			return true;
		}
		return false;
	}
}