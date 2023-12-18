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

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.RockinChaos.core.handlers.ItemHandler;
import me.RockinChaos.core.handlers.ItemHandler.JSONEvent;
import me.RockinChaos.core.utils.ReflectionUtils;
import me.RockinChaos.core.utils.ReflectionUtils.MinecraftMethod;
import me.RockinChaos.core.utils.SchedulerUtils;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.api.LegacyAPI;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.PluginData;
import me.RockinChaos.itemjoin.utils.images.Renderer;
import me.RockinChaos.itemjoin.utils.sql.DataObject;
import me.RockinChaos.itemjoin.utils.sql.DataObject.Table;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

public class ItemDesigner {

    /**
     * Creates a new ItemDesigner instance.
     */
    public ItemDesigner() {
        if (ItemJoin.getCore().isStarted()) {
            SchedulerUtils.runAsyncLater(2L, this::registerItems);
        } else {
            SchedulerUtils.run(this::registerItems);
        }
    }

    /**
     * Attempts to register any found items in the items.yml converting them to memory using an ItemMap.
     */
    public void registerItems() {
        final ConfigurationSection itemsList = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection("items");
        if (itemsList != null) {
            for (String internalName : itemsList.getKeys(false)) {
                ConfigurationSection itemNode = itemsList.getConfigurationSection(internalName);
                if (this.isConfigurable(internalName, itemNode) && itemNode != null) {
                    final String itemSlot = itemNode.getString(".slot");
                    String slotList = ((itemSlot != null && !itemSlot.isEmpty()) ? itemSlot : "ARBITRARY");
                    String[] slots = slotList.replace(" ", "").split(",");
                    for (String slot : slots) {
                        if (slot.startsWith("C[") || slot.startsWith("C(")) {
                            slot = slot.replace("C", "CRAFTING");
                        }
                        if (slot.startsWith("CRAFTING")) {
                            slot = slot.replace("(", "[").replace(")", "]");
                        }
                        if (this.isDefinable(internalName, slot)) {
                            ItemMap itemMap = new ItemMap(internalName, slot);

                            this.setMaterial(itemMap);
                            this.setSkullDatabase(itemMap);
                            this.setUnbreaking(itemMap);
                            this.durabilityBar(itemMap);
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
                            this.setTrim(itemMap);
                            this.setFireworks(itemMap);
                            this.setFireChargeColor(itemMap);
                            this.setDye(itemMap);
                            this.setBookAuthor(itemMap);
                            this.setBookTitle(itemMap);
                            this.setBookGeneration(itemMap);
                            this.setAttributes(itemMap);
                            this.setAttributeFlags(itemMap);
                            this.setEnchantmentFlags(itemMap);
                            this.setFlags(itemMap);
                            this.setProbability(itemMap);
                            this.setMobsDrop(itemMap);
                            this.setBlocksDrop(itemMap);
                            this.setRecipe(itemMap);

                            itemMap.setContents();
                            ItemUtilities.getUtilities().addItem(itemMap);
                            ItemUtilities.getUtilities().addCraftingItem(itemMap);
                            PluginData.getInfo().registerListeners(itemMap);
                        }
                    }
                }
            }
            SchedulerUtils.runLater(8L, () -> ItemUtilities.getUtilities().updateItems());
        } else {
            ServerUtils.logWarn("{ItemDesigner} There are no items detected in the items.yml.");
            ServerUtils.logWarn("{ItemDesigner} Try adding an item to the items section in the items.yml.");
        }
    }

    /**
     * Determines if the specific item node has a valid Material ID
     * defined as well as if the item node has a slot defined.
     *
     * @param internalName - The node name of the item.
     * @param itemNode     - The item node location.
     * @return If the material is valid.
     */
    private boolean isConfigurable(final String internalName, final ConfigurationSection itemNode) {
        String id = this.getMaterial(itemNode);
        String dataValue = null;
        if (id != null) {
            if (id.contains(":")) {
                String[] parts = id.split(":");
                id = parts[0];
                dataValue = parts[1];
                if (ServerUtils.hasSpecificUpdate("1_13")) {
                    ServerUtils.logWarn("{ItemDesigner} The item " + internalName + " is using a Legacy Material which is no longer supported as of Minecraft 1.13.");
                    ServerUtils.logWarn("{ItemDesigner} This will cause issues, please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html for a list of material names.");
                }
            }
            if (!ServerUtils.hasSpecificUpdate("1_9") && id.equalsIgnoreCase("TIPPED_ARROW") || id.equalsIgnoreCase("440") || id.equalsIgnoreCase("0440")) {
                ServerUtils.logSevere("{ItemDesigner} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have the item TIPPED_ARROW.");
                ServerUtils.logWarn("{ItemDesigner} You are receiving this notice because the item(s) exists in your items.yml and will not be set, please remove the item(s) or update your server.");
                return false;
            } else if (!ServerUtils.hasSpecificUpdate("1_9") && id.equalsIgnoreCase("LINGERING_POTION") || id.equalsIgnoreCase("441") || id.equalsIgnoreCase("0441")) {
                ServerUtils.logSevere("{ItemDesigner} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have the item LINGERING_POTION.");
                ServerUtils.logWarn("{ItemDesigner} You are receiving this notice because the item(s) exists in your items.yml and will not be set, please remove the item(s) or update your server.");
                return false;
            } else if (ItemHandler.getMaterial(id, dataValue) == Material.AIR) {
                ServerUtils.logSevere("{ItemDesigner} The Item " + internalName + "'s Material 'ID' is invalid or does not exist.");
                ServerUtils.logWarn("{ItemDesigner} The Item " + internalName + " will not be set!");
                if (StringUtils.isInt(id)) {
                    ServerUtils.logSevere("{ItemDesigner} If you are using a numerical id and a numerical dataValue.");
                    ServerUtils.logSevere("{ItemDesigner} Include quotations or apostrophes at the beginning and the end or this error will persist, the id should look like '160:15' or \"160:15\".");
                }
                return false;
            }
        } else {
            ServerUtils.logSevere("{ItemDesigner} The Item" + internalName + " does not have a Material ID defined.");
            ServerUtils.logWarn("{ItemDesigner} The Item " + internalName + " will not be set!");
            return false;
        }
        return true;
    }

