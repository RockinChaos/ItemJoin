package me.RockinChaos.itemjoin.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnimationHandler {
	
	// This is a currently unimplemented feature that is currently in development so it is blocked so only the DEV can work on it.
	// This will soon be the handler for animations that allows you to update your items if placeholders change or to have an animated lore or item name.
	
    public static ItemStack modifyName(ItemStack item, Player player)
    {
        //String name = ;
       // if(name == null)
      //  {
        //    return item;
      //  } else
      //  {
            ItemMeta im = item.getItemMeta();
           // lore.add();
            //im.setLore(lore);
            item.setItemMeta(im);
            return item;
     //   }
    }
    
    public static ItemStack modifyLore(ItemStack item, Player player)
    {
        //String name = ;
     //   if(name == null)
       // {
       //     return item;
      //  } else
      //  {
            ItemMeta im = item.getItemMeta();
            //List lore = ;
            //lore.add();
            //im.setLore(lore);
            item.setItemMeta(im);
            return item;
     //   }
    }

}
