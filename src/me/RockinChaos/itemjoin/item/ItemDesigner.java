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
package me.RockinChaos.itemjoin.item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ConfigHandler;
import me.RockinChaos.itemjoin.handlers.ItemHandler;
import me.RockinChaos.itemjoin.listeners.Recipes;
import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.SchedulerUtils;
import me.RockinChaos.itemjoin.utils.ServerUtils;
import me.RockinChaos.itemjoin.utils.StringUtils;
import me.RockinChaos.itemjoin.utils.api.ChanceAPI;
import me.RockinChaos.itemjoin.utils.api.DependAPI;
import me.RockinChaos.itemjoin.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.utils.images.Renderer;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.SQL;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;

public class ItemDesigner {
	
	private static ItemDesigner designer;
	
   /**
	* Creates a new ItemDesigner instance.
	* 
	*/
	public ItemDesigner() {
		if (ConfigHandler.getConfig().itemsExist()) {
			for (String internalName: ConfigHandler.getConfig().getConfigurationSection().getKeys(false)) {
				ConfigurationSection itemNode = ConfigHandler.getConfig().getItemSection(internalName);
				if (this.isConfigurable(internalName, itemNode)) {
					String slotList = ((itemNode.getString(".slot") != null && !itemNode.getString(".slot").isEmpty()) ? itemNode.getString(".slot") : "ARBITRARY");
					String[] slots = slotList.replace(" ", "").split(",");
					for (String slot: slots) {
						if (this.isDefinable(internalName, slot)) {
							ItemMap itemMap = new ItemMap(internalName, slot);
							
							this.setMaterial(itemMap);
							this.setSkullDatabase(itemMap);
							this.setUnbreaking(itemMap);
							this.durabilityBar(itemMap);
							this.setEnchantments(itemMap);
							this.setMapImage(itemMap);
							this.setJSONBookPages(itemMap);
							this.setNBTData(itemMap);
							this.setName(itemMap);
							this.setLore(itemMap);
							this.setDurability(itemMap);
							this.setData(itemMap);
							this.setModelData(itemMap);
							this.setSkull(itemMap);
							this.setSkullTexture(itemMap);
							this.setConsumableEffects(itemMap);
							this.setPotionEffects(itemMap);
							this.setTippedArrows(itemMap);
							this.setBanners(itemMap);
							this.setFireworks(itemMap);
							this.setFireChargeColor(itemMap);
							this.setDye(itemMap);
							this.setBookAuthor(itemMap);
							this.setBookTitle(itemMap);
							this.setBookGeneration(itemMap);
							this.setLegacyBookPages(itemMap);
							this.setAttributes(itemMap);
							this.setAttributeFlags(itemMap);
							this.setProbability(itemMap);
							this.setMobsDrop(itemMap);
							this.setBlocksDrop(itemMap);
							this.setRecipe(itemMap);
							
							itemMap.setContents();
							ItemUtilities.getUtilities().addItem(itemMap);
							ItemUtilities.getUtilities().addCraftingItem(itemMap);
					    	ConfigHandler.getConfig().registerListeners(itemMap);
						}
					}
				}
			}
			ItemUtilities.getUtilities().updateItems();
		}
	}
	
   /**
	* Determines if the specific item node has a valid Material ID 
	* defined as well as if the item node has a slot defined.
	* 
	* @param internalName - The node name of the item.
	* @param itemNode - The item node location.
	* @return If the material is valid.
	*/
	private boolean isConfigurable(final String internalName, final ConfigurationSection itemNode) {
		String id = ItemHandler.getMaterial(itemNode);
		String dataValue = null;
		if (id != null) {
			if (id.contains(":")) {
				String[] parts = id.split(":"); id = parts[0]; dataValue = parts[1];
				if (ServerUtils.hasSpecificUpdate("1_13")) {
					ServerUtils.logWarn("{ItemMap} The item " + internalName + " is using a Legacy Material which is no longer supported as of Minecraft 1.13.");
					ServerUtils.logWarn("{ItemMap} This will cause issues, please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html for a list of material names.");
				}
			}
			if (!ServerUtils.hasSpecificUpdate("1_9") && id.equalsIgnoreCase("TIPPED_ARROW") || id.equalsIgnoreCase("440") || id.equalsIgnoreCase("0440")) {
				ServerUtils.logSevere("{ItemMap} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have the item TIPPED_ARROW.");
				ServerUtils.logWarn("{ItemMap} You are receiving this notice because the item(s) exists in your items.yml and will not be set, please remove the item(s) or update your server.");
				return false;
			} else if (!ServerUtils.hasSpecificUpdate("1_9") && id.equalsIgnoreCase("LINGERING_POTION") || id.equalsIgnoreCase("441") || id.equalsIgnoreCase("0441")) {
				ServerUtils.logSevere("{ItemMap} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have the item LINGERING_POTION.");
				ServerUtils.logWarn("{ItemMap} You are receiving this notice because the item(s) exists in your items.yml and will not be set, please remove the item(s) or update your server.");
				return false;
			} else if (ItemHandler.getMaterial(id, dataValue) == null) {
				ServerUtils.logSevere("{ItemMap} The Item " + internalName + "'s Material 'ID' is invalid or does not exist.");
				ServerUtils.logWarn("{ItemMap} The Item " + internalName + " will not be set!");
				if (StringUtils.getUtils().isInt(id)) {
					ServerUtils.logSevere("{ItemMap} If you are using a numerical id and a numerical dataValue.");
					ServerUtils.logSevere("{ItemMap} Include quotations or apostrophes at the beginning and the end or this error will persist, the id should look like '160:15' or \"160:15\".");
				}
				return false;
			}
		} else { 
			ServerUtils.logSevere("{ItemMap} The Item" + internalName + " does not have a Material ID defined."); 
			ServerUtils.logWarn("{ItemMap} The Item " + internalName + " will not be set!"); 
			return false;
		}
		return true;
	}

   /**
	* Determines if the specific item node has an actual ItemJoin 
	* definable slot, being a custom slot or a true integer slot.
	* 
	* @param internalName - The node name of the item.
	* @param slot - The slot of the item.
	* @return If the slot is valid.
	*/
	private boolean isDefinable(final String internalName, final String slot) {
		if (!StringUtils.getUtils().isInt(slot) && !ItemHandler.isCustomSlot(slot)) {
			ServerUtils.logSevere("{ItemMap} The Item " + internalName + "'s slot is invalid or does not exist.");
			ServerUtils.logWarn("{ItemMap} The Item " + internalName + " will not be set!");
			return false;
		} else if (StringUtils.getUtils().isInt(slot)) {
			int parseSlot = Integer.parseInt(slot);
			if (!(parseSlot >= 0 && parseSlot <= 35)) {
				ServerUtils.logSevere("{ItemMap} The Item " + internalName + "'s slot must be between 0 and 35.");
				ServerUtils.logWarn("{ItemMap} The Item " + internalName + " will not be set!");
				return false;
			}
		} else if (!ServerUtils.hasSpecificUpdate("1_9") && slot.equalsIgnoreCase("Offhand")) {
			ServerUtils.logWarn("{ItemMap} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have OFFHAND support!");
			return false;
		}
		return true;
	}
	