    /**
     * Determines if the specific item node has an actual ItemJoin
     * definable slot, being a custom slot or a true integer slot.
     *
     * @param internalName - The node name of the item.
     * @param slot         - The slot of the item.
     * @return If the slot is valid.
     */
    private boolean isDefinable(final String internalName, final String slot) {
        if (!StringUtils.isInt(slot) && !ItemHandler.isCustomSlot(slot)) {
            ServerUtils.logSevere("{ItemDesigner} The Item " + internalName + "'s slot is invalid or does not exist.");
            ServerUtils.logWarn("{ItemDesigner} The Item " + internalName + " will not be set!");
            return false;
        } else if (StringUtils.isInt(slot)) {
            int parseSlot = Integer.parseInt(slot);
            if (!(parseSlot >= 0 && parseSlot <= 35)) {
                ServerUtils.logSevere("{ItemDesigner} The Item " + internalName + "'s slot must be between 0 and 35.");
                ServerUtils.logWarn("{ItemDesigner} The Item " + internalName + " will not be set!");
                return false;
            }
        } else if (!ServerUtils.hasSpecificUpdate("1_9") && slot.equalsIgnoreCase("Offhand")) {
            ServerUtils.logWarn("{ItemDesigner} Your server is running MC " + ReflectionUtils.getServerVersion() + " and this version of Minecraft does not have OFFHAND support!");
            return false;
        }
        return true;
    }

