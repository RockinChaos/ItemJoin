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
package me.RockinChaos.itemjoin;

import com.google.gson.JsonObject;

import me.RockinChaos.itemjoin.utils.ReflectionUtils;
import me.RockinChaos.itemjoin.utils.ReflectionUtils.MinecraftMethod;
import me.RockinChaos.itemjoin.utils.ServerUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ChatComponent {
  
   /**
    * Creates a new Text.
    * 
    * @param text - The beginning text
    * @return A new Text Section.
    */
    public static TextSection of(final String text) {
        return new TextSection(true, true, null).setText(text);
    }
    
   /**
    * Sends the TextComponent to the Player.
    * 
    * @param text - The TextComponent to be sent.
    * @param player - The Player being referenced.
    */
    public static void sendTo(final TextSection text, final Player player) {
        try {
            final Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            final Object connection = craftPlayer.getClass().getField((ServerUtils.hasSpecificUpdate("1_17") ? "b" : "playerConnection")).get(craftPlayer);
            final Class<?> baseComponent = ReflectionUtils.getMinecraftClass("IChatBaseComponent");
            final Class<?> serializer = ReflectionUtils.getMinecraftClass("IChatBaseComponent$ChatSerializer");
            final Class<?> chatPacket = ReflectionUtils.getMinecraftClass((ServerUtils.hasSpecificUpdate("1_19") ? "ClientboundSystemChatPacket" : "PacketPlayOutChat"));
            final Class<?> packetClass = ReflectionUtils.getMinecraftClass("Packet");
            final Object component = serializer.getDeclaredMethod("a", String.class).invoke(null, text.toString());
            if (ServerUtils.hasSpecificUpdate("1_19")) {
            	Constructor<?> packet;
            	try {
            		packet = chatPacket.getConstructor(baseComponent, int.class);
            		connection.getClass().getMethod(MinecraftMethod.sendPacket.getMethod(connection, packetClass), packetClass).invoke(connection, packet.newInstance(component, 0));
            	} catch (Exception e) {
            		try {
            			packet = chatPacket.getConstructor(baseComponent, boolean.class);
            			connection.getClass().getMethod(MinecraftMethod.sendPacket.getMethod(connection, packetClass), packetClass).invoke(connection, packet.newInstance(component, false));
            		} catch (Exception e2) {
            			ServerUtils.sendSevereTrace(e2);
            		}
            	}
            } else if (ServerUtils.hasSpecificUpdate("1_16")) {
            	final Constructor<?> packet = chatPacket.getConstructor(baseComponent, ReflectionUtils.getMinecraftClass("ChatMessageType"), player.getUniqueId().getClass());
            	try {
            		Class<?> chatMessage = ReflectionUtils.getMinecraftClass("ChatMessageType");
            		connection.getClass().getMethod(MinecraftMethod.sendPacket.getMethod(connection, packetClass), packetClass).invoke(connection, packet.newInstance(component, chatMessage.getMethod("a", byte.class).invoke(null, (byte)0), player.getUniqueId()));
            	} catch (Exception e) {
            		ServerUtils.sendSevereTrace(e);
            	}
            } else {
            	final Constructor<?> packet = chatPacket.getConstructor(baseComponent);
            	connection.getClass().getMethod(MinecraftMethod.sendPacket.getMethod(connection, packetClass), packetClass).invoke(connection, packet.newInstance(component));
            }
        } catch (Exception e) {
        	ServerUtils.sendSevereTrace(e);
        }
    }
    
   /**
    * Represents a hover event in chat
    * 
    */
    public static class HoverEvent {
        
        private HoverAction action = HoverAction.SHOW_TEXT;
        private TextSection text;
        
       /**
        * Initializes a new instance of HoverEvent.
        * 
        * @param parent - The TextSection with the Hover text to be set.
        */
        public HoverEvent(TextSection parent) {
            this.text = parent;
        }
        
       /**
        * Sets the hover action.
        * 
        * @param action - The HoverAction to be set.
        */
        public final void action(HoverAction action) {
            this.action = action;
        }
        
       /**
        * Gets the hover action.
        * 
        * @return The hover action as HoverAction.
        */
        public final HoverAction getAction() {
            return action;
        }
        
       /**
        * Gets the hover TextSection.
        * 
        * @return The Hover text as a TextSection.
        */
        public final TextSection getHover() {
            return text;
        }
    }
    
   /**
    * Represents a click event in chat.
    * 
    */
    public static class ClickEvent {
        
        private ClickAction action = ClickAction.SUGGEST_COMMAND;
        private String click;
        
       /**
        * sets the click action.
        * 
        * @param action - The click action to be set.
        */
        public void action(ClickAction action) {
            this.action = action;
        }
        
       /**
        * Gets the click action.
        * 
        * @return The ClickAction.
        */
        public ClickAction getAction() {
            return action;
        }
        
       /**
        * Sets the click text.
        * 
        * @param click - The click text to be set.
        */
        public void click(String click) {
            this.click = click;
        }
        
       /**
        * Gets the click text.
        * 
        * @return The Click text as a String.
        */
        public String getClick() {
            return click;
        }
    }
    
   /**
    * The {@link HoverEvent} actions.
    * 
    */
    public enum HoverAction {
    	
       /**
        * Display text when the user hovers over the message
        * 
        */
        SHOW_TEXT,
       /**
        * Display an item when the user hovers over the message
        * 
        */
        SHOW_ITEM,
       /**
        * Display an entity when the user hovers over the message
        * 
        */
        SHOW_ENTITY
        
    }
    
   /**
    * The {@link ClickEvent} actions.
    * 
    */
    public enum ClickAction {
        
       /**
        * Open a URL for a user when clicked
        * 
        */
        OPEN_URL,
       /**
        * Run a command for a user when clicked
        * 
        */
        RUN_COMMAND,
       /**
        * Suggest a command for a user when clicked
        * 
        */
        SUGGEST_COMMAND,
       /**
        * Change the page for a user when clicked
        * 
        */
        CHANGE_PAGE
        
    }

   /**
    * Represents a section of text.
    * 
    */
    public static class TextSection extends ChatComponent {

        private String text = "";
        private String insertion;
        private boolean isParent;
        private TextSection parent;
        private HoverEvent hoverEvent;
        private ClickEvent clickEvent;
        private boolean canHaveEvents;
        private ChatColor color = ChatColor.RESET;
        private List<TextSection> extra;
        private boolean bold = false;
        private boolean italics = false;
        private boolean underline = false;
        private boolean obfuscated = false;
        private boolean strikethrough = false;
        private Gson gson = new Gson();
        
       /**
        * Creates a new TextSection instance.
        * 
        * @param events - If this TextSection is allowed to have events such as Hover or Click Actions.
        * @param isParent - If this TextSection is a parent or a child.
        * @param parent - A parent TextSection (superior).
        */
        private TextSection(boolean events, boolean isParent, TextSection parent) {
            this.canHaveEvents = events;
            this.isParent = isParent;
            if (!isParent) {
                this.parent = parent;
            } else this.extra = new ArrayList<>();
        }
        
       /**
        * Sends the TextSection as a ChatComponent to the Player.
        * 
        * @param player - The Player to have the ChatComponent sent.
        */
        public final void sendTo(Player player) {
            sendTo(this, player);
        }
        
       /**
        * Adds a text section to the current Text.
        * 
        * @param text - The text to append to this current section.
        * @return The newly created text section.
        */
        public final TextSection append(String text) {
            TextSection section = new TextSection(true, false, this.getParent()).setText(text);
            this.getExtra().add(section);
            return section;
        }
        
       /**
        * Adds a text section to the current text.
        * 
        * @param consumer - The text to append to this current section.
        */
        public final TextSection append(Consumer<TextSection> consumer) {
            TextSection section = new TextSection(true, false, this.getParent());
            consumer.accept(section);
            this.getExtra().add(section);
            return this;
        }
        
       /**
        * Adds a text section to the current text.
        * 
        * @param textSection - The text to append to this current section.
        */
        public final TextSection append(TextSection textSection) {
            this.getExtra().add(textSection.getParent());
            return this;
        }
        
       /**
        * Set the text of this TextSection.
        * 
        * @param text - The text.
        * @return This TextSection.
        */
        public TextSection setText(String text) {
            this.text = text;
            return this;
        }
        
       /**
        * Set the color of the current text.
        * 
        * @param color - The color.
        *
        * <p>
        * Any can be set. 
        * Any formatting such as bold or italics must be set with the appropriate methods.
        * </p>
        */
        public TextSection setColor(ChatColor color) {
            if (21 > color.ordinal() && color.ordinal() > 15) {
                return this;
            }
            this.color = color;
            return this;
        }
        
       /**
        * Sets this Section to bold.
        * 
        * @param bold - True to set bold, false otherwise.
        * @return This TextSection.
        */
        public TextSection setBold(boolean bold) {
            this.bold = bold;
            return this;
        }
        
       /**
        * Sets this section to Italics.
        * 
        * @param italics - True to set italics, false otherwise.
        * @return This TextSection.
        */
        public TextSection setItalics(boolean italics) {
            this.italics = italics;
            return this;
        }
        
       /**
        * Sets this section to underlined.
        * 
        * @param underline - True to set underlined, false otherwise.
        * @return This TextSection.
        */
        public TextSection setUnderlined(boolean underline) {
            this.underline = underline;
            return this;
        }
        
       /**
        * Sets this section to obfuscated.
        * 
        * @param obfuscated - True to set obfuscated, false otherwise.
        * @return This TextSection.
        */
        public TextSection setObfuscated(boolean obfuscated) {
            this.obfuscated = obfuscated;
            return this;
        }
        
       /**
        * Sets this section to Strikethrough.
        * 
        * @param strikethrough - True to set strikethrough, false otherwise.
        * @return This TextSection.
        */
        public TextSection setStrikethrough(boolean strikethrough) {
            this.strikethrough = strikethrough;
            return this;
        }
        
       /**
        * When the current text is shift clicked, this will insert the given text.
        * 
        * @param insertion - The text to insert.
        * @return This TextSection.
        */
        public TextSection shiftClickEvent(String insertion) {
            if (!this.canHaveEvents) return this;
            this.insertion = insertion;
            return this;
        }
        
       /**
        * Adds a hover event to this text section.
        * 
        * @param hoverEvent - The hover event.
        * @return This TextSection.
        */
        public TextSection hoverEvent(Consumer<HoverEvent> hoverEvent) {
            if (!this.canHaveEvents) return this;
            this.hoverEvent = new HoverEvent(this.parent);
            hoverEvent.accept(this.hoverEvent);
            return this;
        }
        
       /**
        * Adds a hover event to this text section.
        * 
        * @param hoverEvent - The hover event.
        * @return This TextSection.
        */
        public TextSection hoverEvent(HoverEvent hoverEvent) {
            if (!this.canHaveEvents) return this;
            this.hoverEvent = hoverEvent;
            return this;
        }
        
       /**
        * Adds a click event to this text section.
        * 
        * @param clickEvent - The click event.
        * @return This TextSection.
        */
        public TextSection clickEvent(Consumer<ClickEvent> clickEvent) {
            if (!this.canHaveEvents) return this;
            this.clickEvent = new ClickEvent();
            clickEvent.accept(this.clickEvent);
            return this;
        }
        
       /**
        * Adds a click event to this text section.
        * 
        * @param clickEvent - The click event.
        * @return This TextSection.
        */
        public TextSection clickEvent(ClickEvent clickEvent) {
            if (!this.canHaveEvents) return this;
            this.clickEvent = clickEvent;
            return this;
        }
        
       /**
        * Checks if this current section is a parent section.
        * 
        * @return True if this section is the parent section, false otherwise..
        */
        public final boolean isParent() {
            return this.isParent;
        }
        
       /**
        * Gets all the additional text sections.
        * 
        * @return all the additional text sections.
        */
        public List<TextSection> getExtra() {
            if (this.isParent) return this.extra;
            else return this.getParent().getExtra();
        }
       
       /**
        * Get the text currently represented by this section.
        * 
        * @return The section text.
        */
        public String getText() {
            return this.text;
        }
        
       /**
        * Returns the parent of the entire TextComponent.
        * 
        * @return The parent TextSection.
        */
        public final TextSection getParent() {
            if (this.isParent) return this;
            else return this.parent;
        }
        
       /**
        * Returns this TextSection as a JsonObject.
        * 
        * @return The TextSection JsonObject.
        */
        public JsonObject getJson() {
            JsonObject hover = null;
            if (this.hoverEvent != null && this.canHaveEvents) {
                hover = new JsonObject();
                List<JsonObject> val = new ArrayList<>();
                val.add(this.hoverEvent.getHover().getJson());
                hover.addProperty("action", this.hoverEvent.getAction().toString().toLowerCase());
                hover.add("value", this.gson.toJsonTree(val));
            }
            JsonObject click = null;
            if (this.clickEvent != null && this.canHaveEvents) {
                click = new JsonObject();
                click.addProperty("action", this.clickEvent.getAction().toString().toLowerCase());
                click.addProperty("value", this.clickEvent.getClick());
            }
            JsonObject json = new JsonObject();
            json.addProperty("text", this.text);
            json.addProperty("color", this.color.name());
            if (this.bold) json.addProperty("bold", true);
            if (this.italics) json.addProperty("italic", true);
            if (this.underline) json.addProperty("underlined", true);
            if (this.obfuscated) json.addProperty("obfuscated", true);
            if (this.insertion != null) json.addProperty("insertion", this.insertion);
            if (this.strikethrough) json.addProperty("strikethrough", true);
            if (hover != null) {
                json.add("hoverEvent", hover);
            }
            if (click != null) {
                json.add("clickEvent", click);
            }
            if (this.isParent && !this.extra.isEmpty()) {
                List<JsonObject> sections = new ArrayList<>();
                for (TextSection section : this.extra) {
                    sections.add(section.getJson());
                }
                json.add("extra", this.gson.toJsonTree(sections));
            }
            return json;
        }
        
       /**
        * Gets a simplified String that isn't a json object. (No click or hover events are included, just text with formatting).
        * 
        * @return Formatted String.
        */
        public String getFormatted() {
            StringBuilder stringBuilder = new StringBuilder();
            if (this.bold) stringBuilder.append(ChatColor.BOLD);
            if (this.italics) stringBuilder.append(ChatColor.ITALIC);
            if (this.underline) stringBuilder.append(ChatColor.UNDERLINE);
            if (this.strikethrough) stringBuilder.append(ChatColor.STRIKETHROUGH);
            stringBuilder.append(this.color);
            stringBuilder.append(this.text);
            if (this.isParent) {
                if (!this.extra.isEmpty()) this.getExtra().forEach(textSection -> stringBuilder.append(textSection.getFormatted()));
            }
            
            return stringBuilder.toString();
        }
        
       /**
        * Turns this text object into the proper JSON format.
        * 
        * @return Json string.
        */
        @Override
        public String toString() {
            if (this.isParent) return this.getJson().toString();
            return this.parent.getJson().toString();
        }

       /**
        * Returns a very simple text. Removes all color, removes all formatting.
        * 
        * @return The plain text.
        */
        public String toUnformatted() {
            StringBuilder builder = new StringBuilder();
            this.getParent().getSections().stream().map(TextSection::getText).forEach(builder::append);
            return builder.toString();
        }

       /**
        * Gets a list of sections this TextSection consists of.
        * 
        * @return A list of TextSections.
        */
        public List<TextSection> getSections() {
            List<TextSection> sections = new ArrayList<>(this.getParent().getExtra());
            sections.add(0, this.getParent());
            return sections;
        }
    }
}