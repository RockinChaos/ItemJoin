package me.RockinChaos.itemjoin.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import me.RockinChaos.itemjoin.ItemJoin;
import me.RockinChaos.itemjoin.handlers.ServerHandler;

public class ImageMap extends MapRenderer {
	
    private int id;
    private String image;
	private BufferedImage imgCache;
	private List<BufferedImage> imgCacheList = new ArrayList<BufferedImage>();
	private boolean isGIF = false;
	private List<Integer> Rendered = new ArrayList<Integer> ();
	
	public ImageMap(String image, int imageID) {
		this.id = imageID;
		this.image = image;
		if (image != null && !image.equalsIgnoreCase("default.jpg")) {
			try { 
				if (Utils.containsIgnoreCase(this.image, ".gif")) {
					this.isGIF = true;
				    ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
				    ImageInputStream ciis = ImageIO.createImageInputStream(new File(ItemJoin.getInstance().getDataFolder(), String.valueOf(image)));
				    reader.setInput(ciis, false);
				    for (int i = 0; i < reader.getNumImages(true); i++) { this.imgCacheList.add(reader.read(i)); }
				    ciis.close();
				} else { this.imgCache = ImageIO.read(new File(ItemJoin.getInstance().getDataFolder(), String.valueOf(image))); }
			} catch (IOException e) { ServerHandler.sendDebugTrace(e); }
		} else if (image != null && image.equalsIgnoreCase("default.jpg") && ItemJoin.getInstance().getResource("files/generated/default.jpg") != null) {
			try { this.imgCache = ImageIO.read(ItemJoin.getInstance().getResource("files/generated/default.jpg"));
			} catch (IOException e) { ServerHandler.sendDebugTrace(e); }
		}
	}
    
    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
    	if (Rendered.isEmpty() || !Rendered.contains(this.id)) {
    		try {
    			Rendered.add(this.id);
				mapView.setScale(MapView.Scale.NORMAL);
				if (this.isGIF) { this.drawGIF(mapCanvas);} 
				else { mapCanvas.drawImage(0, 0, this.imgCache); }
				ServerHandler.sendDebugMessage("rendering map; " + this.image + " with the id " + this.id);
			} catch (Exception e) {
				ServerHandler.sendErrorMessage("&4There was a problem rending your map(s)!");
				ServerHandler.sendErrorMessage("&4Please check and make sure your image size is no larger than 128x128 pixels.");
				ServerHandler.sendErrorMessage("&4If you are still experiencing this error please contact the plugin developer!");
				ServerHandler.sendDebugTrace(e);
			}
    	}
    }
    
    private void drawGIF(final MapCanvas mapCanvas) {
    	final int maxFrames = this.imgCacheList.size();
    	int delay = 0; int frameSize = 1;
    	for (final BufferedImage frame: imgCacheList) {
    		final int frameNumber = frameSize;
    		new java.util.Timer().schedule(new java.util.TimerTask() {
    			@Override
				public void run() {
    				mapCanvas.drawImage(0, 0, frame);
    				if (frameNumber == maxFrames) { drawGIF(mapCanvas); }
    				this.cancel();
    			}
    		}, delay);
    		delay = delay + 135;
    		frameSize++;
    	}
    }

	public MapView FetchExistingView(int id) {
		MapView view = Legacy.getMapView(id);
		if (view == null) { view = Legacy.createLegacyMapView(); }
		return view;
	}
}