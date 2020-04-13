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
package me.RockinChaos.itemjoin.utils.protocol;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.RockinChaos.itemjoin.handlers.ServerHandler;
import me.RockinChaos.itemjoin.utils.Reflection;
import me.RockinChaos.itemjoin.utils.Reflection.FieldAccessor;
import me.RockinChaos.itemjoin.utils.Reflection.MethodInvoker;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.mojang.authlib.GameProfile;

/**
 * Represents a very tiny alternative to ProtocolLib.
 * <p>
 * It now supports intercepting packets during login and status ping (such as OUT_SERVER_PING)!
 * 
 * @author Kristian
 */
public abstract class TinyProtocol {
	private final AtomicInteger ID = new AtomicInteger(0);

	private final MethodInvoker getPlayerHandle = Reflection.getMethod("{obc}.entity.CraftPlayer", "getHandle");
	private final FieldAccessor<Object> getConnection = Reflection.getField("{nms}.EntityPlayer", "playerConnection", Object.class);
	private final FieldAccessor<Object> getManager = Reflection.getField("{nms}.PlayerConnection", "networkManager", Object.class);
	private final FieldAccessor<Channel> getChannel = Reflection.getField("{nms}.NetworkManager", Channel.class, 0);

	private final Class<Object> minecraftServerClass = Reflection.getUntypedClass("{nms}.MinecraftServer");
	private final Class<Object> serverConnectionClass = Reflection.getUntypedClass("{nms}.ServerConnection");
	private final FieldAccessor<Object> getMinecraftServer = Reflection.getField("{obc}.CraftServer", minecraftServerClass, 0);
	private final FieldAccessor<Object> getServerConnection = Reflection.getField(minecraftServerClass, serverConnectionClass, 0);
	private final FieldAccessor<?> getNetworkMarkers = Reflection.getField(serverConnectionClass, (Class<?>)List.class, 1);

	private final Class<?> PACKET_LOGIN_IN_START = Reflection.getMinecraftClass("PacketLoginInStart");
	private final FieldAccessor<GameProfile> getGameProfile = Reflection.getField(PACKET_LOGIN_IN_START, GameProfile.class, 0);

	private Map<String, Channel> channelLookup = new MapMaker().weakValues().makeMap();
	private Listener listener;

	private Set<Channel> uninjectedChannels = Collections.newSetFromMap(new MapMaker().weakKeys().<Channel, Boolean>makeMap());

	private List<Object> networkManagers;

	private List<Channel> serverChannels = Lists.newArrayList();
	private ChannelInboundHandlerAdapter serverChannelHandler;
	private ChannelInitializer<Channel> beginInitProtocol;
	private ChannelInitializer<Channel> endInitProtocol;

	private String handlerName;

	protected volatile boolean closed;
	protected Plugin plugin;

	/**
	 * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
	 * <p>
	 * You can construct multiple instances per plugin.
	 * 
	 * @param plugin - the plugin.
	 */
	public TinyProtocol(final Plugin plugin) {
		this.plugin = plugin;
		this.handlerName = this.getHandlerName();
		this.registerBukkitEvents();
		try {
			this.registerChannelHandler();
			this.registerPlayers(plugin);
		} catch (IllegalArgumentException ex) {
			plugin.getLogger().info("[TinyProtocol] Delaying server channel injection due to late bind.");
			new BukkitRunnable() {
				@Override
				public void run() {
					registerChannelHandler();
					registerPlayers(plugin);
					plugin.getLogger().info("[TinyProtocol] Late bind injection successful.");
				}
			}.runTask(plugin);
		}
	}