    /**
     * Gets the material as defined in the ConfigurationSection.
     *
     * @param itemNode - The node ConfigurationSection (location) of the config section.
     * @return The material exactly as found in the Configuration Section, without any animation delay.
     */
    public String getMaterial(final ConfigurationSection itemNode) {
        final ConfigurationSection matList = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemNode.getCurrentPath() + ".id");
        String material = ItemHandler.cutDelay(itemNode.getString(".id"));
        if (matList != null) {
            material = ItemHandler.cutDelay(itemNode.getString(".id." + matList.getKeys(false).iterator().next()));
            return material;
        }
        return material;
    }

    /**
     * Sets the Custom Material to the Custom Item.
     *
     * @param itemMap - The ItemMap being modified.
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
        final ConfigurationSection matList = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".id");
        String material = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".id"));
        if (matList != null) {
            List<String> materials = new ArrayList<>();
            for (String materialKey : matList.getKeys(false)) {
                String materialList = itemMap.getNodeLocation().getString(".id." + materialKey);
                if (materialList != null) {
                    materials.add(materialList);
                }
            }
            itemMap.setDynamicMaterials(materials);
            material = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".id." + matList.getKeys(false).iterator().next()));
            final Material mat = ItemHandler.getMaterial(material, null);
            if (material.contains(":") && !mat.name().equalsIgnoreCase("PLAYER_HEAD")) {
                String[] parts = material.split(":");
                itemMap.setDataValue((short) Integer.parseInt(parts[1]));
            }
            return mat;
        }
        final Material mat = ItemHandler.getMaterial(material, null);
        if (material.contains(":") && !mat.name().equalsIgnoreCase("PLAYER_HEAD")) {
            String[] parts = material.split(":");
            itemMap.setDataValue((short) Integer.parseInt(parts[1]));
        }
        return mat;
    }

    /**
     * Sets the HeadDatabase Texture to the Custom Skull Item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setSkullDatabase(final ItemMap itemMap) {
        if (ItemJoin.getCore().getDependencies().databaseEnabled() && itemMap.getNodeLocation().getString(".skull-texture") != null) {
            if (itemMap.getMaterial().toString().equalsIgnoreCase("SKULL_ITEM") || itemMap.getMaterial().toString().equalsIgnoreCase("PLAYER_HEAD")) {
                if (itemMap.getNodeLocation().getString(".skull-owner") != null) {
                    ServerUtils.logWarn("{ItemDesigner} You cannot define a skull owner and a skull texture at the same time, remove one from the item.");
                    return;
                }
                String skullTexture = getActualTexture(itemMap);
                if (skullTexture.contains("hdb-")) {
                    try {
                        itemMap.setSkullTexture(skullTexture.replace("hdb-", ""));
                        itemMap.setHeadDatabase(true);
                    } catch (NullPointerException e) {
                        ServerUtils.logSevere("{ItemDesigner} HeadDatabaseAPI could not find #" + skullTexture + ", this head does not exist.");
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
        ConfigurationSection textureSection = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".skull-texture");
        String texture = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".skull-texture"));
        if (textureSection != null) {
            List<String> textures = new ArrayList<>();
            for (String textureKey : textureSection.getKeys(false)) {
                String textureList = itemMap.getNodeLocation().getString(".skull-texture." + textureKey);
                if (textureList != null) {
                    if (StringUtils.containsIgnoreCase(textureList, "url-")) {
                        final String delay = (ItemHandler.getDelayFormat(textureList) != null ? ItemHandler.getDelayFormat(textureList) : "");
                        textureList = delay + StringUtils.toTexture64(textureList);
                    }
                    textures.add(textureList);
                }
            }
            itemMap.setDynamicTextures(textures);
            return ItemHandler.cutDelay(textures.get(0));
        } else if (!texture.isEmpty()) {
            if (itemMap.isDynamic() || itemMap.isAnimated()) {
                List<String> textures = new ArrayList<>();
                if (StringUtils.containsIgnoreCase(texture, "url-")) {
                    final String delay = (ItemHandler.getDelayFormat(texture) != null ? ItemHandler.getDelayFormat(texture) : "");
                    texture = delay + StringUtils.toTexture64(texture);
                }
                textures.add(texture);
                itemMap.setDynamicTextures(textures);
            } else {
                if (StringUtils.containsIgnoreCase(texture, "url-")) {
                    final String delay = (ItemHandler.getDelayFormat(texture) != null ? ItemHandler.getDelayFormat(texture) : "");
                    texture = delay + StringUtils.toTexture64(texture);
                }
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
        if (StringUtils.containsIgnoreCase(itemMap.getItemFlags(), "unbreakable")) {
            try {
                itemMap.setUnbreakable(true);
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Hides the Durability Bar of the Custom Item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void durabilityBar(final ItemMap itemMap) {
        if (StringUtils.containsIgnoreCase(itemMap.getItemFlags(), "hide-durability")) {
            try {
                itemMap.setDurabilityBar(true);
            } catch (Exception e) {
                ServerUtils.sendDebugTrace(e);
            }
        }
    }

    /**
     * Sets the Custom Map Image to the Custom Item,
     * draws the specified map image on the items canvas.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setMapImage(final ItemMap itemMap) {
        if (itemMap.getNodeLocation().getString(".custom-map-image") != null && StringUtils.containsIgnoreCase(itemMap.getMaterial().toString(), "MAP")) {
            if (itemMap.getNodeLocation().getString(".map-id") != null && StringUtils.isInt(itemMap.getNodeLocation().getString(".map-id"))) {
                itemMap.setMapID(itemMap.getNodeLocation().getInt(".map-id"));
            }
            itemMap.setMapImage(itemMap.getNodeLocation().getString(".custom-map-image"));
            if (itemMap.getMapImage().equalsIgnoreCase("default.jpg") || new File(ItemJoin.getCore().getPlugin().getDataFolder(), itemMap.getMapImage()).exists()) {
                DataObject dataObject = (DataObject) ItemJoin.getCore().getSQL().getData(new DataObject(Table.MAP_IDS, null, null, itemMap.getMapImage(), null));
                if (dataObject != null && (itemMap.getMapID() == -1 || (itemMap.getMapID() == Integer.parseInt(dataObject.getMapID())))) {
                    int mapID = Integer.parseInt(dataObject.getMapID());
                    MapRenderer imgPlatform = this.createRenderer(itemMap.getMapImage(), mapID);
                    MapView view = ItemHandler.existingView(mapID);
                    itemMap.setMapID(mapID);
                    itemMap.setMapView(view);
                    try {
                        view.removeRenderer(view.getRenderers().get(0));
                    } catch (NullPointerException e) {
                        ServerUtils.sendDebugTrace(e);
                    }
                    try {
                        view.addRenderer(imgPlatform);
                    } catch (NullPointerException e) {
                        ServerUtils.sendDebugTrace(e);
                    }
                } else {
                    MapView view = LegacyAPI.createMapView();
                    try {
                        view.removeRenderer(view.getRenderers().get(0));
                    } catch (NullPointerException e) {
                        ServerUtils.sendDebugTrace(e);
                    }
                    int mapID = (itemMap.getMapID() != -1 ? itemMap.getMapID() : LegacyAPI.getMapID(view));
                    MapRenderer imgPlatform = this.createRenderer(itemMap.getMapImage(), mapID);
                    itemMap.setMapID(mapID);
                    itemMap.setMapView(view);
                    try {
                        view.addRenderer(imgPlatform);
                    } catch (NullPointerException e) {
                        ServerUtils.sendDebugTrace(e);
                    }
                    ItemJoin.getCore().getSQL().saveData(new DataObject(Table.MAP_IDS, null, null, itemMap.getMapImage(), Integer.toString(itemMap.getMapID())));
                }
            }
        } else if (itemMap.getNodeLocation().getString(".map-id") != null && StringUtils.isInt(itemMap.getNodeLocation().getString(".map-id")) && StringUtils.containsIgnoreCase(itemMap.getMaterial().toString(), "MAP")) {
            itemMap.setMapID(itemMap.getNodeLocation().getInt(".map-id"));
            MapView view = ItemHandler.existingView(itemMap.getMapID());
            itemMap.setMapView(view);
        }
    }

    /**
     * Creates a new MapRenderer.
     *
     * @param image   - The image to be rendered, ex: 'default.jpg'.
     * @param imageID - The id of the MapView.
     * @return The newly created MapRenderer instance.
     */
    public MapRenderer createRenderer(final String image, final int imageID) {
        if (StringUtils.containsIgnoreCase(image, ".GIF")) {
            return new Renderer(image, 0, -1);
        } else {
            return new Renderer(image, imageID);
        }
    }

    /**
     * Sets the NBTData to the Custom Item,
     * designing the item to be unique to ItemJoin.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setNBTData(final ItemMap itemMap) {
        if (ItemJoin.getCore().getData().dataTagsEnabled() && !itemMap.isVanilla() && !itemMap.isVanillaControl() && !itemMap.isVanillaStatus()) {
            try {
                Object tag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
                tag.getClass().getMethod(MinecraftMethod.setString.getMethod(tag, String.class, String.class), String.class, String.class).invoke(tag, "ItemJoin Name", itemMap.getConfigName());
                itemMap.setNewNBTData(itemMap.getConfigName(), tag);
                final String itemProperties = itemMap.getNodeLocation().getString(".properties");
                if (itemProperties != null && !itemProperties.isEmpty()) {
                    List<Object> tags = new ArrayList<>();
                    Map<Object, Object> tagValues = new HashMap<>();
                    String[] properties = itemProperties.split(",");
                    for (String property : properties) {
                        String[] propertyParts = property.split(":");
                        String identifier = (propertyParts[0].startsWith(" ") ? propertyParts[0].substring(1) : propertyParts[0]);
                        Object tagList = null;
                        StringBuilder value = new StringBuilder(propertyParts[1]);
                        for (int i = 2; i < propertyParts.length; i++) {
                            value.append(":").append(propertyParts[i]);
                        }
                        if (value.toString().startsWith("[")) {
                            value = new StringBuilder(value.toString().replace("[", "").replace("]", ""));
                            String[] valueParts = value.toString().split("#");
                            final List<String> listNBT = new ArrayList<>(Arrays.asList(valueParts));
                            tagList = ReflectionUtils.getMinecraftClass("NBTTagList").getConstructor().newInstance();
                            for (String nbt : listNBT) {
                                Object tagString = ReflectionUtils.getMinecraftClass("NBTTagString").getConstructor(String.class).newInstance(nbt);
                                tagList.getClass().getMethod(MinecraftMethod.add.getMethod(tagList, ReflectionUtils.getMinecraftClass("NBTBase")), ReflectionUtils.getMinecraftClass("NBTBase")).invoke(tagList, tagString);
                            }
                        }
                        Object propertyTag = ReflectionUtils.getMinecraftClass("NBTTagCompound").getConstructor().newInstance();
                        propertyTag.getClass().getMethod(MinecraftMethod.setString.getMethod(propertyTag, String.class, String.class), String.class, String.class).invoke(propertyTag, identifier, value.toString());
                        tags.add(propertyTag);
                        if (tagList == null) {
                            tagValues.put(identifier, value.toString());
                        } else {
                            tagValues.put(identifier, tagList);
                        }
                    }
                    itemMap.setNBTProperties(tagValues, tags);
                }
            } catch (Exception e) {
                ServerUtils.logSevere("{ItemDesigner} An error has occurred when setting NBTData to an item.");
                ServerUtils.sendDebugTrace(e);
            }
        } else {
            itemMap.setLegacySecret(itemMap.getNBTFormat());
        }
    }

    /**
     * Sets the Book Pages to the Custom Item,
     * adding the custom book pages to the item in JSON Formatting.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setJSONBookPages(final ItemMap itemMap) {
        ConfigurationSection pagesSection = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".pages");
        if (itemMap.getMaterial().toString().equalsIgnoreCase("WRITTEN_BOOK") && itemMap.getNodeLocation().getString(".pages") != null && pagesSection != null) {
            List<String> JSONPages = new ArrayList<>();
            List<List<String>> rawPages = new ArrayList<>();
            for (String pageString : pagesSection.getKeys(false)) {
                List<String> pageList = itemMap.getNodeLocation().getStringList(".pages." + pageString);
                rawPages.add(pageList);
                StringBuilder textBuilder = new StringBuilder("{\"text\":\"\",\"extra\":[");
                boolean firstIn = true;
                for (String string : pageList) {
                    Map<Integer, String> JSONBuilder = new HashMap<>();
                    String formatLine = string.replace("\n", "\\n");
                    if (ItemHandler.containsJSONEvent(formatLine)) {
                        while (ItemHandler.containsJSONEvent(formatLine)) {
                            for (JSONEvent jsonType : JSONEvent.values()) {
                                Matcher matchPattern = java.util.regex.Pattern.compile(jsonType.matchType + "(.*?)>").matcher(formatLine);
                                if (matchPattern.find()) {
                                    String inputResult = matchPattern.group(1);
                                    JSONBuilder.put(JSONBuilder.size(), ((jsonType != JSONEvent.TEXT)
                                            ? (",\"" + jsonType.event + "\":{\"action\":\"" + jsonType.action + "\",\"value\":\"" + inputResult + "\"}")
                                            : ("," + "{\"" + jsonType.action + "\":\"" + inputResult + "\"")));
                                    formatLine = formatLine.replace(jsonType.matchType + inputResult + ">", "<JSONEvent>");
                                    ItemHandler.safetyCheckURL(itemMap.getConfigName(), jsonType, inputResult);
                                }
                            }
                        }
                        if (!formatLine.isEmpty() && !formatLine.trim().isEmpty()) {
                            boolean definingText = false;
                            String[] JSONEvents = formatLine.split("<JSONEvent>");
                            if (!(org.apache.commons.lang.StringUtils.countMatches(formatLine, "<JSONEvent>") <= JSONEvents.length)) {
                                StringBuilder adjustLine = new StringBuilder();
                                for (String s : formatLine.split("JSONEvent>")) {
                                    adjustLine.append(s).append("JSONEvent> ");
                                }
                                JSONEvents = adjustLine.toString().split("<JSONEvent>");
                            }
                            for (int i = 0; i < JSONEvents.length; i++) {
                                if (!JSONEvents[i].isEmpty() && !JSONEvents[i].isEmpty() && !JSONEvents[i].trim().isEmpty()) {
                                    textBuilder.append((i == 0) ? "," : "},").append("{\"").append("text").append("\":\"").append(JSONEvents[i]).append((JSONBuilder.get(i) != null && JSONBuilder.get(i).contains("\"text\""))
                                            ? "\"}" : "\"").append(JSONBuilder.get(i) != null ? JSONBuilder.get(i) : "");
                                } else if (JSONBuilder.get(i) != null) {
                                    if (JSONBuilder.get(i).contains("\"text\"") && !definingText) {
                                        textBuilder.append(JSONBuilder.get(i));
                                        definingText = true;
                                    } else if (JSONBuilder.get(i).contains("\"text\"") && definingText) {
                                        textBuilder.append("}").append(JSONBuilder.get(i));
                                        definingText = false;
                                    } else {
                                        textBuilder.append(JSONBuilder.get(i));
                                    }
                                }
                            }
                            textBuilder.append("},{\"text\":\"\\n\"}");
                            firstIn = false;
                        }
                    } else if (formatLine.contains("raw:")) {
                        String format = (!firstIn ? "," : "") + formatLine.replace("raw: ", "").replace("raw:", "").replace("[\"\",", "").replace("[\"\"", "").replace("\"bold\":false}]", "\"bold\":false}").replace("\"bold\":true}]", "\"bold\":true}");
                        if (format.endsWith("]")) {
                            format = StringUtils.replaceLast(format, "]", "");
                        }
                        textBuilder.append(format).append(",{\"text\":\"\\n\"}");
                        firstIn = false;
                    } else {
                        textBuilder.append(!firstIn ? "," : "").append("{\"text\":\"").append(formatLine).append("\"},{\"text\":\"\\n\"}");
                        firstIn = false;
                    }
                }
                JSONPages.add(textBuilder + "]}");
            }
            itemMap.setPages(JSONPages);
            itemMap.setListPages(rawPages);
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
        if (ItemJoin.getCore().getData().dataTagsEnabled() || itemMap.isVanilla()) {
            itemMap.setCustomName(name);
        } else {
            itemMap.setCustomName("§f" + name);
        }
    }

    /**
     * Gets the exact name to be set to the Custom Item.
     *
     * @param itemMap - The ItemMap being modified.
     * @return The correctly formatted display name.
     */
    private String getActualName(final ItemMap itemMap) {
        ConfigurationSection nameSection = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".name");
        String name = itemMap.getNodeLocation().getString(".name");
        try {
            ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".name"));
        } catch (Exception ignored) {
        }
        if (nameSection != null) {
            List<String> names = new ArrayList<>();
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
        if (itemMap.isDynamic() || itemMap.isAnimated()) {
            List<String> names = new ArrayList<>();
            names.add(name);
            itemMap.setDynamicNames(names);
        }
        return ItemHandler.cutDelay(name);
    }

    /**
     * Sets the Custom Lore to the Custom Item,
     * adding the custom lore to the items' lore.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setLore(final ItemMap itemMap) {
        if (itemMap.getNodeLocation().getString(".lore") != null) {
            List<String> lore = getActualLore(itemMap);
            itemMap.setCustomLore(lore);
        }
    }

    /**
     * Gets the exact lore to be set to the Custom Item.
     *
     * @param itemMap - The ItemMap being modified.
     * @return The correctly formatted list of displayed lores.
     */
    private List<String> getActualLore(final ItemMap itemMap) {
        ConfigurationSection loreSection = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".lore");
        List<String> lore = itemMap.getNodeLocation().getStringList(".lore");
        if (loreSection != null) {
            List<List<String>> lores = new ArrayList<>();
            for (String loreKey : loreSection.getKeys(false)) {
                List<String> loreList = itemMap.getNodeLocation().getStringList(".lore." + loreKey);
                lores.add(loreList);
            }
            itemMap.setDynamicLores(lores);
            return itemMap.getNodeLocation().getStringList(".lore." + loreSection.getKeys(false).iterator().next());
        }
        if (!lore.isEmpty()) {
            if (itemMap.isDynamic() || itemMap.isAnimated()) {
                List<List<String>> lores = new ArrayList<>();
                lores.add(lore);
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
            itemMap.setModelData(itemMap.getNodeLocation().getString(".model-data"));
        }
    }

    /**
     * Sets the Probability of the Custom Item,
     * defining the probability percentage of the item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setProbability(final ItemMap itemMap) {
        final String probability = itemMap.getNodeLocation().getString(".probability");
        if (probability != null) {
            String percentageString = probability.replace("%", "").replace("-", "").replace(" ", "");
            int percentage = Integer.parseInt(percentageString);
            if (!ItemJoin.getCore().getChances().getItems().containsKey(itemMap)) {
                ItemJoin.getCore().getChances().putItem(itemMap, percentage);
            }
            itemMap.setProbability(percentage);
            if (itemMap.getProbability() == 100) {
                ServerUtils.logWarn("{ItemDesigner} An item cannot be defined with 100 percent probability, please check the wiki on this usage.");
                ServerUtils.logWarn("{ItemDesigner} Please change the probability of the item, or remove it entirely, items may not function.");
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
        Map<EntityType, Double> mobsDrop = new HashMap<>();
        if (itemMap.getNodeLocation().getString(".mobs-drop") != null) {
            List<String> mobs = itemMap.getNodeLocation().getStringList(".mobs-drop");
            for (String mobsLine : mobs) {
                String[] mobsParts = mobsLine.replace(" ", "").split(":");
                if (mobsParts[0] != null && mobsParts[1] != null && StringUtils.isDouble(mobsParts[1])) {
                    EntityType mob = EntityType.valueOf(mobsParts[0].toUpperCase());
                    mobsDrop.put(mob, Double.parseDouble(mobsParts[1]));
                } else if (!StringUtils.isDouble(mobsParts[1])) {
                    ServerUtils.logWarn("{ItemDesigner} The percentage value for the mob " + mobsParts[0] + " is not a valid number, please check the wiki on this usage.");
                } else {
                    ServerUtils.logWarn("{ItemDesigner} An error has occurred when trying to set mobs drop for " + itemMap.getConfigName() + ", please check your formatting.");
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
        Map<Material, Double> blocksDrop = new HashMap<>();
        if (itemMap.getNodeLocation().getString(".blocks-drop") != null) {
            List<String> blocks = itemMap.getNodeLocation().getStringList(".blocks-drop");
            for (String blocksLine : blocks) {
                String[] blocksParts = blocksLine.replace(" ", "").split(":");
                if (blocksParts[0] != null && blocksParts[1] != null && StringUtils.isDouble(blocksParts[1])) {
                    Material block = ItemHandler.getMaterial(blocksParts[0].toUpperCase(), null);
                    if (block != Material.AIR) {
                        blocksDrop.put(block, Double.parseDouble(blocksParts[1]));
                    } else {
                        ServerUtils.logWarn("{ItemDesigner} The material " + blocksParts[0] + " is not a valid material type, please check the wiki on this usage.");
                    }
                } else if (!StringUtils.isDouble(blocksParts[1])) {
                    ServerUtils.logWarn("{ItemDesigner} The percentage value for the material " + blocksParts[0] + " is not a valid number, please check the wiki on this usage.");
                } else {
                    ServerUtils.logWarn("{ItemDesigner} An error has occurred when trying to set blocks drop for " + itemMap.getConfigName() + ", please check your formatting.");
                }
            }
            itemMap.setBlocksDrop(blocksDrop);
        }
    }

    /**
     * Sets the exact recipe.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setRecipe(final ItemMap itemMap) {
        if (itemMap.getNodeLocation().getString(".recipe") != null) {
            ConfigurationSection recipeSection = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".recipe");
            Map<Character, ItemRecipe> ingredientList = new HashMap<>();
            List<String> recipe = itemMap.getNodeLocation().getStringList(".recipe");
            if (recipeSection != null) {
                for (String recipeKey : recipeSection.getKeys(false)) {
                    final List<String> recipeList = itemMap.getNodeLocation().getStringList(".recipe." + recipeKey);
                    if (!recipeList.isEmpty()) {
                        this.addRecipe(itemMap, recipeList, "-" + recipeKey, ingredientList);
                    }
                }
            } else {
                this.addRecipe(itemMap, recipe, "-1", ingredientList);
            }
            itemMap.setIngredients(ingredientList);
        } else {
            itemMap.setRecipe(Arrays.asList('X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'));
        }
    }

    /**
     * Sets the Recipe of the Custom Item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void addRecipe(final ItemMap itemMap, final List<String> recipe, final String identifier, final Map<Character, ItemRecipe> ingredientList) {
        final ShapedRecipe shapedRecipe = (ServerUtils.hasSpecificUpdate("1_12") ? new ShapedRecipe(new NamespacedKey(ItemJoin.getCore().getPlugin(), itemMap.getConfigName() + identifier), itemMap.getItem(null)) : LegacyAPI.newShapedRecipe(itemMap.getItem(null)));
        String[] shape = itemMap.trimRecipe(recipe);
        shapedRecipe.shape(shape);
        if (itemMap.getNodeLocation().getString(".ingredients") != null) {
            List<String> ingredients = itemMap.getNodeLocation().getStringList(".ingredients");
            for (String ingredient : ingredients) {
                String[] ingredientParts = ingredient.split(":");
                int getCount = 1;
                int getData = 0;
                if (ingredientParts.length > 2 && ingredientParts[2].startsWith("#")) {
                    try {
                        getCount = Integer.parseInt(ingredientParts[2].replace("#", ""));
                    } catch (Exception e) {
                        ServerUtils.logWarn("{ItemDesigner} [1] " + ingredientParts[2].replace("#", "") + " is not a valid count!");
                    }
                } else if (ingredientParts.length > 3 && ingredientParts[3].startsWith("#")) {
                    try {
                        getCount = Integer.parseInt(ingredientParts[3].replace("#", ""));
                    } catch (Exception e) {
                        ServerUtils.logWarn("{ItemDesigner} [2] " + ingredientParts[3].replace("#", "") + " is not a valid count!");
                    }
                    try {
                        getData = Integer.parseInt(ingredientParts[2]);
                    } catch (Exception e) {
                        ServerUtils.logWarn("{ItemDesigner} [3] " + ingredientParts[2] + " is not a valid item data!");
                    }
                } else if (ingredientParts.length > 2 && !ingredientParts[2].startsWith("#")) {
                    try {
                        getData = Integer.parseInt(ingredientParts[2]);
                    } catch (Exception e) {
                        ServerUtils.logWarn("{ItemDesigner} [4] " + ingredientParts[2] + " is not a valid item data!");
                    }
                }
                final int count = getCount;
                final int itemData = getData;
                final Material material = ItemHandler.getMaterial(ingredientParts[1], String.valueOf(itemData));
                final ConfigurationSection itemsPath = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection("items");
                if (count >= 1) {
                    try {
                        char character = 'X';
                        try {
                            character = ingredientParts[0].charAt(0);
                        } catch (Exception e) {
                            ServerUtils.logWarn("{ItemDesigner} The character " + ingredientParts[0] + " for the custom recipe defined for the item " + itemMap.getConfigName() + " is not a valid character!");
                        }
                        if (itemData <= 0) {
                            shapedRecipe.setIngredient(character, material);
                        } else {
                            LegacyAPI.setIngredient(shapedRecipe, character, material, (byte) itemData);
                        }
                        ingredientList.put(character, new ItemRecipe(null, material, (byte) itemData, count));
                    } catch (IllegalArgumentException e) {
                        if (!StringUtils.containsIgnoreCase(e.getMessage(), "Symbol does not appear")) {
                            ServerUtils.sendSevereTrace(e);
                        }
                    }
                } else {
                    if (itemsPath != null) {
                        itemsPath.getConfigurationSection(ingredientParts[1]);
                    }
                    ServerUtils.logWarn("{ItemDesigner} The material " + ingredientParts[1] + " for the custom recipe defined for the item " + itemMap.getConfigName() + " is not a proper material type OR custom item node!");
                }
            }
            SchedulerUtils.runLater(45L, () -> {
                try {
                    try {
                        Bukkit.getServer().addRecipe(shapedRecipe);
                    } catch (IllegalStateException ignored) {
                    }
                } catch (NullPointerException e2) {
                    if (e2.getMessage() != null && !e2.getMessage().isEmpty() && e2.getMessage().contains("registry")) {
                        ServerUtils.logWarn("{ItemDesigner} Magma has been detected on the server which currently doesn't support ShapedRecipes.");
                        ServerUtils.logWarn("{ItemDesigner} The recipe for " + itemMap.getConfigName() + " may still continue to function in limited capacity.");
                        ServerUtils.sendDebugTrace(e2);
                    }
                }
            });
        } else {
            ServerUtils.logWarn("{ItemDesigner} There is a custom recipe defined for the item " + itemMap.getConfigName() + " but it still needs ingredients defined!");
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
                if (itemMap.getNodeLocation().getString(".skull-texture") != null) {
                    ServerUtils.logWarn("{ItemDesigner} You cannot define a skull owner and a skull texture at the same time, remove one from the item.");
                    return;
                }
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
        ConfigurationSection ownerSection = ItemJoin.getCore().getConfig("items.yml").getConfigurationSection(itemMap.getNodeLocation().getCurrentPath() + ".skull-owner");
        String owner = ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".skull-owner"));
        if (ownerSection != null) {
            List<String> owners = new ArrayList<>();
            for (String ownerKey : ownerSection.getKeys(false)) {
                String ownerList = itemMap.getNodeLocation().getString(".skull-owner." + ownerKey);
                if (ownerList != null) {
                    owners.add(ownerList);
                }
            }
            itemMap.setDynamicOwners(owners);
            return ItemHandler.cutDelay(itemMap.getNodeLocation().getString(".skull-owner." + ownerSection.getKeys(false).iterator().next()));
        }
        if (!owner.isEmpty()) {
            if (itemMap.isDynamic() || itemMap.isAnimated()) {
                List<String> owners = new ArrayList<>();
                owners.add(owner);
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
        if (itemMap.getNodeLocation().getString(".skull-texture") != null) {
            if (itemMap.getMaterial().toString().equalsIgnoreCase("SKULL_ITEM") || itemMap.getMaterial().toString().equalsIgnoreCase("PLAYER_HEAD")) {
                if (itemMap.getNodeLocation().getString(".skull-owner") != null) {
                    ServerUtils.logWarn("{ItemDesigner} You cannot define a skull owner and a skull texture at the same time, remove one from the item.");
                    return;
                }
                String texture = this.getActualTexture(itemMap);
                if (!StringUtils.containsIgnoreCase(texture, "hdb-")) {
                    final UUID uuid = UUID.randomUUID();
                    GameProfile gameProfile = new GameProfile(uuid, uuid.toString().replaceAll("_", "").replaceAll("-", ""));
                    gameProfile.getProperties().put("textures", new Property("textures", texture));
                    try {
                        itemMap.setSkullTexture(texture);
                    } catch (Exception e) {
                        ServerUtils.sendDebugTrace(e);
                    }
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
        String potionEffect = (itemMap.getNodeLocation().getString(".potion-effect") != null ? itemMap.getNodeLocation().getString(".potion-effect") : itemMap.getNodeLocation().getString(".potion-effects"));
        if (potionEffect != null && (itemMap.getMaterial().isEdible() || ItemHandler.isSkull(itemMap.getMaterial()))) {
            String potionList = potionEffect.replace(" ", "");
            List<PotionEffect> potionEffectList = new ArrayList<>();
            for (String potion : potionList.split(",")) {
                String[] potionSection = potion.split(":");
                PotionEffectType type = LegacyAPI.getEffectByName(potionSection[0]);
                if (type != null) {
                    try {
                        int amplifier = 1;
                        if (StringUtils.containsIgnoreCase(potion, ":")) {
                            if (Integer.parseInt(potionSection[1]) == 1 || Integer.parseInt(potionSection[1]) == 2 || Integer.parseInt(potionSection[1]) == 3) {
                                amplifier = Integer.parseInt(potionSection[1]) - 1;
                            } else {
                                amplifier = Integer.parseInt(potionSection[1]);
                            }
                        }
                        potionEffectList.add(new PotionEffect(type, Integer.parseInt(potionSection[2]) * 20, amplifier));
                    } catch (NumberFormatException e) {
                        ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + potionSection[1] + " is not a number and a number was expected.");
                        ServerUtils.logWarn("{ItemDesigner} Consumable Potion: " + potionSection[0] + " will now be set to level 1.");
                        ServerUtils.sendDebugTrace(e);
                    }
                } else {
                    ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + potionSection[0] + " is an incorrect potion effect for the consumable.");
                    ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct potion effects.");
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
        String potionEffect = (itemMap.getNodeLocation().getString(".potion-effect") != null ? itemMap.getNodeLocation().getString(".potion-effect") : itemMap.getNodeLocation().getString(".potion-effects"));
        if (potionEffect != null) {
            if (itemMap.getMaterial().toString().equalsIgnoreCase("POTION") || itemMap.getMaterial().toString().equalsIgnoreCase("SPLASH_POTION")
                    || ServerUtils.hasSpecificUpdate("1_9") && itemMap.getMaterial().toString().equalsIgnoreCase("LINGERING_POTION")) {
                String potionList = potionEffect.replace(" ", "");
                List<PotionEffect> potionEffectList = new ArrayList<>();
                for (String potion : potionList.split(",")) {
                    String[] potionSection = potion.split(":");
                    PotionEffectType type = LegacyAPI.getEffectByName(potionSection[0]);
                    if (type != null) {
                        try {
                            int amplifier = 1;
                            if (StringUtils.containsIgnoreCase(potion, ":")) {
                                if (Integer.parseInt(potionSection[1]) == 1 || Integer.parseInt(potionSection[1]) == 2 || Integer.parseInt(potionSection[1]) == 3) {
                                    amplifier = Integer.parseInt(potionSection[1]) - 1;
                                } else {
                                    amplifier = Integer.parseInt(potionSection[1]);
                                }
                            }
                            potionEffectList.add(new PotionEffect(type, Integer.parseInt(potionSection[2]) * 20, amplifier));
                        } catch (NumberFormatException e) {
                            ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + potionSection[1] + " is not a number and a number was expected.");
                            ServerUtils.logWarn("{ItemDesigner} Custom Potion: " + potionSection[0] + " will now be set to level 1.");
                            ServerUtils.sendDebugTrace(e);
                        }
                    } else {
                        ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + potionSection[0] + " is an incorrect potion effect for the custom potion.");
                        ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct potion effects.");
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
        String potionEffect = (itemMap.getNodeLocation().getString(".potion-effect") != null ? itemMap.getNodeLocation().getString(".potion-effect") : itemMap.getNodeLocation().getString(".potion-effects"));
        if (potionEffect != null) {
            if (ServerUtils.hasSpecificUpdate("1_9") && !ItemJoin.getCore().getPlugin().getServer().getVersion().contains("(MC: 1.9)") && itemMap.getMaterial().toString().equalsIgnoreCase("TIPPED_ARROW")) {
                String effectList = potionEffect.replace(" ", "");
                List<PotionEffect> potionEffectList = new ArrayList<>();
                for (String effect : effectList.split(",")) {
                    String[] tippedSection = effect.split(":");
                    PotionEffectType type = LegacyAPI.getEffectByName(tippedSection[0]);
                    if (type != null) {
                        try {
                            int level = 1;
                            int duration;
                            if (StringUtils.containsIgnoreCase(effect, ":")) {
                                if (Integer.parseInt(tippedSection[1]) == 1 || Integer.parseInt(tippedSection[1]) == 2 || Integer.parseInt(tippedSection[1]) == 3) {
                                    level = Integer.parseInt(tippedSection[1]) - 1;
                                } else {
                                    level = Integer.parseInt(tippedSection[1]);
                                }
                            }
                            duration = Integer.parseInt(tippedSection[2]);
                            potionEffectList.add(new PotionEffect(type, duration * 160, level));
                        } catch (NumberFormatException e) {
                            ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + tippedSection[1] + " is not a number and a number was expected.");
                            ServerUtils.logWarn("{ItemDesigner} Tipped Effect: " + tippedSection[0] + " will now be set to level 1.");
                            ServerUtils.sendDebugTrace(e);
                        }
                    } else {
                        ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + tippedSection[0] + " is an incorrect potion effect for the tipped arrow.");
                        ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html for a list of correct potion effects.");
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
        final String bannerMeta = itemMap.getNodeLocation().getString(".banner-meta");
        if (bannerMeta != null && StringUtils.containsIgnoreCase(itemMap.getMaterial().toString(), "BANNER")) {
            String bannerList = bannerMeta.replace(" ", "");
            List<Pattern> patterns = new ArrayList<>();
            for (String banner : bannerList.split(",")) {
                String[] bannerSection = banner.split(":");
                DyeColor Color = null;
                PatternType Pattern = null;
                try {
                    Color = DyeColor.valueOf(bannerSection[0].toUpperCase());
                    Pattern = PatternType.valueOf(bannerSection[1].toUpperCase());
                } catch (Exception ignored) {
                }
                if (Color != null && Pattern != null) {
                    patterns.add(new Pattern(Color, Pattern));
                } else if (Color == null) {
                    ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + bannerSection[0] + " is an incorrect dye color.");
                    ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for a list of correct dye colors.");
                } else if (banner.contains(":")) {
                    ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + bannerSection[1] + " is an incorrect pattern type.");
                    ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/block/banner/PatternType.html for a list of correct pattern types.");
                }
            }
            itemMap.setBannerPatterns(patterns);
        }
    }

    /**
     * Sets the Armor Trim Pattern of the Custom Item,
     * adding the Custom Armor Trim Pattern to the item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setTrim(final ItemMap itemMap) {
        final String trimMeta = itemMap.getNodeLocation().getString(".trim-meta");
        if (trimMeta != null && ServerUtils.hasSpecificUpdate("1_20") && ItemHandler.isArmor(itemMap.getMaterial().toString())) {
            String armorTrim = trimMeta.replace(" ", "");
            String[] armorSection = armorTrim.split(":");
            final org.bukkit.inventory.meta.trim.TrimMaterial Material = ItemHandler.getTrimMaterial(armorSection[0].toUpperCase());
            final org.bukkit.inventory.meta.trim.TrimPattern Pattern = ItemHandler.getTrimPattern(armorSection[1].toUpperCase());
            Map<String, String> trimPattern = new HashMap<>();
            if (Material != null && Pattern != null) {
                trimPattern.put(armorSection[0].toUpperCase(), armorSection[1].toUpperCase());
            } else if (Material == null) {
                ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + armorSection[0] + " is an incorrect armor trim material.");
                ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/meta/trim/TrimMaterial.html for a list of correct material types.");
            } else if (armorTrim.contains(":")) {
                ServerUtils.logSevere("{ItemDesigner} An error occurred in the config, " + armorSection[1] + " is an incorrect armor trim pattern.");
                ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/meta/trim/TrimPattern.html for a list of correct pattern types.");
            }
            itemMap.setTrimPattern(trimPattern);
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
                final String fireType = itemMap.getNodeLocation().getString(".firework.type");
                final String fireColors = itemMap.getNodeLocation().getString(".firework.colors");
                if (fireType != null) {
                    String stringType = fireType.toUpperCase();
                    boolean flicker = itemMap.getNodeLocation().getBoolean(".firework.flicker");
                    boolean trail = itemMap.getNodeLocation().getBoolean(".firework.trail");
                    Type buildType = Type.valueOf(stringType);
                    List<Color> colors = new ArrayList<>();
                    List<DyeColor> saveColors = new ArrayList<>();
                    if (fireColors != null) {
                        String colorList = fireColors.replace(" ", "");
                        for (String color : colorList.split(",")) {
                            try {
                                colors.add(DyeColor.valueOf(color.toUpperCase()).getFireworkColor());
                                saveColors.add(DyeColor.valueOf(color.toUpperCase()));
                            } catch (Exception e) {
                                ServerUtils.logSevere("{ItemDesigner} The item " + itemMap.getConfigName() + " has the incorrect dye color " + color.toUpperCase() + " and does not exist.");
                                ServerUtils.logWarn("{ItemDesigner} Please see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for a list of correct dye color names.");
                            }
                        }
                    } else if (itemMap.getNodeLocation().getString(".firework.colors") == null) {
                        colors.add(DyeColor.valueOf("WHITE").getFireworkColor());
                        saveColors.add(DyeColor.valueOf("WHITE"));
                    }
                    FireworkEffect effect = FireworkEffect.builder().trail(trail).flicker(flicker).withColor(colors).withFade(colors).with(buildType).build();
                    itemMap.setFirework(effect);
                    itemMap.setFireworkType(buildType);
                    itemMap.setFireworkColor(saveColors);
                    itemMap.setFireworkTrail(trail);
                    itemMap.setFireworkFlicker(flicker);
                }
                int power = itemMap.getNodeLocation().getInt(".firework.power");
                if (power == 0) {
                    power = 1;
                }
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
        final String chargeColor = itemMap.getNodeLocation().getString(".charge-color");
        if (chargeColor != null) {
            if (StringUtils.containsIgnoreCase(itemMap.getMaterial().toString(), "CHARGE") || StringUtils.containsIgnoreCase(itemMap.getMaterial().toString(), "STAR")) {
                itemMap.setChargeColor(DyeColor.valueOf(chargeColor.toUpperCase()));
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
        String leatherColor = itemMap.getNodeLocation().getString(".leather-color");
        if (leatherColor != null) {
            if (itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_HELMET") || itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_CHESTPLATE")
                    || itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_LEGGINGS") || itemMap.getMaterial().toString().equalsIgnoreCase("LEATHER_BOOTS")) {
                boolean isPlaceholder = leatherColor.startsWith("%");
                leatherColor = leatherColor.replace(" ", "");
                try {
                    if (leatherColor.startsWith("#")) {
                        itemMap.setLeatherHex(leatherColor);
                    } else if (!isPlaceholder) {
                        boolean hexValue = true;
                        for (DyeColor color : DyeColor.values()) {
                            if (color.name().replace(" ", "").equalsIgnoreCase(leatherColor)) {
                                itemMap.setLeatherColor(leatherColor.toUpperCase());
                                hexValue = false;
                                break;
                            }
                        }
                        if (hexValue) {
                            itemMap.setLeatherHex(leatherColor);
                        }
                    } else {
                        itemMap.setLeatherColor(leatherColor);
                    }
                } catch (Exception ex) {
                    ServerUtils.logSevere("{ItemDesigner} The leather-color: " + leatherColor + " is not a valid color for the item " + itemMap.getConfigName() + ".");
                    ServerUtils.logWarn("{ItemDesigner} Use hex-color or see: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html for valid bukkit colors.");
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
     * Sets the Custom Attributes for the Custom Item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setAttributes(final ItemMap itemMap) {
        String[] attributes = null;
        String val = itemMap.getNodeLocation().getString(".attributes");
        if (val != null && val.contains("{") && val.contains("}")) {
            attributes = val.split(",");
        }
        if (attributes != null && attributes.length != 0) {
            try {
                Map<String, Double> attributesList = new HashMap<>();
                for (String value : attributes) {
                    String[] valueParts = value.replace("{", "").replace("}", "").replace(" ", "").split(":");
                    if (StringUtils.isInt(valueParts[1]) || StringUtils.isDouble(valueParts[1])) {
                        attributesList.put(valueParts[0], Double.parseDouble(valueParts[1]));
                    } else {
                        ServerUtils.logSevere("{ItemDesigner} There was an issue setting the custom attribute " + valueParts[0] + " for " + itemMap.getConfigName() + ".");
                        ServerUtils.logSevere("{ItemDesigner} The value " + valueParts[1] + " is not an integer or double value.");
                    }
                }
                itemMap.setAttributes(attributesList);
            } catch (Exception e) {
                ServerUtils.logSevere("{ItemDesigner} An error has occurred when setting custom attributes for " + itemMap.getConfigName() + ".");
                ServerUtils.logSevere("{ItemDesigner} The attributes should look like '{GENERIC_ARMOR:10}, {GENERIC_ARMOR_TOUGHNESS:8}' or \"{GENERIC_ARMOR:10}, {GENERIC_ARMOR_TOUGHNESS:8}\".");
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
        if (StringUtils.containsIgnoreCase(itemMap.getItemFlags(), "hide-attributes")) {
            itemMap.setAttributesInfo(true);
        }
    }

    /**
     * Sets the Enchantments of the Custom Item,
     * shows or hides the Enchantments of the item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setEnchantmentFlags(final ItemMap itemMap) {
        if (StringUtils.containsIgnoreCase(itemMap.getItemFlags(), "hide-enchants")) {
            itemMap.setEnchantmentsInfo(true);
        }
    }

    /**
     * Sets the Flags of the Custom Item,
     * shows or hides the Flags of the item.
     *
     * @param itemMap - The ItemMap being modified.
     */
    private void setFlags(final ItemMap itemMap) {
        if (StringUtils.containsIgnoreCase(itemMap.getItemFlags(), "hide-flags")) {
            itemMap.setFlagsInfo(true);
        }
    }
}