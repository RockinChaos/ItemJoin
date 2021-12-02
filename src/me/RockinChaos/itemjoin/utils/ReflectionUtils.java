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
package me.RockinChaos.itemjoin.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
* An utility class that simplifies reflection in Bukkit plugins.
* 
*/
public final class ReflectionUtils {
	private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
	private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
	private static String MC_PREFIX = "net.minecraft";
	private static String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
	private static Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");
	private static boolean MC_REMAPPED = (Integer.parseInt(VERSION.replace("_", "").replace("R0", "").replace("R1", "").replace("R2", "").replace("R3", "").replace("R4", "").replace("R5", "").replaceAll("[a-z]", "")) >= 117);
	
   /**
	* An interface for invoking a specific constructor.
	*/
	public interface ConstructorInvoker {
	   /**
		* Invoke a constructor for a specific class.
		* 
		* @param arguments - the arguments to pass to the constructor.
		* @return The constructed object.
		*/
		public Object invoke(final Object... arguments);
	}

   /**
	* An interface for invoking a specific method.
	*/
	public interface MethodInvoker {
	   /**
		* Invoke a method on a specific target object.
		* 
		* @param target - the target object, or NULL for a method.
		* @param arguments - the arguments to pass to the method.
		* @return The return value, or NULL if is void.
		*/
		public Object invoke(final Object target, final Object... arguments);
	}

   /**
	* An interface for retrieving the field content.
	* 
	* @param <T> - field type.
	*/
	public interface FieldAccessor<T> {
	   /**
		* Retrieve the content of a field.
		* 
		* @param target - the target object, or NULL for a field.
		* @return The value of the field.
		*/
		public T get(final Object target);

	   /**
		* Set the content of a field.
		* 
		* @param target - the target object, or NULL for a field.
		* @param value - the new value of the field.
		*/
		public void set(final Object target, final Object value);

	   /**
		* Determine if the given object has this field.
		* 
		* @param target - the object to test.
		* @return TRUE if it does, FALSE otherwise.
		*/
		public boolean hasField(final Object target);
	}

   /**
	* Retrieve a field accessor for a specific field type and name.
	* 
	* @param target - the target type.
	* @param name - the name of the field, or NULL to ignore.
	* @param fieldType - a compatible field type.
	* @return The field accessor.
	*/
	public static <T> FieldAccessor<T> getField(final Class<?> target, final String name, final Class<T> fieldType) {
		return getField(target, name, fieldType, 0);
	}

   /**
	* Retrieve a field accessor for a specific field type and name.
	* 
	* @param className - lookup name of the class, see {@link #getClass(String)}.
	* @param name - the name of the field, or NULL to ignore.
	* @param fieldType - a compatible field type.
	* @return The field accessor.
	*/
	public static <T> FieldAccessor<T> getField(final String className, final String name, final Class<T> fieldType) {
		return getField(getClass(className), name, fieldType, 0);
	}

   /**
	* Retrieve a field accessor for a specific field type and name.
	* 
	* @param target - the target type.
	* @param fieldType - a compatible field type.
	* @param index - the number of compatible fields to skip.
	* @return The field accessor.
	*/
	public static <T> FieldAccessor<T> getField(final Class<?> target, final Class<T> fieldType, final int index) {
		return getField(target, null, fieldType, index);
	}

   /**
	* Retrieve a field accessor for a specific field type and name.
	* 
	* @param className - lookup name of the class, see {@link #getClass(String)}.
	* @param fieldType - a compatible field type.
	* @param index - the number of compatible fields to skip.
	* @return The field accessor.
	*/
	public static <T> FieldAccessor<T> getField(final String className, final Class<T> fieldType, final int index) {
		return getField(getClass(className), fieldType, index);
	}

   /**
	* Retrieve a field accessor for a specific field type and name.
	* 
	* @param target - the targeted class.
	* @param fieldType - a compatible field type.
	* @param index - the number of compatible fields to skip.
	* @return The field accessor.
	*/
	private static <T> FieldAccessor<T> getField(final Class<?> target, final String name, Class<T> fieldType, int index) {
		for (final Field field : target.getDeclaredFields()) {
			if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
				field.setAccessible(true);
				return new FieldAccessor<T>() {
					@Override
					public T get(Object target) {
						try {
							return (T) field.get(target);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}
					@Override
					public void set(Object target, Object value) {
						try {
							field.set(target, value);
						} catch (IllegalAccessException e) {
							throw new RuntimeException("Cannot access reflection.", e);
						}
					}
					@Override
					public boolean hasField(Object target) {
						return field.getDeclaringClass().isAssignableFrom(target.getClass());
					}
				};
			}
		}
		if (target.getSuperclass() != null)
			return getField(target.getSuperclass(), name, fieldType, index);
		throw new IllegalArgumentException("Cannot find field with type " + fieldType);
	}