	private void createServerChannelHandler() {
		this.endInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				try {
					synchronized (networkManagers) {
						if (!closed) {
							channel.eventLoop().submit(() -> injectChannelInternal(channel));
						}
					}
				} catch (Exception e) {
					plugin.getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
				}
			}

		};
		this.beginInitProtocol = new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				channel.pipeline().addLast(endInitProtocol);
			}
		};
		this.serverChannelHandler = new ChannelInboundHandlerAdapter() {
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				Channel channel = (Channel) msg;
				channel.pipeline().addFirst(beginInitProtocol);
				ctx.fireChannelRead(msg);
			}

		};
	}

	/**
	 * Register bukkit events.
	 */
	private void registerBukkitEvents() {
		this.listener = new Listener() {
			@EventHandler(priority = EventPriority.LOWEST)
			public final void onPlayerLogin(PlayerLoginEvent event) {
				if (closed)
					return;
				try {
					Channel channel = getChannel(event.getPlayer());
					if (!uninjectedChannels.contains(channel)) {
						injectPlayer(event.getPlayer());
					}
				} catch (NullPointerException e) { }
			}
			@EventHandler
			public final void onPluginDisable(PluginDisableEvent e) {
				if (e.getPlugin().equals(plugin)) {
					close();
				}
			}
		};
		this.plugin.getServer().getPluginManager().registerEvents(this.listener, this.plugin);
	}

	private void registerChannelHandler() {
		Object mcServer = this.getMinecraftServer.get(Bukkit.getServer());
		Object serverConnection = this.getServerConnection.get(mcServer);
		boolean looking = true;
		this.networkManagers = (List<Object>) this.getNetworkMarkers.get(serverConnection);
		this.createServerChannelHandler();
		for (int i = 0; looking; i++) {
			List<Object> list = Reflection.getField(serverConnection.getClass(), List.class, i).get(serverConnection);
			for (Object item : list) {
				if (!ChannelFuture.class.isInstance(item))
					break;
				Channel serverChannel = ((ChannelFuture) item).channel();
				this.serverChannels.add(serverChannel);
				serverChannel.pipeline().addFirst(this.serverChannelHandler);
				looking = false;
			}
		}
	}

	private void unregisterChannelHandler() {
		if (this.serverChannelHandler == null)
			return;
		for (Channel serverChannel : this.serverChannels) {
			final ChannelPipeline pipeline = serverChannel.pipeline();
			serverChannel.eventLoop().execute(new Runnable() {
				@Override
				public void run() {
					try {
						pipeline.remove(serverChannelHandler);
					} catch (NoSuchElementException e) {
					}
				}

			});
		}
	}

	private void registerPlayers(Plugin plugin) {
		Collection < ? > playersOnlineNew = null;
		Player[] playersOnlineOld;
		try {
			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
				if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
					playersOnlineNew = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
					for (Object objPlayer: playersOnlineNew) {
						this.injectPlayer(((Player) objPlayer));
					}
				}
			} else {
				playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
				for (Player player: playersOnlineOld) {
					this.injectPlayer(player);
				}
			}
		} catch (Exception e) {
			ServerHandler.sendDebugTrace(e);
		}
	}

	/**
	 * Invoked when the server is starting to send a packet to a player.
	 * <p>
	 * Note that this is not executed on the main thread.
	 * 
	 * @param receiver - the receiving player, NULL for early login/status packets.
	 * @param channel - the channel that received the packet. Never NULL.
	 * @param packet - the packet being sent.
	 * @return The packet to send instead, or NULL to cancel the transmission.
	 */
	public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
		return packet;
	}

	/**
	 * Invoked when the server has received a packet from a given player.
	 * <p>
	 * Use {@link Channel#remoteAddress()} to get the remote address of the client.
	 * 
	 * @param sender - the player that sent the packet, NULL for early login/status packets.
	 * @param channel - channel that received the packet. Never NULL.
	 * @param packet - the packet being received.
	 * @return The packet to recieve instead, or NULL to cancel.
	 */
	public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
		return packet;
	}

	/**
	 * Send a packet to a particular player.
	 * <p>
	 * Note that {@link #onPacketOutAsync(Player, Channel, Object)} will be invoked with this packet.
	 * 
	 * @param player - the destination player.
	 * @param packet - the packet to send.
	 */
	public void sendPacket(Player player, Object packet) {
		this.sendPacket(this.getChannel(player), packet);
	}

	/**
	 * Send a packet to a particular client.
	 * <p>
	 * Note that {@link #onPacketOutAsync(Player, Channel, Object)} will be invoked with this packet.
	 * 
	 * @param channel - client identified by a channel.
	 * @param packet - the packet to send.
	 */
	public void sendPacket(Channel channel, Object packet) {
		channel.pipeline().writeAndFlush(packet);
	}

	/**
	 * Pretend that a given packet has been received from a player.
	 * <p>
	 * Note that {@link #onPacketInAsync(Player, Channel, Object)} will be invoked with this packet.
	 * 
	 * @param player - the player that sent the packet.
	 * @param packet - the packet that will be received by the server.
	 */
	public void receivePacket(Player player, Object packet) {
		this.receivePacket(this.getChannel(player), packet);
	}

	/**
	 * Pretend that a given packet has been received from a given client.
	 * <p>
	 * Note that {@link #onPacketInAsync(Player, Channel, Object)} will be invoked with this packet.
	 * 
	 * @param channel - client identified by a channel.
	 * @param packet - the packet that will be received by the server.
	 */
	public void receivePacket(Channel channel, Object packet) {
		channel.pipeline().context("encoder").fireChannelRead(packet);
	}

	/**
	 * Retrieve the name of the channel injector, default implementation is "tiny-" + plugin name + "-" + a unique ID.
	 * <p>
	 * Note that this method will only be invoked once. It is no longer necessary to override this to support multiple instances.
	 * 
	 * @return A unique channel handler name.
	 */
	protected String getHandlerName() {
		return "tiny-" + this.plugin.getName() + "-" + this.ID.incrementAndGet();
	}

	/**
	 * Add a custom channel handler to the given player's channel pipeline, allowing us to intercept sent and received packets.
	 * <p>
	 * This will automatically be called when a player has logged in.
	 * 
	 * @param player - the player to inject.
	 */
	public void injectPlayer(Player player) {
		this.injectChannelInternal(this.getChannel(player)).player = player;
	}

	/**
	 * Add a custom channel handler to the given channel.
	 * 
	 * @param channel - the channel to inject.
	 * @return The intercepted channel, or NULL if it has already been injected.
	 */
	public void injectChannel(Channel channel) {
		this.injectChannelInternal(channel);
	}

	/**
	 * Add a custom channel handler to the given channel.
	 * 
	 * @param channel - the channel to inject.
	 * @return The packet interceptor.
	 */
	private PacketInterceptor injectChannelInternal(Channel channel) {
		try {
			PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get(this.handlerName);
			if (interceptor == null) {
				interceptor = new PacketInterceptor();
				channel.pipeline().addBefore("packet_handler", this.handlerName, interceptor);
				this.uninjectedChannels.remove(channel);
			}
			return interceptor;
		} catch (IllegalArgumentException e) {
			return (PacketInterceptor) channel.pipeline().get(this.handlerName);
		} catch (ClassCastException e) {
			channel.pipeline().remove(this.handlerName);
			return this.injectChannelInternal(channel);
		}
	}

	/**
	 * Retrieve the Netty channel associated with a player. This is cached.
	 * 
	 * @param player - the player.
	 * @return The Netty channel.
	 */
	public Channel getChannel(Player player) {
		Channel channel = this.channelLookup.get(player.getName());
		if (channel == null) {
			Object connection = this.getConnection.get(this.getPlayerHandle.invoke(player));
			Object manager = this.getManager.get(connection);

			this.channelLookup.put(player.getName(), channel = this.getChannel.get(manager));
		}
		return channel;
	}

	/**
	 * Uninject a specific player.
	 * 
	 * @param player - the injected player.
	 */
	public void uninjectPlayer(Player player) {
		this.uninjectChannel(this.getChannel(player));
	}

	/**
	 * Uninject a specific channel.
	 * <p>
	 * This will also disable the automatic channel injection that occurs when a player has properly logged in.
	 * 
	 * @param channel - the injected channel.
	 */
	public void uninjectChannel(final Channel channel) {
		if (!this.closed) {
			this.uninjectedChannels.add(channel);
		}
		channel.eventLoop().execute(new Runnable() {
			@Override
			public void run() {
				channel.pipeline().remove(handlerName);
			}

		});
	}

	/**
	 * Determine if the given player has been injected by TinyProtocol.
	 * 
	 * @param player - the player.
	 * @return TRUE if it is, FALSE otherwise.
	 */
	public boolean hasInjected(Player player) {
		return this.hasInjected(this.getChannel(player));
	}

	/**
	 * Determine if the given channel has been injected by TinyProtocol.
	 * 
	 * @param channel - the channel.
	 * @return TRUE if it is, FALSE otherwise.
	 */
	public boolean hasInjected(Channel channel) {
		return channel.pipeline().get(this.handlerName) != null;
	}

	/**
	 * Cease listening for packets. This is called automatically when your plugin is disabled.
	 */
	public final void close() {
		if (!this.closed) {
			this.closed = true;
			for (Player player : this.plugin.getServer().getOnlinePlayers()) {
				this.uninjectPlayer(player);
			}
			HandlerList.unregisterAll(this.listener);
			this.unregisterChannelHandler();
		}
	}

	/**
	 * Channel handler that is inserted into the player's channel pipeline, allowing us to intercept sent and received packets.
	 * 
	 */
	private final class PacketInterceptor extends ChannelDuplexHandler {
		public volatile Player player;

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			final Channel channel = ctx.channel();
			handleLoginStart(channel, msg);
			try {
				msg = onPacketInAsync(player, channel, msg);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Error in onPacketInAsync().", e);
			}
			if (msg != null) {
				super.channelRead(ctx, msg);
			}
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			try {
				msg = onPacketOutAsync(player, ctx.channel(), msg);
			} catch (Exception e) {
				plugin.getLogger().log(Level.SEVERE, "Error in onPacketOutAsync().", e);
			}

			if (msg != null) {
				super.write(ctx, msg, promise);
			}
		}

		private void handleLoginStart(Channel channel, Object packet) {
			if (PACKET_LOGIN_IN_START.isInstance(packet)) {
				GameProfile profile = getGameProfile.get(packet);
				channelLookup.put(profile.getName(), channel);
			}
		}
	}
}
