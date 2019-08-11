package me.RockinChaos.itemjoin.guicreator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.giveitems.utils.ItemMap;
import me.RockinChaos.itemjoin.giveitems.utils.ItemUtilities;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Utils;

public class ItemCreator implements Listener {
	
	private static String GUIName = Utils.colorFormat("&a[BETA]   &0&n ItemJoin Menu&7   &a[BETA]");
	private ItemStack fillerPaneBItem = ItemHandler.getItem("STAINED_GLASS_PANE:15", 1, "&7", "");
	private ItemStack fillerPaneGItem = ItemHandler.getItem("STAINED_GLASS_PANE:7", 1, "&7", "");
	private ItemStack exitItem = ItemHandler.getItem("BARRIER", 1, "&c&l&nExit", "&7", "&7*Returns you to the game.");
	
	public static List < ItemMap > creatingItems = new ArrayList < ItemMap >();
	
    @EventHandler // Completed
    private void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GInventory) {
            ((GInventory) holder).onClick(event);
        }
    }

	public void LaunchCreator(final CommandSender sender) { // Completed
		final Player player = (Player) sender;
		if (!ServerHandler.hasCombatUpdate()) { GUIName = Utils.colorFormat("&a[BETA]   &0&n ItemJoin Menu"); }
        GInventory pagedPane = new GInventory(false, 1, GUIName);
        pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.addButton(new Button(fillerPaneBItem, event -> event.setCancelled(true)), 2);
        pagedPane.addButton(new Button(ItemHandler.getItem("386", 1, "&a&l&nCreate", "&7" , "&7*Create a custom item"), event -> LaunchCreating(player)));
        pagedPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, "&c&l&nModify", "&7", "&7*Modify an existing custom item"), event -> LaunchModifying(player, null)));
        pagedPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, "&e&l&nView", "&7", "&7*View the currently defined, &7items in the items.yml"), event -> LaunchViewing(player)));
        pagedPane.addButton(new Button(fillerPaneBItem, event -> event.setCancelled(true)), 2);
        pagedPane.addButton(new Button(exitItem, event -> player.closeInventory()));
        pagedPane.open(player);
	}
	
	public void LaunchViewing(final Player player) {
        GInventory pagedPane = new GInventory(true, 6, GUIName);
        for (ItemMap item : ItemUtilities.getItems()) {
            pagedPane.addButton(new Button(item.getTempItem(), event -> player.sendMessage(item.getConfigName())));
        }
        pagedPane.open(player);
	}

	public void LaunchModifying(final Player player, final ItemStack currentItem) {
		// 	public static String modifyTheItem = "&c&lModify Item";
	}

	private int getPath(final int i) {
		if (ConfigHandler.getConfig("items.yml").get("items.item_" + i) != null) { getPath(i + 1); } 
		return i;
	}
	
	public void LaunchCreating(Player player) {
		materialPane(player, new ItemMap("items.item_" + getPath(1), "ARBITRARY"), 0);
	}
	
	private boolean checkStack(ItemStack item, Inventory inventoryCheck) {
		inventoryCheck.setItem(0, item);
		if (inventoryCheck.getItem(0) != null && inventoryCheck.getItem(0).getType().name() != "AIR") { return true; }
		return false;
	}
        
	private void materialPane(final Player player, final ItemMap itemMap, final int stage) {
		GInventory materialPane = new GInventory(true, 6, GUIName);
		Inventory inventoryCheck = ItemJoin.getInstance().getServer().createInventory(null, 9, GUIName);
		for (Material material : Material.values()) {
			if (!material.name().contains("LEGACY") && material.name() != "AIR" && checkStack(ItemHandler.getItem(material.toString(), 1, "", ""), inventoryCheck)) { // doesnt exist 1.8 -> material.isItem()
				materialPane.addButton(new Button(ItemHandler.getItem(material.toString(), 1, "", "&7", "&7*Click to set the", "&7material of the item."), event -> { player.sendMessage(material + ""); itemMap.setMaterial(material); if (stage == 0) { slotPane(player, itemMap); } else { creatingPane(player, itemMap); }})); 
			}
		}
		inventoryCheck.clear();
		materialPane.open(player);
	}
	
	private void slotPane(final Player player, final ItemMap itemMap) {
		GInventory slotPane = new GInventory(false, 6, GUIName);
		GInventory craftingPane = new GInventory(false, 3, GUIName);
		craftingPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true)), 3); 
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 1, "&9&lSlot: &7&lCRAFTING&a&l[1]", "&7", "&7*Click to set the custom item", "&7to appear in the &lCRAFTING &7slot &a&l[1]&7."), event -> { itemMap.setSlot("CRAFTING[1]"); creatingPane(player, itemMap); })); 
		craftingPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true))); 
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 2, "&9&lSlot: &7&lCRAFTING&a&l[2]", "&7", "&7*Click to set the custom item", "&7to appear in the &lCRAFTING &7slot &a&l[2]&7."), event -> { itemMap.setSlot("CRAFTING[2]"); creatingPane(player, itemMap); }));
		craftingPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true)), 6); 
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 3, "&9&lSlot: &7&lCRAFTING&a&l[3]", "&7", "&7*Click to set the custom item", "&7to appear in the &lCRAFTING &7slot &a&l[3]&7."), event -> { itemMap.setSlot("CRAFTING[3]"); creatingPane(player, itemMap); }));
		craftingPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true))); 
		craftingPane.addButton(new Button(ItemHandler.getItem("58", 4, "&9&lSlot: &7&lCRAFTING&a&l[4]", "&7", "&7*Click to set the custom item", "&7to appear in the &lCRAFTING &7slot &a&l[4]&7."), event -> { itemMap.setSlot("CRAFTING[4]"); creatingPane(player, itemMap); }));
		craftingPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true)), 3); 
		
		if (ServerHandler.hasSpecificUpdate("1_8")) { craftingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu."), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> slotPane.open(player))); }
		else { craftingPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu."), event -> slotPane.open(player))); }
		
		craftingPane.addButton(new Button(fillerPaneBItem, event -> event.setCancelled(true)), 7);
		
		if (ServerHandler.hasSpecificUpdate("1_8")) { craftingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu."), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> slotPane.open(player))); }
		else { craftingPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the main slot selection menu."), event -> slotPane.open(player))); }
		
		slotPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true))); 
		slotPane.addButton(new Button(ItemHandler.getItem("SUGAR", 1, "&9&lSlot: &a&lARBITRARY", "&7", "&7*Click to set the custom item", "&7to appear in slot &a&lArbitrary&7.", "&7", "&7*Arbitrary is defined as giving the", "&7item in the first available slot."), event -> { itemMap.setSlot("Arbitrary"); creatingPane(player, itemMap); }));
		slotPane.addButton(new Button(ItemHandler.getItem("58", 1, "&9&lSlot: &a&lCRAFTING", "&7", "&7*Click to see a list of crafting slots."), event -> craftingPane.open(player))); 
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_HELMET", 1, "&9&lSlot: &a&lHELMET", "&7", "&7*Click to set the custom item", "&7to appear in slot &a&lHELMET&7."), event -> { itemMap.setSlot("HELMET"); creatingPane(player, itemMap); }));
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_CHESTPLATE", 1, "&9&lSlot: &a&lCHESTPLATE", "&7", "&7*Click to set the custom item", "&7to appear in slot &a&lCHESTPLATE&7."), event -> { itemMap.setSlot("CHESTPLATE"); creatingPane(player, itemMap); }));
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_LEGGINGS", 1, "&9&lSlot: &a&lLEGGINGS", "&7", "&7*Click to set the custom item", "&7to appear in slot &a&lLEGGINGS&7."), event -> { itemMap.setSlot("LEGGINGS"); creatingPane(player, itemMap); }));
		slotPane.addButton(new Button(ItemHandler.getItem("LEATHER_BOOTS", 1, "&9&lSlot: &a&lBOOTS", "&7", "&7*Click to set the custom item", "&7to appear in slot &a&lBOOTS&7."), event -> { itemMap.setSlot("BOOTS"); creatingPane(player, itemMap); }));
		
		if (ServerHandler.hasCombatUpdate()) { slotPane.addButton(new Button(ItemHandler.getItem("SHIELD", 1, "&9&lSlot: &a&lOFFHAND", "&7", "&7*Click to set the custom item", "&7to appear in slot &a&lOFFHAND&7."), event -> { itemMap.setSlot("OFFHAND"); creatingPane(player, itemMap); })); }
		else { slotPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true))); }
		
		slotPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true))); 
		for (int i = 9; i < 36; i++) { 
			final int slot = i;
			slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:3", i, "&9&lSlot: &a&l" + i, "&7", "&7*Click to set the custom item", "&7to appear in slot &a&l" + i + "&7."), event -> { itemMap.setSlot(slot + ""); creatingPane(player, itemMap); })); 
		}
		for (int j = 0; j < 9; j++) { 
			final int slot = j; int count = j;
			if (slot == 0) { count = 1; }
			slotPane.addButton(new Button(ItemHandler.getItem("STAINED_GLASS_PANE:11", count, "&9&lSlot: &a&l" + j, "&7", "&7*Click to set the custom item", "&7to appear in slot &a&l" + j + "&7."), event -> { itemMap.setSlot(slot + ""); creatingPane(player, itemMap); }));
		}
		
		if (ServerHandler.hasSpecificUpdate("1_8")) { slotPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> LaunchCreating(player))); }
		else { slotPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> LaunchCreating(player))); }
		
		slotPane.addButton(new Button(fillerPaneBItem, event -> event.setCancelled(true)), 7);
		
		if (ServerHandler.hasSpecificUpdate("1_8")) { slotPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19"), event -> LaunchCreating(player))); }
		else { slotPane.addButton(new Button(ItemHandler.getItem("ARROW", 1, "&c&l&nReturn", "&7", "&7*Returns you back to", "&7the material selection menu."), event -> LaunchCreating(player))); }
		slotPane.open(player);
	}
	
	private void creatingPane(final Player player, final ItemMap itemMap) {
		GInventory creatingPane = new GInventory(false, 4, GUIName);
		creatingPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true)), 4); 
		creatingPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString(), 1, "&7*&6&l&nItem Information", "&7", "&9&lMaterial: &a" + itemMap.getMaterial().toString(), "&9&lSlot: &a" + itemMap.getSlot()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(fillerPaneGItem, event -> event.setCancelled(true)), 4); 
		
		
		creatingPane.addButton(new Button(ItemHandler.getItem(itemMap.getMaterial().toString(), 1, "&b&lMaterial", "&7", "&7*Click to set the item &7&lMATERIAL", "&9&lMATERIAL: &a" + itemMap.getMaterial().toString()), event -> materialPane(player, itemMap, 1)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GLASS", 1, "&b&lSlot", "&7", "&7*Click to set the item &7&lSLOT", "&9&lSlot: &a" + itemMap.getSlot()), event -> slotPane(player, itemMap)));
		creatingPane.addButton(new Button(ItemHandler.getItem("DIAMOND", 1, "&b&lCount", "&7", "&7*Click to set the item &7&lCOUNT", "&9&lCOUNT: &a" + itemMap.getCount()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("NAME_TAG", 1, "&b&lName", "&7", "&7*Click to set the item &7&lNAME", "&9&lNAME: &a" + itemMap.getCustomName()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("386", 1, "&b&lLore", "&7", "&7*Click to set the item &7&lLORE", "&9&lLORE: &a" + itemMap.getCustomLore()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("DIAMOND_SWORD", 1, "&b&lDurability", "&7", "&7*Click to set the item &7&lDURABILITY", "&9&lDURABILITY: &a" + itemMap.getDurability()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("BOOK", 1, "&b&lCommands Settings", "&7", "&7*Click to set the item &7&lCOMMANDS SETTINGS"), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("ENCHANTED_BOOK", 1, "&b&lEnchantments", "&7", "&7*Click to set the item &7&lENCHANTMENTS", "&9&lENCHANTMENTS: &a" + itemMap.getEnchantments()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("CHEST", 1, "&b&lItemflags", "&7", "&7*Click to set the item &7&lITEMFLAGS", "&9&lITEMFLAGS: &a" + itemMap.getItemFlags()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("REDSTONE", 1, "&b&lTriggers", "&7", "&7*Click to set the item &7&lTRIGGERS", "&9&lTRIGGERS: &a" + itemMap.getTriggers()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("76", 1, "&b&lPermission-Node", "&7", "&7*Click to set the item &7&lPERMISSION-NODE", "&9&lPERMISSION-NODE: &a" + itemMap.getPermissionNode()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GRASS", 1, "&b&lEnabled-Worlds", "&7", "&7*Click to set the item &7&lENABLED-WORLDS", "&9&lENABLED-WORLDS: &a" + itemMap.getEnabledWorlds()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, "&b&lEnabled-Regions", "&7", "&7*Click to set the item &7&lENABLED-REGIONS", "&9&lENABLED-REGIONS: &a" + itemMap.getEnabledRegions()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, "&b&lAnimation", "&7", "&7*Click to set the item &7&lANIMATIONS", "&9&lANIMATIONS: &a" + itemMap.isAnimated()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, "&b&lLimit-Modes", "&7", "&7*Click to set the item &7&lLIMIT-MODES", "&9&lLIMIT-MODES: &a" + itemMap.isUseOnLimitSwitch()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, "&b&lProbability", "&7", "&7*Click to set the item &7&lPROBABILITY", "&9&lPROBABILITY: &a" + itemMap.getProbability()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, "&b&lUse-Cooldown", "&7", "&7*Click to set the item &7&lUSE-COOLDOWN", "&9&lUSE-COOLDOWN: &a" + itemMap.getInteractCooldown()), event -> event.setCancelled(true)));
		creatingPane.addButton(new Button(ItemHandler.getItem("GOLD_BLOCK", 1, "&b&lUse-Cooldown", "&7", "&7*Click to set the item &7&lUSE-COOLDOWN", "&9&lUSE-COOLDOWN: &a" + itemMap.getInteractCooldown()), event -> event.setCancelled(true)));
		
		
		creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu."), event -> LaunchCreator(player)));
		creatingPane.addButton(new Button(fillerPaneBItem, event -> event.setCancelled(true)), 3);

		if (ServerHandler.hasSpecificUpdate("1_8")) { creatingPane.addButton(new Button(ItemHandler.setSkullTexture(ItemHandler.getItem("SKULL_ITEM:3", 1, "&a&l&nSave to Config", "&7", "&7*Saves this new custom item", "&7to the items.yml."), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdiNjJkMjc1ZDg3YzA5Y2UxMGFjYmNjZjM0YzRiYTBiNWYxMzVkNjQzZGM1MzdkYTFmMWRmMzU1YTIyNWU4MiJ9fX0"), event -> LaunchCreating(player))); }
		else { creatingPane.addButton(new Button(ItemHandler.getItem("WOOL:5", 1, "&a&l&nSave to Config", "&7", "&7*Saves this new custom item", "&7to the items.yml."), event -> { itemMap.saveToConfig(); player.closeInventory(); })); }
		
		creatingPane.addButton(new Button(fillerPaneBItem, event -> event.setCancelled(true)), 3);
		creatingPane.addButton(new Button(ItemHandler.getItem("BARRIER", 1, "&c&l&nMain Menu", "&7", "&7*Returns you to the main menu."), event -> LaunchCreator(player)));
		creatingPane.open(player);
	}
    
    public static boolean isOpen(Player player) {
    	if (player.getOpenInventory().getTitle().toString().equalsIgnoreCase(GUIName)) {
    		return true;
    	}
    	return false;
    }
}