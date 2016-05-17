package me.RockinChaos.itemjoin.utils;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class RenderImageMaps extends MapRenderer {
	
	  private static String writeImage;
	  public static ConsoleCommandSender Console = ItemJoin.pl.getServer().getConsoleSender();
	  public static String Prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "ItemJoin" + ChatColor.GRAY + "] ";
	  
	  public static void setImage(String image)
	  {
		  writeImage = image;
	  }

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		try {
			canvas.drawImage(0, 0, ImageIO.read(new File(ItemJoin.pl.getDataFolder(), String.valueOf(writeImage))));
		} catch (IOException e) {
			Console.sendMessage(Prefix + ChatColor.RED + "[ERROR] There was a problem rending your map(s)!");
			Console.sendMessage(Prefix + ChatColor.RED + "Please check and make sure your image size is no larger than 128x128 pixels.");
			Console.sendMessage(Prefix + ChatColor.RED + "If you are still experiencing this error please contact the plugin developer!");
		}	
	}
}
