package me.RockinChaos.itemjoin.utils;

import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageRenderer extends MapRenderer {

	private static String writeImage;
	private static int id;
	public static HashMap < String, Integer > hasRendered = new HashMap < String, Integer > ();
	
	public ImageRenderer(String image, int imageID) {
		writeImage = image;
		id = imageID;
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if (hasRendered.get(writeImage) == null || hasRendered.get(writeImage) != null && !hasRendered.get(writeImage).toString().contains(id + "")) {
			try {
				if (writeImage != null && !writeImage.equalsIgnoreCase("default.png")) {
					hasRendered.put(writeImage, id);
					view.setScale(MapView.Scale.NORMAL);
					canvas.drawImage(0, 0, ImageIO.read(new File(ItemJoin.getInstance().getDataFolder(), String.valueOf(writeImage))));
					ServerHandler.sendDebugMessage("rendering map; " + writeImage + " with the id " + id);
				} else if (writeImage != null && writeImage.equalsIgnoreCase("default.png") && ItemJoin.getInstance().getResource("default.png") != null) {
					hasRendered.put(writeImage, id);
					view.setScale(MapView.Scale.NORMAL);
					canvas.drawImage(0, 0, ImageIO.read(ItemJoin.getInstance().getResource("default.png")));
					ServerHandler.sendDebugMessage("rendering map; default.png" + " with the id " + id);
				}
			} catch (Exception e) {
				ServerHandler.sendConsoleMessage("&4[ERROR; 7753c61] There was a problem rending your map(s)!");
				ServerHandler.sendConsoleMessage("&4Please check and make sure your image size is no larger than 128x128 pixels.");
				ServerHandler.sendConsoleMessage("&4If you are still experiencing this error please contact the plugin developer!");
				ServerHandler.sendDebugTrace(e);
			}
		}
	}
	
	public static MapView NewMapView() {
		return ItemJoin.getInstance().getServer().createMap(Bukkit.getWorlds().get(1));
	}
	
	public static MapView FetchExistingView(int id) {
		MapView view = Legacy.getMapView(id);
		if (view == null) {
			view = NewMapView();
		}
		return view;
	}

	public static void clearMaps(String image) {
		hasRendered.remove(image);
	}
}