   /**
	* Sets the Custom Material to the Custom Item.
	*
	*@param itemMap - The ItemMap being modified.
	*/
	private void setMaterial(final ItemMap itemMap) {
		Material material = this.getActualMaterial(itemMap);
		itemMap.setMaterial(material);
		itemMap.renderItemStack();
	}
	
   /**
	* Fetches the correct Bukkit material.
	* 
	* @param itemMap - The ItemMap being modified.
	* @return The found Bukkit material.
	*/
	private Material getActualMaterial(final ItemMap itemMap) {
		String material = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".id"));
		if (ConfigHandler.getConfig().getMaterialSection(itemMap.getNodeLocation()) != null) {
			List<String> materials = new ArrayList<String>();
			for (String materialKey : ConfigHandler.getConfig().getMaterialSection(itemMap.getNodeLocation()).getKeys(false)) {
				String materialList = itemMap.getNodeLocation().getString(".id." + materialKey);
				if (materialList != null) {
					materials.add(materialList);
				}
			}
			itemMap.setDynamicMaterials(materials);
			material = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".id." + ConfigHandler.getConfig().getMaterialSection(itemMap.getNodeLocation()).getKeys(false).iterator().next()));
			if (material.contains(":")) { String[] parts = material.split(":"); itemMap.setDataValue((short) Integer.parseInt(parts[1])); }
			return ItemHandler.getMaterial(material, null);
		}
		if (material.contains(":")) { String[] parts = material.split(":"); itemMap.setDataValue((short) Integer.parseInt(parts[1])); }
		return ItemHandler.getMaterial(material, null);
	}
	
   /**
	* Sets the HeadDatabase Texture to the Custom Skull Item.
	*
	*@param itemMap - The ItemMap being modified.
	*/
	private void setSkullDatabase(final ItemMap itemMap) {
		if (DependAPI.getDepends(false).databaseEnabled() && itemMap.getNodeLocation().getString(".skull-texture") != null) {
			if (itemMap.getMaterial().toString().equalsIgnoreCase("SKULL_ITEM") || itemMap.getMaterial().toString().equalsIgnoreCase("PLAYER_HEAD")) {
				if (itemMap.getNodeLocation().getString(".skull-owner") != null) {  ServerUtils.logWarn("{ItemMap} You cannot define a skull owner and a skull texture at the same time, remove one from the item."); return;  }
				String skullTexture = getActualTexture(itemMap);
				if (skullTexture.contains("hdb-")) {
					try {
						itemMap.setSkullTexture(skullTexture.replace("hdb-", ""));
						itemMap.setHeadDatabase(true);
					} catch (NullPointerException e) {
						ServerUtils.logSevere("{ItemMap} HeadDatabaseAPI could not find #" + skullTexture + ", this head does not exist.");
						ServerUtils.sendDebugTrace(e);
					}
				}
			}
		}
	}
	
   /**
	* Fetches the correct Skull Texture.
	* 
	* @param itemMap - The ItemMap being modified.
	* @return The found skull texture.
	*/
	private String getActualTexture(final ItemMap itemMap) {
		ConfigurationSection textureSection = ConfigHandler.getConfig().getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".skull-texture");
		String texture = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".skull-texture"));
		if (textureSection != null) {
			List<String> textures = new ArrayList<String>();
			for (String textureKey : textureSection.getKeys(false)) {
				String textureList = itemMap.getNodeLocation().getString(".skull-texture." + textureKey);
				if (textureList != null) {
					textures.add(textureList);
				}
			}
			itemMap.setDynamicTextures(textures);
			return ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".skull-texture." + textureSection.getKeys(false).iterator().next()));
		}
		if (texture != null && !texture.isEmpty()) {
			if (itemMap.isDynamic() || itemMap.isAnimated()) {
				List<String> textures = new ArrayList<String>(); textures.add(texture);
				itemMap.setDynamicTextures(textures);
			}
		}
		return texture;
	}

   /**
	* Sets the Unbreakable Boolean to the Custom Item, 
	* preventing any item with a durability from being damaged
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setUnbreaking(final ItemMap itemMap) {
		if (StringUtils.getUtils().containsIgnoreCase(itemMap.getItemFlags(), "unbreakable")) {
			try {
				itemMap.setUnbreakable(true);
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); } }
	}

   /**
	* Hides the Durability Bar of the Custom Item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void durabilityBar(final ItemMap itemMap) {
		if (StringUtils.getUtils().containsIgnoreCase(itemMap.getItemFlags(), "hide-durability")) {
			try {
				itemMap.setDurabilityBar(true);
			} catch (Exception e) { ServerUtils.sendDebugTrace(e); } }
	}
	
   /**
	* Sets the Custom Enchants to the Custom Item, 
	* adding the specified enchantments to the item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setEnchantments(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".enchantment") != null) {
			String enchantlist = itemMap.getNodeLocation().getString(".enchantment").replace(" ", "");
			String[] enchantments = enchantlist.split(",");
			Map < String, Integer > listEnchants = new HashMap < String, Integer > ();
			for (String enchantment: enchantments) {
				String[] parts = enchantment.split(":");
				String name = parts[0].toUpperCase();
				int level = 1;
				Enchantment enchantName = ItemHandler.getEnchantByName(name);
				if (StringUtils.getUtils().containsIgnoreCase(enchantment, ":")) {
					try {
						level = Integer.parseInt(parts[1]);
					} catch (NumberFormatException e) {
						ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + parts[1] + " is not a number and a number was expected!");
						ServerUtils.logWarn("{ItemMap} Enchantment: " + parts[0] + " will now be enchanted by level 1.");
						ServerUtils.sendDebugTrace(e);
					}
				}
				if (enchantName != null) {
					listEnchants.put(name, level);
				} else if (enchantName == null && DependAPI.getDepends(false).tokenEnchantEnabled() && TokenEnchantAPI.getInstance().getEnchantment(name) != null) {
					listEnchants.put(name, level);
				} else if (enchantName == null && !DependAPI.getDepends(false).tokenEnchantEnabled()) {
					ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + name + " is not a proper enchant name!");
					ServerUtils.logWarn("{ItemMap} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html for a list of correct enchantment names.");
				}
			}
			itemMap.setEnchantments(listEnchants);
		}
	}
	
   /**
	* Sets the Custom Map Image to the Custom Item, 
	* draws the specified map image on the items canvas.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setMapImage(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".custom-map-image") != null && StringUtils.getUtils().containsIgnoreCase(itemMap.getMaterial().toString(), "MAP")) {
			if (itemMap.getNodeLocation().getString(".map-id") != null && StringUtils.getUtils().isInt(itemMap.getNodeLocation().getString(".map-id"))) { itemMap.setMapID(itemMap.getNodeLocation().getInt(".map-id")); }
			itemMap.setMapImage(itemMap.getNodeLocation().getString(".custom-map-image"));
			if (itemMap.getMapImage().equalsIgnoreCase("default.jpg") || new File(ItemJoin.getInstance().getDataFolder(), itemMap.getMapImage()).exists()) {
				DataObject dataObject = SQL.getData().getData(new DataObject(Table.MAP_IDS, null, null, itemMap.getMapImage(), null));
				if (dataObject != null && (itemMap.getMapID() == -1 || (itemMap.getMapID() == Integer.parseInt(dataObject.getMapID())))) {
					int mapID = Integer.parseInt(dataObject.getMapID());
					MapRenderer imgPlatform = this.createRenderer(itemMap.getMapImage(), mapID);
					MapView view = ItemHandler.existingView(mapID);
					itemMap.setMapID(mapID);
					itemMap.setMapView(view);
					try { view.removeRenderer(view.getRenderers().get(0)); } catch (NullPointerException e) { ServerUtils.sendDebugTrace(e); }
					try { view.addRenderer(imgPlatform); } catch (NullPointerException e) { ServerUtils.sendDebugTrace(e); }
				} else {
					MapView view = LegacyAPI.createMapView();
					try { view.removeRenderer(view.getRenderers().get(0)); } catch (NullPointerException e) { ServerUtils.sendDebugTrace(e); }
					int mapID = (itemMap.getMapID() != -1 ? itemMap.getMapID() : LegacyAPI.getMapID(view));
					MapRenderer imgPlatform = this.createRenderer(itemMap.getMapImage(), mapID);
					itemMap.setMapID(mapID);
					itemMap.setMapView(view);
					try { view.addRenderer(imgPlatform); } catch (NullPointerException e) { ServerUtils.sendDebugTrace(e); }
					SQL.getData().saveData(new DataObject(Table.MAP_IDS, null, null, itemMap.getMapImage(), Integer.toString(itemMap.getMapID())));
				}
			}
		} else if (itemMap.getNodeLocation().getString(".map-id") != null && StringUtils.getUtils().isInt(itemMap.getNodeLocation().getString(".map-id")) && StringUtils.getUtils().containsIgnoreCase(itemMap.getMaterial().toString(), "MAP")) {
			itemMap.setMapID(itemMap.getNodeLocation().getInt(".map-id"));
			MapView view = ItemHandler.existingView(itemMap.getMapID());
			itemMap.setMapView(view);
		}
	}
	
   /**
    * Creates a new MapRenderer.
    * 
    * @param image - The image to be rendered, ex: 'default.jpg'.
    * @param imageID - The id of the MapView.
    * @return The newly created MapRenderer instance.
    */
    public MapRenderer createRenderer(final String image, final int imageID) {
    	if (StringUtils.getUtils().containsIgnoreCase(image, ".GIF")) { 
    		return new Renderer(image, imageID, 0, -1);
    	} else { return new Renderer(image, imageID); }
    }
	
   /**
	* Sets the NBTData to the Custom Item, 
	* designing the item to be unique to ItemJoin.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setNBTData(final ItemMap itemMap) {
		if (ItemHandler.dataTagsEnabled() && !itemMap.isVanilla() && !itemMap.isVanillaControl() && !itemMap.isVanillaStatus()) {
			try {
				Object tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
				tag.getClass().getMethod("setString", String.class, String.class).invoke(tag, "ItemJoin Name", itemMap.getConfigName());
				tag.getClass().getMethod("setString", String.class, String.class).invoke(tag, "ItemJoin Slot", itemMap.getItemValue());
				itemMap.setNewNBTData(itemMap.getConfigName() + " " + itemMap.getItemValue(), tag);
				
				if (itemMap.getNodeLocation().getString(".properties") != null && !itemMap.getNodeLocation().getString(".properties").isEmpty()) {
					List<Object> tags = new ArrayList<Object>();
					Map<String, String> tagValues = new HashMap<String, String>();
					String[] properties = itemMap.getNodeLocation().getString(".properties").split(",");
					for (String property: properties) {
						String[] propertyParts = property.split(":");
						String identifier = (propertyParts[0].startsWith(" ") ? propertyParts[0].substring(1) : propertyParts[0]);
						Object propertyTag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
						propertyTag.getClass().getMethod("setString", String.class, String.class).invoke(propertyTag, identifier, propertyParts[1]);
						tags.add(propertyTag);
						tagValues.put(identifier, propertyParts[1]);
					}
					itemMap.setNBTProperties(tagValues, tags);
				}
			} catch (Exception e) {
				ServerUtils.logSevere("{ItemMap} An error has occured when setting NBTData to an item.");
				ServerUtils.sendDebugTrace(e);
			}
		} else { itemMap.setLegacySecret(StringUtils.getUtils().colorEncode(itemMap.getNBTFormat())); }
	}
	
   /**
	* Sets the Book Pages to the Custom Item, 
	* adding the custom book pages to the item in JSON Formatting.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setJSONBookPages(final ItemMap itemMap) {
		ConfigurationSection pagesSection = ConfigHandler.getConfig().getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".pages");
		if (itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK") && itemMap.getNodeLocation().getString(".pages") != null && pagesSection != null && ServerUtils.hasSpecificUpdate("1_8")) {
			List < String > JSONPages = new ArrayList < String > ();
			List < List < String > > rawPages = new ArrayList < List < String > > ();
			for (String pageString: pagesSection.getKeys(false)) {
				List < String > pageList = itemMap.getNodeLocation().getStringList(".pages." + pageString);
				rawPages.add(pageList);
				String textBuilder = "[\"\"";
				for (int k = 0; k < pageList.size(); k++) {
					Map < Integer, String > JSONBuilder = new HashMap < Integer, String > ();
					String formatLine = pageList.get(k);
					if (this.containsJSONEvent(formatLine)) {
						while (this.containsJSONEvent(formatLine)) {
							for (JSONEvent jsonType: JSONEvent.values()) {
								Matcher matchPattern = java.util.regex.Pattern.compile(jsonType.matchType + "(.*?)>").matcher(formatLine);
								if (matchPattern.find()) {
									String inputResult = matchPattern.group(1);
									JSONBuilder.put(JSONBuilder.size(), ((jsonType != JSONEvent.TEXT) 
										? (",\"" + jsonType.event + "\":{\"action\":\"" + jsonType.action + "\",\"value\":\"" + inputResult + "\"}") 
										: ("," + "{\"" + jsonType.action + "\":\"" + inputResult + "\"")));
									formatLine = formatLine.replace(jsonType.matchType + inputResult + ">", "<JSONEvent>");
									this.safteyCheckURL(itemMap, jsonType, inputResult);
								}
							}
						}
						if (!formatLine.isEmpty() && formatLine.length() != 0 && !formatLine.trim().isEmpty()) {
							boolean definingText = false;
							String[] JSONEvents = formatLine.split("<JSONEvent>");
							if (!(org.apache.commons.lang.StringUtils.countMatches(formatLine,"<JSONEvent>") <= JSONEvents.length)) { 
								String adjustLine = new String(); 
								for (String s : formatLine.split("JSONEvent>"))  { adjustLine += s + "JSONEvent> "; } 
								JSONEvents = adjustLine.split("<JSONEvent>"); 
							}
							for (int i = 0; i < JSONEvents.length; i++) {
								if (!JSONEvents[i].isEmpty() && JSONEvents[i].length() != 0 && !JSONEvents[i].trim().isEmpty()) {
									textBuilder += ((i == 0) ? "," : "},") + "{\"" + "text" + "\":\"" + JSONEvents[i] + ((JSONBuilder.get(i) != null && JSONBuilder.get(i).contains("\"text\"")) 
										? "\"}" : "\"") + (JSONBuilder.get(i) != null ? JSONBuilder.get(i) : "");
								} else if (JSONBuilder.get(i) != null) {
									if (JSONBuilder.get(i).contains("\"text\"") && !definingText) {
										textBuilder += JSONBuilder.get(i); definingText = true;
									} else if (JSONBuilder.get(i).contains("\"text\"") && definingText) {
										textBuilder += "}" + JSONBuilder.get(i); definingText = false;
									} else {
									textBuilder += JSONBuilder.get(i);
									}
								}
							}
							textBuilder += "}," + "{\"text\":\"\\n\",\"color\":\"reset\"}";
						}
					} else if (formatLine.contains("raw:")) {
						textBuilder += formatLine.replace("raw: ", "").replace("raw:", "").replace("[\"\"", "").replace("\"bold\":false}]", "\"bold\":false}").replace("\"bold\":true}]", "\"bold\":true}") + "," + "{\"text\":\"\\n\",\"color\":\"reset\"}";
					} else { textBuilder += "," + "{\"text\":\"" + formatLine + "\"}" + "," + "{\"text\":\"\\n\",\"color\":\"reset\"}"; }
				}
				JSONPages.add(textBuilder + "]");
			}
			itemMap.setPages(JSONPages);
			itemMap.setListPages(rawPages);
		}
	}
	
   /**
	* Checks if the book pages contains a JSONEvent.
	* 
	* @param formatPage - The page to be formatted..
	* @return If the book page contains a JSONEvent.
	*/
	private boolean containsJSONEvent(final String formatPage) {
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
	private void safteyCheckURL(final ItemMap itemMap, final JSONEvent type, final String inputResult) {
		if (type.equals(JSONEvent.OPEN_URL)) {
			if (!StringUtils.getUtils().containsIgnoreCase(inputResult, "https") && !StringUtils.getUtils().containsIgnoreCase(inputResult, "http")) {
				ServerUtils.logSevere("{ItemMap} The URL Specified for the clickable link in the book " + itemMap.getConfigName() + " is missing http or https and will not be clickable.");
				ServerUtils.logWarn("{ItemMap} A URL designed for a clickable link should resemble this link structure: https://www.google.com/");
			}
		}
	}
	
   /**
	* Sets the Custom Name to the Custom Item, 
	* adding the custom name to the items display name.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setName(final ItemMap itemMap) {
		String name = getActualName(itemMap);
		if (ItemHandler.dataTagsEnabled() && ServerUtils.hasSpecificUpdate("1_8") || itemMap.isVanilla() && ServerUtils.hasSpecificUpdate("1_8")) {
			itemMap.setCustomName(name);
		} else {
			itemMap.setCustomName(encodeName(itemMap, name));
		}
	}
	
   /**
	* Encodes the LegacySecret into the Custom Items name.
	* 
	* @param itemMap - The ItemMap being modified.
	* @param text - The String to be encoded.
	* @return The correctly encoded display name containing the plugin secret.
	*/
	private String encodeName(final ItemMap itemMap, final String text) {
		return ("&f" + text + itemMap.getLegacySecret());
	}
	
   /**
	* Gets the exact name to be set to the Custom Item.
	* 
	* @param itemMap - The ItemMap being modified.
	* @return The correctly formatted display name.
	*/
	private String getActualName(final ItemMap itemMap) {
		ConfigurationSection nameSection = ConfigHandler.getConfig().getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".name");
		String name = itemMap.getNodeLocation().getString(".name");
		try { ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".name")); } catch (Exception e) { }
		if (nameSection != null) {
			List<String> names = new ArrayList<String>();
			for (String nameKey : nameSection.getKeys(false)) {
				String nameList = itemMap.getNodeLocation().getString(".name." + nameKey);
				if (nameList != null) {
					names.add(nameList);
				}
			}
			itemMap.setDynamicNames(names);
			return ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".name." + nameSection.getKeys(false).iterator().next()));
		} else if (name == null || name.isEmpty()) {
			return ItemHandler.getMaterialName(itemMap.getTempItem());
		}
		if (name != null && !name.isEmpty()) {
			if (itemMap.isDynamic() || itemMap.isAnimated()) {
				List<String> names = new ArrayList<String>(); names.add(name);
				itemMap.setDynamicNames(names);
			}
		}
		return ItemHandler.cutDelay(name);
	}

   /**
	* Sets the Custom Lore to the Custom Item,
	* adding the custom lore to the items lore.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setLore(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".lore") != null) {
			List <String> lore = getActualLore(itemMap);
			itemMap.setCustomLore(lore);
		}
	}
	
   /**
	* Gets the exact lore to be set to the Custom Item.
	* 
	* @param itemMap - The ItemMap being modified.
	* @return The correctly formatted list of displayed lores.
	*/
	private List < String > getActualLore(final ItemMap itemMap) {
		ConfigurationSection loreSection = ConfigHandler.getConfig().getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".lore");
		List <String> lore = itemMap.getNodeLocation().getStringList(".lore");
		if (loreSection != null) {
			List<List<String>> lores = new ArrayList<List<String>>();
			for (String loreKey : loreSection.getKeys(false)) {
				List<String> loreList = itemMap.getNodeLocation().getStringList(".lore." + loreKey);
				if (loreList != null) {
					lores.add(loreList);
				}
			}
			itemMap.setDynamicLores(lores);
			return itemMap.getNodeLocation().getStringList(".lore." + loreSection.getKeys(false).iterator().next());
		}
		if (lore != null && !lore.isEmpty()) {
			if (itemMap.isDynamic() || itemMap.isAnimated()) {
				List<List<String>> lores = new ArrayList<List<String>>(); lores.add(lore);
				itemMap.setDynamicLores(lores);
			}
		}
		return lore;
	}

   /**
	* Sets the Durability to the Custom Item,
	* changes the items durability to the specified durability.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setDurability(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".data") == null || itemMap.getNodeLocation().getInt(".data") == 0) {
			if (itemMap.getNodeLocation().getString(".skull-owner") != null) {
				itemMap.setDurability((short) 3);
			} else if (itemMap.getNodeLocation().getString(".durability") != null) {
				int durability = itemMap.getNodeLocation().getInt(".durability");
				itemMap.setDurability((short) durability);
			}
		}
	}
	
   /**
	* Sets the durability model data to the Custom Item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setData(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".data") != null) {
			itemMap.setData(itemMap.getNodeLocation().getInt(".data"));
			itemMap.setAttributesInfo(true);
			itemMap.setUnbreakable(true);
		}
	}

	
   /**
	* Sets the Model Data for the Custom Item,
	* adding an NBTTag to the item containing the numerical value for the Custom Model Data.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setModelData(final ItemMap itemMap) {
		if (ServerUtils.hasSpecificUpdate("1_14") && itemMap.getNodeLocation().getString(".model-data") != null) {
			itemMap.setModelData(itemMap.getNodeLocation().getInt(".model-data"));
		}
	}
	
   /**
	* Sets the Probability of the Custom Item,
	* defining the probability percentage of the item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setProbability(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".probability") != null) {
			String percentageString = itemMap.getNodeLocation().getString(".probability").replace("%", "").replace("-", "").replace(" ", "");
			int percentage = Integer.parseInt(percentageString);
			if (!ChanceAPI.getChances().getItems().containsKey(itemMap)) { ChanceAPI.getChances().putItem(itemMap, percentage); }
			itemMap.setProbability(percentage);
			if (itemMap.getProbability() == 100) {
				ServerUtils.logWarn("{ItemMap} An item cannot be defined with 100 percent probability, please check the wiki on this usage.");
				ServerUtils.logWarn("{ItemMap} Please change the probability of the item, or remove it entirely, items may not function.");
			}
		}
	}
	
   /**
	* Sets the Mobs Drop Chances of the Custom Item,
	* defining the drop percentage of the item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setMobsDrop(final ItemMap itemMap) {
		Map < EntityType, Double > mobsDrop = new HashMap < EntityType, Double > ();
		if (itemMap.getNodeLocation().getString(".mobs-drop") != null) {
			List < String > mobs = itemMap.getNodeLocation().getStringList(".mobs-drop");
			for (String mobsLine: mobs) {
				String[] mobsParts = mobsLine.replace(" ", "").split(":");
				if (mobsParts[0] != null && mobsParts[1] != null && StringUtils.getUtils().isDouble(mobsParts[1])) {
					EntityType mob = EntityType.valueOf(mobsParts[0].toUpperCase());
					if (mob != null) {
						mobsDrop.put(mob, Double.parseDouble(mobsParts[1]));
					} else { ServerUtils.logWarn("{ItemMap} The mob " + mobsParts[0] + " is not a valid mob type, please check the wiki on this usage."); }
				} else if (!StringUtils.getUtils().isDouble(mobsParts[1])) {
					ServerUtils.logWarn("{ItemMap} The percentage value for the mob " + mobsParts[0] + " is not a valid number, please check the wiki on this usage.");
				} else {
					ServerUtils.logWarn("{ItemMap} An error has occured when trying to set mobs drop for " + itemMap.getConfigName() + ", please check your formatting.");
				}
			}
			itemMap.setMobsDrop(mobsDrop);
		}
	}
	
   /**
	* Sets the Blocks Drop Chances of the Custom Item,
	* defining the drop percentage of the item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setBlocksDrop(final ItemMap itemMap) {
		Map < Material, Double > blocksDrop = new HashMap < Material, Double > ();
		if (itemMap.getNodeLocation().getString(".blocks-drop") != null) {
			List < String > blocks = itemMap.getNodeLocation().getStringList(".blocks-drop");
			for (String blocksLine: blocks) {
				String[] blocksParts = blocksLine.replace(" ", "").split(":");
				if (blocksParts[0] != null && blocksParts[1] != null && StringUtils.getUtils().isDouble(blocksParts[1])) {
					Material block = ItemHandler.getMaterial(blocksParts[0].toUpperCase(), null);
					if (block != null && block != Material.AIR) {
						blocksDrop.put(block, Double.parseDouble(blocksParts[1]));
					} else { ServerUtils.logWarn("{ItemMap} The material " + blocksParts[0] + " is not a valid material type, please check the wiki on this usage."); }
				} else if (!StringUtils.getUtils().isDouble(blocksParts[1])) {
					ServerUtils.logWarn("{ItemMap} The percentage value for the material " + blocksParts[0] + " is not a valid number, please check the wiki on this usage.");
				} else {
					ServerUtils.logWarn("{ItemMap} An error has occured when trying to set blocks drop for " + itemMap.getConfigName() + ", please check your formatting.");
				}
			}
			itemMap.setBlocksDrop(blocksDrop);
		}
	}
	
   /**
	* Sets the Recipe of the Custom Item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setRecipe(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".recipe") != null) {
			ShapedRecipe shapedRecipe = (ServerUtils.hasSpecificUpdate("1_12") ? new ShapedRecipe(new NamespacedKey(ItemJoin.getInstance(), itemMap.getConfigName()), itemMap.getItem(null)) : LegacyAPI.newShapedRecipe(itemMap.getItem(null)));
			Map < Character, String > ingredientList = new HashMap < Character, String > ();
			String[] shape = itemMap.trimRecipe(itemMap.getNodeLocation().getStringList(".recipe"));
			shapedRecipe.shape(shape);
			if (itemMap.getNodeLocation().getString(".ingredients") != null) {
				List < String > ingredients = itemMap.getNodeLocation().getStringList(".ingredients");
				for (String ingredient: ingredients) {
					String[] ingredientParts = ingredient.split(":");
					Material material = ItemHandler.getMaterial(ingredientParts[1], null);
					if (material != null) {
						char character = 'X';
						try { character = ingredientParts[0].charAt(0); } 
						catch (Exception e) { ServerUtils.logWarn("{ItemMap} The character " + ingredientParts[0] + " for the custom recipe defined for the item " + itemMap.getConfigName() + " is not a valid character!"); }
						shapedRecipe.setIngredient(character, material);
						ingredientList.put(character, material.name());
					} else if (ConfigHandler.getConfig().getItemSection(ingredientParts[1]) != null) {
						SchedulerUtils.runLater(40L, () -> {
							if (ItemUtilities.getUtilities().getItemMap(null, ingredientParts[1], null) != null) {
								final ItemStack itemStack = ItemUtilities.getUtilities().getItemMap(null, ingredientParts[1], null).getItem(null);
								char character = 'X';
								try { character = ingredientParts[0].charAt(0); } 
								catch (Exception e) { ServerUtils.logWarn("{ItemMap} The character " + ingredientParts[0] + " for the custom recipe defined for the item " + itemMap.getConfigName() + " is not a valid character!"); }
								shapedRecipe.setIngredient(character, itemStack.getType());
								ingredientList.put(character, ingredientParts[1]);
								if (!StringUtils.getUtils().isRegistered(Recipes.class.getSimpleName())) {
									ItemJoin.getInstance().getServer().getPluginManager().registerEvents(new Recipes(), ItemJoin.getInstance());
								}
							} else { ServerUtils.logWarn("{ItemMap} The material " + ingredientParts[1] + " for the custom recipe defined for the item " + itemMap.getConfigName() + " is not a proper material type OR custom item node!"); }
						});
					} else { ServerUtils.logWarn("{ItemMap} The material " + ingredientParts[1] + " for the custom recipe defined for the item " + itemMap.getConfigName() + " is not a proper material type OR custom item node!"); }
				}
				SchedulerUtils.runLater(45L, () -> Bukkit.getServer().addRecipe(shapedRecipe));
				itemMap.setIngredients(ingredientList);
			} else { ServerUtils.logWarn("{ItemMap} There is a custom recipe defined for the item " + itemMap.getConfigName() + " but it still needs ingredients defined!"); }
		}
	}

   /**
	* Sets the Skull Owner of the Custom Skull Item,
	* adding the Texture of the Skull Owner to the item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
	private void setSkull(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".skull-owner") != null) {
			if (itemMap.getMaterial().toString().equalsIgnoreCase("SKULL_ITEM") || itemMap.getMaterial().toString().equalsIgnoreCase("PLAYER_HEAD")) {
				if (itemMap.getNodeLocation().getString(".skull-texture") != null) { ServerUtils.logWarn("{ItemMap} You cannot define a skull owner and a skull texture at the same time, remove one from the item."); return;  }
				String owner = this.getActualOwner(itemMap);
				itemMap.setSkull(owner);
			}
		}
	}
	
   /**
	* Gets the exact skull owner value to be added to the ItemMap.
	* 
	* @param itemMap - The ItemMap being modified.
	* @return The found skull owner.
	*/
	private String getActualOwner(final ItemMap itemMap) {
		ConfigurationSection ownerSection = ConfigHandler.getConfig().getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".skull-owner");
		String owner = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".skull-owner"));
		if (ownerSection != null) {
			List<String> owners = new ArrayList<String>();
			for (String ownerKey : ownerSection.getKeys(false)) {
				String ownerList = itemMap.getNodeLocation().getString(".skull-owner." + ownerKey);
				if (ownerList != null) {
					owners.add(ownerList);
				}
			}
			itemMap.setDynamicOwners(owners);
			return ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".skull-owner." + ownerSection.getKeys(false).iterator().next()));
		}
		if (owner != null && !owner.isEmpty()) {
			if (itemMap.isDynamic() || itemMap.isAnimated()) {
				List<String> owners = new ArrayList<String>(); owners.add(owner);
				itemMap.setDynamicOwners(owners);
			}
		}
		return owner;
	}

   /**
	* Sets the Skull Texture of the Custom Item,
	* adding the Custom Skull Texture to the item.
	* 
	* @param itemMap - The ItemMap being modified.
	*/
    private void setSkullTexture(final ItemMap itemMap) {
    	if (ServerUtils.hasSpecificUpdate("1_8") && itemMap.getNodeLocation().getString(".skull-texture") != null) {
    		if (itemMap.getMaterial().toString().equalsIgnoreCase("SKULL_ITEM") || itemMap.getMaterial().toString().equalsIgnoreCase("PLAYER_HEAD")) {
				if (itemMap.getNodeLocation().getString(".skull-owner") != null) { ServerUtils.logWarn("{ItemMap} You cannot define a skull owner and a skull texture at the same time, remove one from the item."); return;  }
    			String texture = getActualTexture(itemMap);
    			if (!texture.contains("hdb-")) {
    				GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
    				gameProfile.getProperties().put("textures", new Property("textures", new String(texture)));
    				try {
    					itemMap.setSkullTexture(texture);
    				} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
    			}
    		}
    	}
    }
 
    /**
 	* Sets the Consumable Potion Effects of the Custom Item,
 	* adding the Custom Consumable Potion Effects to the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setConsumableEffects(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".potion-effect") != null && itemMap.getMaterial().toString().equalsIgnoreCase("GOLDEN_APPLE")) {
			String potionList = itemMap.getNodeLocation().getString(".potion-effect").replace(" ", "");
			List < PotionEffect > potionEffectList = new ArrayList < PotionEffect > ();
			for (String potion: potionList.split(",")) {
				String[] potionSection = potion.split(":");
				PotionEffectType type = PotionEffectType.getByName(potionSection[0].toUpperCase());
				if (PotionEffectType.getByName(potionSection[0].toUpperCase()) != null) {
					try {
						int duritation = 1;
						int amplifier = 1;
						if (StringUtils.getUtils().containsIgnoreCase(potion, ":")) {
							if (Integer.parseInt(potionSection[1]) == 1 || Integer.parseInt(potionSection[1]) == 2 || Integer.parseInt(potionSection[1]) == 3) {
								amplifier = Integer.parseInt(potionSection[1]) - 1;
							} else { amplifier = Integer.parseInt(potionSection[1]); }
						}
						duritation = Integer.parseInt(potionSection[2]) * 20;
						potionEffectList.add(new PotionEffect(type, duritation, amplifier));
					} catch (NumberFormatException e) {
						ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + potionSection[1] + " is not a number and a number was expected.");
						ServerUtils.logWarn("{ItemMap} Consumable Potion: " + potionSection[0] + " will now be set to level 1.");
						ServerUtils.sendDebugTrace(e);
					}
				} else {
					ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + potionSection[0] + " is an incorrect potion effect for the consumable.");
					ServerUtils.logWarn("{ItemMap} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct potion effects.");
				}
			}
			itemMap.setCustomConsumable(true);
			itemMap.setPotionEffect(potionEffectList);
		}
	}

    /**
 	* Sets the Potion Effects of the Custom Item,
 	* adding the Custom Potion Effects to the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setPotionEffects(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".potion-effect") != null) {
			if (itemMap.getMaterial().toString().equalsIgnoreCase("POTION") || itemMap.getMaterial().toString().equalsIgnoreCase("SPLASH_POTION")
				|| ServerUtils.hasSpecificUpdate("1_9") && itemMap.getMaterial().toString().equalsIgnoreCase("LINGERING_POTION")) {
				String potionList = itemMap.getNodeLocation().getString(".potion-effect").replace(" ", "");
				List <PotionEffect> potionEffectList = new ArrayList<PotionEffect>();
				for (String potion: potionList.split(",")) {
					String[] potionSection = potion.split(":");
					PotionEffectType type = PotionEffectType.getByName(potionSection[0].toUpperCase());
					if (PotionEffectType.getByName(potionSection[0].toUpperCase()) != null) {
						try {
							int duritation = 1; int amplifier = 1;
							if (StringUtils.getUtils().containsIgnoreCase(potion, ":")) {
								if (Integer.parseInt(potionSection[1]) == 1 || Integer.parseInt(potionSection[1]) == 2 || Integer.parseInt(potionSection[1]) == 3) { amplifier = Integer.parseInt(potionSection[1]) - 1; } 
								else { amplifier = Integer.parseInt(potionSection[1]); }
							}
							duritation = Integer.parseInt(potionSection[2]) * 20;
							potionEffectList.add(new PotionEffect(type, duritation, amplifier));
						} catch (NumberFormatException e) {
							ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + potionSection[1] + " is not a number and a number was expected.");
							ServerUtils.logWarn("{ItemMap} Custom Potion: " + potionSection[0] + " will now be set to level 1.");
							ServerUtils.sendDebugTrace(e);
						}
					} else {
						ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + potionSection[0] + " is an incorrect potion effect for the custom potion.");
						ServerUtils.logWarn("{ItemMap} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct potion effects.");
					}
				}
				itemMap.setPotionEffect(potionEffectList);
			}
		}
	}
 
    /**
 	* Sets the Tipped Arrow Effects of the Custom Item,
 	* adding the Custom Tipped Arrow Effects to the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setTippedArrows(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".potion-effect") != null) {
			if (ServerUtils.hasSpecificUpdate("1_9") && !ItemJoin.getInstance().getServer().getVersion().contains("(MC: 1.9)") && itemMap.getMaterial().toString().equalsIgnoreCase("TIPPED_ARROW")) {
				String effectList = itemMap.getNodeLocation().getString(".potion-effect").replace(" ", "");
				List <PotionEffect> potionEffectList = new ArrayList<PotionEffect>();
				for (String effect: effectList.split(",")) {
					String[] tippedSection = effect.split(":");
					PotionEffectType type = PotionEffectType.getByName(tippedSection[0].toUpperCase());
					if (PotionEffectType.getByName(tippedSection[0].toUpperCase()) != null) {
						try {
							int level = 1; int duration;
							if (StringUtils.getUtils().containsIgnoreCase(effect, ":")) {
								if (Integer.parseInt(tippedSection[1]) == 1 || Integer.parseInt(tippedSection[1]) == 2 || Integer.parseInt(tippedSection[1]) == 3) { level = Integer.parseInt(tippedSection[1]) - 1; } 
								else { level = Integer.parseInt(tippedSection[1]); }
							}
							duration = Integer.parseInt(tippedSection[2]);
							potionEffectList.add(new PotionEffect(type, duration * 160, level));
						} catch (NumberFormatException e) {
							ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + tippedSection[1] + " is not a number and a number was expected.");
							ServerUtils.logWarn("{ItemMap} Tipped Effect: " + tippedSection[0] + " will now be set to level 1.");
							ServerUtils.sendDebugTrace(e);
						}
					} else {
						ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + tippedSection[0] + " is an incorrect potion effect for the tipped arrow.");
						ServerUtils.logWarn("{ItemMap} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct potion effects.");
					}
				}
				itemMap.setPotionEffect(potionEffectList);
			}
		}
	}

    /**
 	* Sets the Banner Patterns of the Custom Item,
 	* adding the Custom Banner Patterns to the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setBanners(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".banner-meta") != null && ServerUtils.hasSpecificUpdate("1_8") && StringUtils.getUtils().containsIgnoreCase(itemMap.getMaterial().toString(), "BANNER")) {
			String bannerList = itemMap.getNodeLocation().getString(".banner-meta").replace(" ", "");
			List <Pattern> patterns = new ArrayList <Pattern> ();
			for (String banner: bannerList.split(",")) {
				String[] bannerSection = banner.split(":");
				DyeColor Color = null;
				PatternType Pattern = null;
				try {
					Color = DyeColor.valueOf(bannerSection[0].toUpperCase());
					Pattern = PatternType.valueOf(bannerSection[1].toUpperCase());
				} catch (Exception e) { }
				if (Color != null && Pattern != null) {
					patterns.add(new Pattern(Color, Pattern));
				} else if (Color == null) {
					ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + bannerSection[0] + " is an incorrect dye color.");
					ServerUtils.logWarn("{ItemMap} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for a list of correct dye colors.");
				} else if (Pattern == null && banner.contains(":")) {
					ServerUtils.logSevere("{ItemMap} An error occurred in the config, " + bannerSection[1] + " is an incorrect pattern type.");
					ServerUtils.logWarn("{ItemMap} Please see: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/block/banner/PatternType.html for a list of correct pattern types.");
				}
			}
			itemMap.setBannerPatterns(patterns);
		}
	}

    /**
 	* Sets the Firework Effects of the Custom Item,
 	* adding the Custom Firework Effects to the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setFireworks(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".firework") != null) {
			if (itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK") || itemMap.getMaterial().toString().equalsIgnoreCase("FIREWORK_ROCKET")) {
				if (itemMap.getNodeLocation().getString(".firework.type") != null) {
					String stringType = itemMap.getNodeLocation().getString(".firework.type").toUpperCase();
					boolean flicker = itemMap.getNodeLocation().getBoolean(".firework.flicker");
					boolean trail = itemMap.getNodeLocation().getBoolean(".firework.trail");
					Type buildType = Type.valueOf(stringType);
					List <Color> colors = new ArrayList <Color> (); List <DyeColor> saveColors = new ArrayList <DyeColor> ();
					if (itemMap.getNodeLocation().getString(".firework.colors") != null) {
						String colorlist = itemMap.getNodeLocation().getString(".firework.colors").replace(" ", "");
						for (String color: colorlist.split(",")) {
							try { colors.add(DyeColor.valueOf(color.toUpperCase()).getFireworkColor()); saveColors.add(DyeColor.valueOf(color.toUpperCase())); } 
							catch (Exception e) {
								ServerUtils.logSevere("{ItemMap} The item " + itemMap.getConfigName() + " has the incorrect dye color " + color.toUpperCase() + " and does not exist.");
								ServerUtils.logWarn("{ItemMap} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for a list of correct dye color names.");
							}
						}
					} else if (itemMap.getNodeLocation().getString(".firework.colors") == null) {
						colors.add(DyeColor.valueOf("WHITE").getFireworkColor());
						saveColors.add(DyeColor.valueOf("WHITE"));
					}
					FireworkEffect effect = FireworkEffect.builder().trail(trail).flicker(flicker).withColor(colors).withFade(colors).with(buildType).build();
					itemMap.setFirework(effect); itemMap.setFireworkType(buildType); itemMap.setFireworkColor(saveColors); itemMap.setFireworkTrail(trail); itemMap.setFireworkFlicker(flicker);
				}
				int power = itemMap.getNodeLocation().getInt(".firework.power"); if (power == 0) { power = 1; }
				itemMap.setFireworkPower(power);
			}
		}
	}

    /**
 	* Sets the Firework Charge Color of the Custom Item,
 	* adding the Custom Firework Charge Color to the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setFireChargeColor(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".charge-color") != null) {
			if (StringUtils.getUtils().containsIgnoreCase(itemMap.getMaterial().toString(), "CHARGE") || StringUtils.getUtils().containsIgnoreCase(itemMap.getMaterial().toString(), "STAR")) {
				String color = itemMap.getNodeLocation().getString(".charge-color").toUpperCase();
				itemMap.setChargeColor(DyeColor.valueOf(color));
			}
		}
	}

    /**
 	* Sets the Dye Color of the Custom Item,
 	* changing the Custom Dye Color of the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setDye(final ItemMap itemMap) {
		if (itemMap.getNodeLocation().getString(".leather-color") != null) {
			if (itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_HELMET") || itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_CHESTPLATE")
				|| itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_LEGGINGS") || itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_BOOTS")) {
				String leatherColor = itemMap.getNodeLocation().getString(".leather-color").toUpperCase();
				try { 
					if (leatherColor.startsWith("#")) { 
						itemMap.setLeatherHex(leatherColor); 
					} else { 
						boolean hexValue = true;
						for (DyeColor color: DyeColor.values()) {
							if (color.name().replace(" ", "").equalsIgnoreCase(leatherColor)) {
								itemMap.setLeatherColor(leatherColor); 
								hexValue = false;
								break;
							}
						}
						if (hexValue) { itemMap.setLeatherHex(leatherColor); }
					} 
				} catch (Exception ex) { 
					ServerUtils.logSevere("{ItemMap} The leather-color: " + leatherColor + " is not a valid color for the item " + itemMap.getConfigName() + "."); 
					ServerUtils.logWarn("{ItemMap} Use hexcolor or see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for valid bukkit colors."); 
				}
			}
		}
	}

    /**
 	* Sets the Author of the Custom Book Item,
 	* defining the author of the book item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setBookAuthor(final ItemMap itemMap) {
		if (itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK")) {
			if (itemMap.getNodeLocation().getString(".author") != null) {
				itemMap.setAuthor(itemMap.getNodeLocation().getString(".author"));
			} else {
				itemMap.setAuthor("&f");
			}
		}
	}
	
    /**
 	* Sets the Title of the Custom Book Item,
 	* defining the custom title of the book item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setBookTitle(final ItemMap itemMap) {
		if (itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK")) {
			if (itemMap.getNodeLocation().getString(".title") != null) {
				itemMap.setTitle(itemMap.getNodeLocation().getString(".title"));
			} else {
				itemMap.setTitle("&f");
			}
		}
	}

    /**
 	* Sets the Generation of the Custom Book Item,
 	* defining the custom generation of the book item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setBookGeneration(final ItemMap itemMap) {
		if (ServerUtils.hasSpecificUpdate("1_10") && itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK")) {
			if (itemMap.getNodeLocation().getString(".generation") != null) {
				itemMap.setGeneration(org.bukkit.inventory.meta.BookMeta.Generation.valueOf(itemMap.getNodeLocation().getString(".generation")));
			} else {
				itemMap.setGeneration(org.bukkit.inventory.meta.BookMeta.Generation.ORIGINAL);
			}
		}
	}

    /**
 	* Sets the Legacy Book Pages of the Custom Book Item,
 	* adding the custom book pages to the item without any JSON Formatting.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setLegacyBookPages(final ItemMap itemMap) {
		ConfigurationSection pagesSection = ConfigHandler.getConfig().getFile("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".pages");
		if (!ServerUtils.hasSpecificUpdate("1_8") && itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK") 
			&& itemMap.getNodeLocation().getString(".pages") != null && pagesSection != null) {
			List < String > formattedPages = new ArrayList < String > ();
			List<List <String> > rawPages = new ArrayList<List <String> >();
			for (String pageString: pagesSection.getKeys(false)) {
				List < String > pageList = itemMap.getNodeLocation().getStringList(".pages." + pageString);
				rawPages.add(pageList);
				String saveList = "";
				for (int k = 0; k < pageList.size(); k++) {
					String formatLine = pageList.get(k);
					if (this.containsJSONEvent(formatLine)) {
						for (JSONEvent jsonType: JSONEvent.values()) {
							Matcher matchPattern = java.util.regex.Pattern.compile(jsonType.matchType + "(.*?)>").matcher(pageList.get(k));
							while (matchPattern.find()) {
								String inputResult = matchPattern.group(1);
								formatLine = formatLine.replace(jsonType.matchType + inputResult + ">", ((jsonType == JSONEvent.TEXT) ? inputResult : ""));
							}
						}
					} else if (formatLine.contains("raw:")) { formatLine = new String(); }
					saveList = saveList + formatLine + "\n";
				}
				formattedPages.add(saveList);
			}
			itemMap.setPages(formattedPages);
			itemMap.setListPages(rawPages);
		}
	}

    /**
 	* Sets the Custom Attributes for the Custom Item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setAttributes(final ItemMap itemMap) {
		String[] attributes = null;
		String val = itemMap.getNodeLocation().getString(".attributes");
		if (val != null && val.contains("{") && val.contains("}")) { attributes = val.split(","); }
		if (attributes != null && attributes.length != 0) {
			try {
				if (attributes != null) {
					Map < String, Double > attributesList = new HashMap < String, Double > ();
					for (String value: attributes) {
						String[] valueParts = value.replace("{", "").replace("}", "").replace(" ", "").split(":");
						if (StringUtils.getUtils().isInt(valueParts[1]) || StringUtils.getUtils().isDouble(valueParts[1])) {
							attributesList.put(valueParts[0], Double.parseDouble(valueParts[1]));
						} else {
							ServerUtils.logSevere("{ItemMap} There was an issue setting the custom attribute " + valueParts[0] + " for " + itemMap.getConfigName()+ ".");
							ServerUtils.logSevere("{ItemMap} The value " + valueParts[1] + " is not an integer or double value.");
						}
					}
					itemMap.setAttributes(attributesList);
				}
			} catch (Exception e) {
				ServerUtils.logSevere("{ItemMap} An error has occurred when setting custom attributes for " + itemMap.getConfigName()+ ".");
				ServerUtils.logSevere("{ItemMap} The attributes should look like '{GENERIC_ARMOR:10}, {GENERIC_ARMOR_TOUGHNESS:8}' or \"{GENERIC_ARMOR:10}, {GENERIC_ARMOR_TOUGHNESS:8}\".");
			}
		}
	}
	
    /**
 	* Sets the Attributes of the Custom Item,
 	* shows or hides the Attributes of the item.
 	* 
 	* @param itemMap - The ItemMap being modified.
 	*/
	private void setAttributeFlags(final ItemMap itemMap) {
		if (ServerUtils.hasSpecificUpdate("1_8") && StringUtils.getUtils().containsIgnoreCase(itemMap.getItemFlags(), "hide-attributes")) {
			itemMap.setAttributesInfo(true);
		}
	}
	
   /**
	* Defines the JSONEvents for their action, event, and matchType.
	* 
	*/
	private enum JSONEvent {
		TEXT("nullEvent", "text", "<text:"),
		SHOW_TEXT("hoverEvent", "show_text", "<show_text:"),
		OPEN_URL("clickEvent", "open_url", "<open_url:"),
		RUN_COMMAND("clickEvent", "run_command", "<run_command:"),
		CHANGE_PAGE("clickEvent", "change_page", "<change_page:");
		private final String event;
		private final String action;
		private final String matchType;
		private JSONEvent(String Event, String Action, String MatchType) {
			this.event = Event;
			this.action = Action;
			this.matchType = MatchType;
		}
	}
	
   /**
    * Gets the instance of the ItemDesigner.
    * 
    * @param regen - If a new instance is expected.
    * @return The ItemDesigner instance.
    */
    public static ItemDesigner getDesigner(final boolean regen) { 
        if (designer == null || regen == true) { designer = new ItemDesigner(); }
        return designer; 
    } 
}