   /**
	* Search for the first publicly and privately defined method of the given name and parameter count.
	* 
	* @param className - lookup name of the class, see {@link #getClass(String)}.
	* @param methodName - the method name, or NULL to skip.
	* @param params - the expected parameters.
	* @return An object that invokes this specific method.
	* @throws IllegalStateException If we cannot find this method.
	*/
	public static MethodInvoker getMethod(final String className, final String methodName, final Class<?>... params) {
		return getTypedMethod(getClass(className), methodName, null, params);
	}

   /**
	* Search for the first publicly and privately defined method of the given name and parameter count.
	* 
	* @param clazz - a class to start with.
	* @param methodName - the method name, or NULL to skip.
	* @param params - the expected parameters.
	* @return An object that invokes this specific method.
	* @throws IllegalStateException If we cannot find this method.
	*/
	public static MethodInvoker getMethod(final Class<?> clazz, final String methodName, final Class<?>... params) {
		return getTypedMethod(clazz, methodName, null, params);
	}

   /**
	* Search for the first publicly and privately defined method of the given name and parameter count.
	* 
	* @param clazz - a class to start with.
	* @param methodName - the method name, or NULL to skip.
	* @param returnType - the expected return type, or NULL to ignore.
	* @param params - the expected parameters.
	* @return An object that invokes this specific method.
	* @throws IllegalStateException If we cannot find this method.
	*/
	public static MethodInvoker getTypedMethod(final Class<?> clazz, final String methodName, final Class<?> returnType, final Class<?>... params) {
		for (final Method method : clazz.getDeclaredMethods()) {
			if ((methodName == null || method.getName().equals(methodName))
					&& (returnType == null || method.getReturnType().equals(returnType))
					&& Arrays.equals(method.getParameterTypes(), params)) {
				method.setAccessible(true);
				return new MethodInvoker() {
					@Override
					public Object invoke(Object target, Object... arguments) {
						try {
							return method.invoke(target, arguments);
						} catch (Exception e) {
							throw new RuntimeException("Cannot invoke method " + method, e);
						}
					}

				};
			}
		}
		if (clazz.getSuperclass() != null)
			return getMethod(clazz.getSuperclass(), methodName, params);
		throw new IllegalStateException(String.format("Unable to find method %s (%s).", methodName, Arrays.asList(params)));
	}

   /**
	* Search for the first publically and privately defined constructor of the given name and parameter count.
	* 
	* @param className - lookup name of the class, see {@link #getClass(String)}.
	* @param params - the expected parameters.
	* @return An object that invokes this constructor.
	* @throws IllegalStateException If we cannot find this method.
	*/
	public static ConstructorInvoker getConstructor(final String className, final Class<?>... params) {
		return getConstructor(getClass(className), params);
	}

