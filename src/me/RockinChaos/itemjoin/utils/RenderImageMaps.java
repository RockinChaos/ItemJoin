package me.RockinChaos.itemjoin.utils;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import me.RockinChaos.itemjoin.ItemJoin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class RenderImageMaps extends MapRenderer {
	
	  private static String writeImage;
	  
	  public static void setImage(String image)
	  {
		  writeImage = image;
	  }

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		try {
			canvas.drawImage(0, 0, ImageIO.read(new URL(writeImage)));
		} catch (IOException e) {
			ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "[ERROR] There was a problem rending your map(s)!");
			ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "Please check and make sure your image size is no larger than 128x128 pixels.");
			ItemJoin.pl.Console.sendMessage(ItemJoin.pl.Prefix + ChatColor.RED + "If you are still experiencing this error please contact the plugin developer!");
		}	
	}
}
