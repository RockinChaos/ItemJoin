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
package me.RockinChaos.itemjoin.api;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemJoinAPI {

	private APIUtils apiUtils = new APIUtils();
	
   /**
    * Gives all custom items to the specified player.
    * 
    * @param player - that will recieve the items.
    */
	public void getItems(Player player) {
		this.apiUtils.setItems(player);
	}
	
   /**
	* Checks if the itemstack is a custom item.
	* 
	* @param item - that will be checked.
	* @return Boolean is a custom item.
	*/
	public boolean isCustom(ItemStack item) {
		return this.apiUtils.isCustom(item, null);
	}
	
   /**
	* Checks if the itemstack in the said world is a custom item.
	* 
	* @param item - that is being checked..
	* @param world - that the item is said to be in.
	* @return Boolean is a custom item.
	*/
	public boolean isCustom(ItemStack item, World world) {
		return this.apiUtils.isCustom(item, world);
	}
	
	/**
	* Fetches the ItemStack defined for the provided itemNode.
	* 
	* @param player - that will recieve the item.
	* @param itemNode - that is the custom items config node.
	* @return ItemStack found custom item.
	*/
	public ItemStack getItemStack(Player player, String itemNode) {
		return this.apiUtils.getItemStack(player, itemNode);
	}
	
   /**
	* Fetches the config node name of the custom item.
    * 
    * @param item - that will be checked.
	* @return String node of the custom item.
    */
	public String getNode(ItemStack item) {
		return this.apiUtils.getNode(item, null);
	}
	
   /**
    * Fetches the config node name of the custom item.
	* 
	* @param item - that will be checked.
	* @param world - that the item is said to be in.
	* @return String node of the custom item.
	*/
	public String getNode(ItemStack item, World world) {
		return this.apiUtils.getNode(item, world);
	}
	
   /**
    * Fetches the itemflags that are defined for the custom item.
	* 
    * @param itemNode - that is the custom items config node.
	* @return List of itemflags for the custom item.
    */
	public List <String> getItemflags(String itemNode) {
		return this.apiUtils.getItemflags(itemNode);
	}
	
   /**
    * Fetches commands that are defined for the custom item.
	* 
    * @param itemNode - that is the custom items config node.
	* @return List of commands for the custom item.
    */
	public List <String> getCommands(String itemNode) {
		return this.apiUtils.getCommands(itemNode);
	}
	
   /**
    * Fetches triggers that are defined for the custom item.
	* 
    * @param itemNode - that is the custom items config node.
	* @return List of triggers for the custom item.
    */
	public List <String> getTriggers(String itemNode) {
		return this.apiUtils.getTriggers(itemNode);
	}
	
   /**
	* Fetches the slot that the custom item is defined to be set to.
    * 
	* @param itemNode - that is the custom items config node.
	* @return String of integer or custom slot for the custom item.
	*/
	public String getSlot(String itemNode) {
		return this.apiUtils.getSlot(itemNode);
	}
	
   /**
    * Fetches all slots that the custom item is defined to be set to.
    * In the instance that the custom item is a MultiSlot item.
	* 
    * @param itemNode - that is the custom items config node.
	* @return List of slots for the custom item.
	*/
	public List<String> getMultipleSlots(String itemNode) {
		return this.apiUtils.getMultipleSlots(itemNode);
	}
}