   /**
	* Search for the first publically and privately defined constructor of the given name and parameter count.
	* 
	* @param clazz - a class to start with.
	* @param params - the expected parameters.
	* @return An object that invokes this constructor.
	* @throws IllegalStateException If we cannot find this method.
	*/
	public static ConstructorInvoker getConstructor(final Class<?> clazz, final Class<?>... params) {
		for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (Arrays.equals(constructor.getParameterTypes(), params)) {
				constructor.setAccessible(true);
				return new ConstructorInvoker() {
					@Override
					public Object invoke(Object... arguments) {
						try {
							return constructor.newInstance(arguments);
						} catch (Exception e) {
							throw new RuntimeException("Cannot invoke constructor " + constructor, e);
						}
					}
				};
			}
		}
		throw new IllegalStateException(String.format("Unable to find constructor for %s (%s).", clazz, Arrays.asList(params)));
	}

   /**
	* Retrieve a class from its full name, without knowing its type on compile time.
	* <p>
	* This is useful when looking up fields by a NMS or OBC type.
	* <p>
	* 
	* @see {@link #getClass()} for more information.
	* @param lookupName - the class name with variables.
	* @return The class.
	*/
	public static Class<Object> getUntypedClass(final String lookupName) {
		Class<Object> clazz = (Class<Object>) getClass(lookupName);
		return clazz;
	}
	
   /**
	* Retrieve a the version referenced in the packages.
	* 
	*/
	public static String getServerVersion() {
		return VERSION;
	}

   /**
	* Retrieve a class from its full name.
	* <p>
	* Strings enclosed with curly brackets - such as {TEXT} - will be replaced according to the following table:
	* <p>
	* <table border="1">
	* <tr>
	* <th>Variable</th>
	* <th>Content</th>
	* </tr>
	* <tr>
	* <td>{nms}</td>
	* <td>Actual package name of net.minecraft.server.VERSION</td>
	* </tr>
	* <tr>
	* <td>{obc}</td>
	* <td>Actual pacakge name of org.bukkit.craftbukkit.VERSION</td>
	* </tr>
	* <tr>
	* <td>{version}</td>
	* <td>The current Minecraft package VERSION, if any.</td>
	* </tr>
	* </table>
	* 
	* @param lookupName - the class name with variables.
	* @return The looked up class.
	* @throws IllegalArgumentException If a variable or class could not be found.
	*/
	public static Class<?> getClass(final String lookupName) {
		return getCanonicalClass(expandVariables(lookupName));
	}

   /**
	* Retrieve a class in the net.minecraft.server.VERSION.* package.
	* 
	* @param name - the name of the class, excluding the package.
	* @throws IllegalArgumentException If the class doesn't exist.
	*/
	public static Class<?> getMinecraftClass(final String name) {
		if (MC_REMAPPED) {
			try {
				return getMinecraftTag(name);
			} catch (Exception e) {
				return getCanonicalClass(NMS_PREFIX + "." + name); 
			}
		} else { 
			return getCanonicalClass(NMS_PREFIX + "." + name); 
		}
	}

   /**
	* Retrieve a class in the org.bukkit.craftbukkit.VERSION.* package.
	* 
	* @param name - the name of the class, excluding the package.
	* @throws IllegalArgumentException If the class doesn't exist.
	*/
	public static Class<?> getCraftBukkitClass(final String name) {
		return getCanonicalClass(OBC_PREFIX + "." + name);
	}
	
   /**
	* Retrieve a class in the org.bukkit.* package.
	* 
	* @param name - the name of the class, excluding the package.
	* @throws IllegalArgumentException If the class doesn't exist.
	*/
	public static Class<?> getBukkitClass(final String name) {
		return getCanonicalClass("org.bukkit." + name);
	}

   /**
	* Retrieve a class by its canonical name.
	* 
    * @param canonicalName - the canonical name.
	* @return The class.
	*/
	private static Class<?> getCanonicalClass(final String canonicalName) {
		try {
			return Class.forName(canonicalName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Cannot find " + canonicalName, e);
		}
	}
	
   /**
	* Sends a PacketPlayOutSetSlot Packet to the specified player.
	* 
    * @param player - The player receiving the packet.
    * @param item - The ItemStack to be sent to the slot.
    * @param index - The slot to have the item sent.
	*/
	public static void sendPacketPlayOutSetSlot(Player player, ItemStack item, int index) throws Exception {
		Class < ? > itemStack = getMinecraftClass("ItemStack");
		Object nms = getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
		Object packet = null;
		if (MC_REMAPPED) {
			try {
				packet = getMinecraftClass("PacketPlayOutSetSlot").getConstructor(int.class, int.class, int.class, itemStack).newInstance(0, 0, index, itemStack.cast(nms));
			} catch (NoSuchMethodException e) {
				packet = getMinecraftClass("PacketPlayOutSetSlot").getConstructor(int.class, int.class, itemStack).newInstance(0, index, itemStack.cast(nms));
			}
		} else {
			packet = getMinecraftClass("PacketPlayOutSetSlot").getConstructor(int.class, int.class, itemStack).newInstance(0, index, itemStack.cast(nms));
		}
		sendPacket(player, packet);
	}
	
   /**
	* Sends a Packet Object to the specified player.
	* 
    * @param player - The player receiving the packet.
    * @param packet - The Packet Object being sent.
	*/
	public static void sendPacket(final Player player, final Object packet) throws Exception {
	    Object nmsPlayer = player.getClass().getMethod("getHandle").invoke(player);
	    Object playerHandle = nmsPlayer.getClass().getField(MinecraftField.PlayerConnection.getField(nmsPlayer.getClass())).get(nmsPlayer);
	    Class<?> packetClass = getMinecraftClass("Packet");
	    playerHandle.getClass().getMethod(MinecraftMethod.sendPacket.getMethod(playerHandle.getClass(), packetClass), packetClass).invoke(playerHandle, packet);
	}

   /**
	* Expand variables such as "{nms}" and "{obc}" to their corresponding packages.
	* 
	* @param name - the full name of the class.
	* @return The expanded string.
	*/
	private static String expandVariables(final String name) {
		StringBuffer output = new StringBuffer();
		Matcher matcher = MATCH_VARIABLE.matcher(name);
		while (matcher.find()) {
			String variable = matcher.group(1);
			String replacement = "";
			if ("nms".equalsIgnoreCase(variable)) {
				if (MC_REMAPPED) {
					try {
						String forClass = ReflectionUtils.getMinecraftClass("PlayerConnection").getCanonicalName();
						if (forClass != null) {
							replacement = MC_PREFIX;
						} else { replacement = NMS_PREFIX; }
					} catch (Exception e) {
						replacement = NMS_PREFIX;
					}
				} else { 
					replacement = NMS_PREFIX;
				}
				replacement = NMS_PREFIX;
			}
			else if ("obc".equalsIgnoreCase(variable)) {
				replacement = OBC_PREFIX;
			}
			else if ("version".equalsIgnoreCase(variable)) {
				replacement = VERSION;
			}
			else {
				throw new IllegalArgumentException("Unknown variable: " + variable);
			}
			if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
				replacement += ".";
			matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
			
		}
		matcher.appendTail(output);
		return output.toString();
	}
	
	
   /**
	* Searchable methods that no longer require NBT Reflections.
	* 
	*/
	public enum MinecraftMethod {
		add("add", "c"),
		set("set", "a"),
		setInt("setInt", "a"),
		getPage("a", "a"),
		getTag("getTag", "s"),
		setTag("setTag", "c"),
		setString("setString", "a"),
		getString("getString", "l"),
		setDouble("setDouble", "a"),
		sendPacket("sendPacket", "a");
		public String original;
		public String remapped;
		private MinecraftMethod(final String original, final String remapped) {
			this.original = original;
			this.remapped = remapped;
		}
		
		public String getMethod(final Object objClass, Class<?>...arguments) {
			try {
				Class<?> canonicalClass = (!(objClass instanceof Class<?>) ? objClass.getClass() : (Class<?>)objClass);
				Method forMethod = (arguments == null ? canonicalClass.getMethod((ReflectionUtils.remapped() ? this.remapped : this.original)) : canonicalClass.getMethod((ReflectionUtils.remapped() ? this.remapped : this.original), arguments));
				if (forMethod != null) {
					return (ReflectionUtils.remapped() ? this.remapped : this.original);	
				} else { return this.original; }
			} catch (Exception e) {
				return this.original;
			}
		}
	}
	
   /**
	* Searchable tags that no longer require NBT Reflections.
	* 
	*/
	public enum MinecraftField {
		PlayerConnection("playerConnection", "b"),
		NetworkManager("networkManager", "a");
		public String original;
		public String remapped;
		private MinecraftField(final String original, final String remapped) {
			this.original = original;
			this.remapped = remapped;
		}
		
		public String getField(final Class<? extends Object> canonicalClass) {
			try {
				Field forField = canonicalClass.getField((ReflectionUtils.remapped() ? this.remapped : this.original));
				if (forField != null) {
					return (ReflectionUtils.remapped() ? this.remapped : this.original);	
				} else { return this.original; }
			} catch (Exception e) {
				return this.original;
			}
		}
	}
	
   /**
	* Searchable tags that no longer require NBT Reflections.
	* 
	*/
	public enum MinecraftTags {
		NBTTagCompound(".nbt"),
		NBTTagList(".nbt"),
		NBTTagString(".nbt"),
		NBTBase(".nbt"),
		ItemStack(".world.item"),
		Packet(".network.protocol"),
		PacketLoginInStart(".network.protocol.login"),
		PacketPlayOutSetSlot(".network.protocol.game"),
		PlayerConnection(".server.network"),
		EntityPlayer(".server.level"),
		NetworkManager(".network"),
		MinecraftServer(".server"),
		ServerConnection(".server.network"),
		IChatBaseComponent(".network.chat"),
		IChatBaseComponent$ChatSerializer(".network.chat"),
		PacketPlayOutChat(".network.protocol.game"),
		ChatMessageType(".network.chat");
		public String tag;
		private MinecraftTags(final String tag) {
			this.tag = tag;
		}
	}
	    	
   /**
	* Gets the correct location of the searchable tag.
	* 
	* @param name - The Tag being located.
	* @return The located searchable tag.
	*/
	public static Class<?> getMinecraftTag(final String name) {
		for (MinecraftTags tag: MinecraftTags.values()) {
		    if (tag.name().equalsIgnoreCase(name)) {
		        return getCanonicalClass(MC_PREFIX + tag.tag + "."  + name);
		    }
		}
		return null;
	}
	
	public static boolean remapped() {
		return MC_REMAPPED;
	}
}