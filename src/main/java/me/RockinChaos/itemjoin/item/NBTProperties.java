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

import com.google.gson.*;
import me.RockinChaos.core.utils.ReflectionUtils;
import me.RockinChaos.core.utils.ReflectionUtils.MinecraftMethod;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Readable NBT properties with scalar inference and lossless imported values. */
public final class NBTProperties {
    private static final Gson JSON = new Gson();
    private static final String[] TYPES = {"end", "byte", "short", "int", "long", "float", "double", "byte-array", "string", "list", "compound", "int-array", "long-array"};
    private static final Pattern ENTRY = Pattern.compile("^\\s*(\"(?:\\\\.|[^\"\\\\])*\")\\s*:(.*)$", Pattern.DOTALL);
    private static final Map<Class<?>, Method> READERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Method> WRITERS = new ConcurrentHashMap<>();

    private NBTProperties() {}

    /**
     * Checks if the property uses a quoted key,
     * allowing namespace colons to be distinguished from the key/value separator.
     *
     * @param property The property entry to be checked.
     * @return True if the property begins with a quoted key.
     */
    public static boolean isTyped(final String property) {
        return property.trim().startsWith("\"");
    }

    /**
     * Splits the properties into entries,
     * preserving commas inside strings, typed arrays, and compounds for entries with quoted keys.
     *
     * @param properties The comma-separated properties to be split.
     * @return The individual property entries.
     * @throws IllegalArgumentException If a quoted-key entry contains an unclosed string, array, or compound.
     */
    public static List<String> split(final String properties) {
        final List<String> entries = new ArrayList<>();
        int start = 0;
        int depth = 0;
        boolean quoted = false;
        boolean escaped = false;
        boolean typed = false;
        for (int i = 0; i < properties.length(); i++) {
            final char c = properties.charAt(i);
            if (i == start) {
                int first = i;
                while (first < properties.length() && Character.isWhitespace(properties.charAt(first))) first++;
                typed = first < properties.length() && properties.charAt(first) == '"';
            }
            if (typed) {
                if (escaped) { escaped = false; continue; }
                if (quoted && c == '\\') { escaped = true; continue; }
                if (c == '"') quoted = !quoted;
                if (!quoted) {
                    if (c == '[' || c == '{') depth++;
                    if (c == ']' || c == '}') depth--;
                }
            }
            if (c == ',' && !quoted && depth == 0) {
                entries.add(properties.substring(start, i));
                start = i + 1;
            }
        }
        if (quoted || depth != 0) throw new IllegalArgumentException("Unclosed typed NBT property");
        if (start < properties.length()) entries.add(properties.substring(start));
        while (!entries.isEmpty() && entries.get(entries.size() - 1).isEmpty()) entries.remove(entries.size() - 1);
        return entries;
    }

    /**
     * Deserializes property entries with quoted keys into native NBT tags.
     *
     * @param properties The comma-separated property entries with quoted keys.
     * @return The property keys mapped to their native NBT tag values.
     * @throws IllegalArgumentException If the properties cannot be parsed into native NBT tags.
     */
    @SuppressWarnings("unchecked")
    public static Map<Object, Object> deserialize(final String properties) {
        final Class<?> compoundClass = ReflectionUtils.getMinecraftClass("NBTTagCompound");
        final Object compound = decode(properties, compoundClass);
        final Map<Object, Object> result = new HashMap<>();
        for (final String key : (Set<String>) ReflectionUtils.getMethod(compoundClass, MinecraftMethod.getKeys.getMethod()).invoke(compound)) {
            result.put(key, ReflectionUtils.getMethod(compoundClass, MinecraftMethod.getBase.getMethod(), String.class).invoke(compound, key));
        }
        return result;
    }

    /**
     * Serializes the properties into readable entries,
     * preserving their key names and native NBT types.
     *
     * @param properties The property keys mapped to strings or native NBT tag values.
     * @return The comma-separated property entries.
     * @throws IllegalArgumentException If the native NBT compound cannot be encoded.
     */
    public static String serialize(final Map<Object, Object> properties) {
        final Class<?> compoundClass = ReflectionUtils.getMinecraftClass("NBTTagCompound");
        final Object compound = ReflectionUtils.getConstructor(compoundClass).invoke();
        for (final Map.Entry<Object, Object> property : properties.entrySet()) {
            if (property.getValue() instanceof String) {
                ReflectionUtils.getMethod(compoundClass, MinecraftMethod.setString.getMethod(), String.class, String.class)
                        .invoke(compound, (String) property.getKey(), property.getValue());
            } else {
                ReflectionUtils.getMethod(compoundClass, MinecraftMethod.put.getMethod(), String.class, ReflectionUtils.getMinecraftClass("NBTBase"))
                        .invoke(compound, (String) property.getKey(), property.getValue());
            }
        }
        return encode(compound);
    }

    /**
     * Encodes a native NBT compound as readable property entries.
     * Binary NBT is used internally to preserve array types and string escaping on legacy versions.
     *
     * @param compound The native NBT compound containing the properties.
     * @return The comma-separated property entries.
     * @throws IllegalArgumentException If the native NBT compound cannot be encoded.
     */
    static String encode(final Object compound) {
        try {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            streamMethod(compound.getClass(), true).invoke(null, compound, new DataOutputStream(bytes));
            final DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
            if (input.readByte() != 10) throw new IOException("Expected compound");
            input.readUTF();
            final JsonObject entries = readValue(input, 10).getAsJsonObject();
            final StringJoiner result = new StringJoiner(", ");
            for (final Map.Entry<String, JsonElement> entry : entries.entrySet()) {
                final JsonObject tag = entry.getValue().getAsJsonObject();
                final String type = tag.get("type").getAsString();
                result.add(JSON.toJson(entry.getKey()) + ":" + formatValue(type, tag.get("value")));
            }
            return result.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not serialize NBT properties", e);
        }
    }

    /**
     * Decodes property entries with quoted keys into a native NBT compound,
     * inferring scalar types and preserving explicitly specified types.
     *
     * @param properties The comma-separated property entries with quoted keys.
     * @param compoundClass The native NBT compound class for the server version.
     * @return The native NBT compound containing the decoded properties.
     * @throws IllegalArgumentException If the property syntax, values, or native NBT decoding is invalid.
     */
    static Object decode(final String properties, final Class<?> compoundClass) {
        try {
            final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            final DataOutputStream output = new DataOutputStream(bytes);
            output.writeByte(10);
            output.writeUTF("");
            for (final String entry : split(properties)) {
                final Matcher match = ENTRY.matcher(entry);
                if (!match.matches()) throw new IllegalArgumentException("Expected a quoted NBT property key: " + entry);
                final String key = JSON.fromJson(match.group(1), String.class);
                String value = match.group(2).trim();
                final JsonObject inferred = inferValue(value);
                final int type = typeId(inferred.get("type").getAsString());
                final JsonElement payload = inferred.get("value");
                if (type == 0) throw new IllegalArgumentException("Named END tag");
                output.writeByte(type);
                output.writeUTF(key);
                writeValue(output, type, payload);
            }
            output.writeByte(0);
            return streamMethod(compoundClass, false).invoke(null, new DataInputStream(new ByteArrayInputStream(bytes.toByteArray())));
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse typed NBT properties", e);
        }
    }

    /**
     * Creates a string property for a legacy unquoted key or menu input,
     * preserving the literal value.
     *
     * @param key The full property key to be preserved.
     * @param value The literal property value to be preserved.
     * @return The property key mapped to its string value.
     */
    public static Map<Object, Object> scalar(final String key, final String value) {
        final Map<Object, Object> result = new HashMap<>();
        result.put(key, value);
        return result;
    }

    /**
     * Formats an NBT value for a property entry,
     * retaining numeric suffixes or type annotations where needed.
     *
     * @param type The NBT type name of the value.
     * @param value The JSON representation of the NBT value.
     * @return The formatted property value.
     */
    private static String formatValue(final String type, final JsonElement value) {
        switch (type) {
            case "string": case "int":
            case "double": return value.toString();
            case "byte":
                if (value.getAsByte() == 0) return "false";
                if (value.getAsByte() == 1) return "true";
                return value + "b";
            case "short": return value + "s";
            case "long": return value + "L";
            case "float": return value + "f";
            default: return "(" + type + ")" + value;
        }
    }

    /**
     * Infers the NBT type and JSON representation of a property value.
     * Quoted values remain strings, while numbers, booleans, and explicit types are parsed accordingly.
     *
     * @param input The property value to be parsed.
     * @return The inferred NBT type name and its JSON value.
     * @throws IllegalArgumentException If a quoted value, number, or explicit type is invalid.
     */
    private static JsonObject inferValue(final String input) {
        final String value = input.trim();
        if (value.startsWith("\"")) {
            final JsonElement string = JSON.fromJson(value, JsonElement.class);
            if (string == null || !string.isJsonPrimitive() || !string.getAsJsonPrimitive().isString()) {
                throw new IllegalArgumentException("Expected a quoted string");
            }
            return tag(8, string);
        }
        // Continue to read explicit type annotations saved by earlier development builds and complex tags.
        if (value.startsWith("(")) {
            final int end = value.indexOf(')');
            if (end < 0) throw new IllegalArgumentException("Missing NBT type delimiter");
            return tag(typeId(value.substring(1, end)), JSON.fromJson(value.substring(end + 1).trim(), JsonElement.class));
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return tag(1, new JsonPrimitive(Boolean.parseBoolean(value) ? 1 : 0));
        }
        if (value.matches("[+-]?[0-9]+")) {
            final long number = Long.parseLong(value);
            return tag(number >= Integer.MIN_VALUE && number <= Integer.MAX_VALUE ? 3 : 4, new JsonPrimitive(number));
        }
        if (value.matches("[+-]?[0-9]+[bBsSlL]")) {
            final char suffix = Character.toLowerCase(value.charAt(value.length() - 1));
            return tag(suffix == 'b' ? 1 : suffix == 's' ? 2 : 4,
                    new JsonPrimitive(new java.math.BigDecimal(value.substring(0, value.length() - 1))));
        }
        if (value.matches("[+-]?(?:(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?|NaN|Infinity)[fFdD]?")) {
            final char suffix = Character.toLowerCase(value.charAt(value.length() - 1));
            final boolean suffixed = suffix == 'f' || suffix == 'd';
            final String number = suffixed ? value.substring(0, value.length() - 1) : value;
            return suffix == 'f' ? tag(5, new JsonPrimitive(Float.parseFloat(number)))
                    : tag(6, new JsonPrimitive(Double.parseDouble(number)));
        }
        return tag(8, new JsonPrimitive(input));
    }

    /**
     * Gets the numerical NBT type identifier for the specified type name.
     *
     * @param name The NBT type name to be located.
     * @return The numerical NBT type identifier.
     * @throws IllegalArgumentException If the NBT type name is unknown.
     */
    private static int typeId(final String name) {
        for (int i = 0; i < TYPES.length; i++) if (TYPES[i].equals(name)) return i;
        throw new IllegalArgumentException("Unknown NBT type: " + name);
    }

    /**
     * Creates a JSON object containing an NBT type name and its value.
     *
     * @param type The numerical NBT type identifier.
     * @param value The JSON representation of the NBT value.
     * @return The JSON object containing the type name and value.
     */
    private static JsonObject tag(final int type, final JsonElement value) {
        final JsonObject result = new JsonObject();
        result.addProperty("type", TYPES[type]);
        result.add("value", value);
        return result;
    }

    /**
     * Reads a binary NBT payload and converts it to a JSON value.
     *
     * @param input The binary input containing the NBT payload.
     * @param type The numerical NBT type identifier of the payload.
     * @return The JSON representation of the NBT payload.
     * @throws IOException If the payload cannot be read or its NBT type is unsupported.
     */
    private static JsonElement readValue(final DataInput input, final int type) throws IOException {
        switch (type) {
            case 1: return new JsonPrimitive(input.readByte());
            case 2: return new JsonPrimitive(input.readShort());
            case 3: return new JsonPrimitive(input.readInt());
            case 4: return new JsonPrimitive(input.readLong());
            case 5: return new JsonPrimitive(input.readFloat());
            case 6: return new JsonPrimitive(input.readDouble());
            case 8: return new JsonPrimitive(input.readUTF());
            case 7: case 11: case 12: {
                final JsonArray values = new JsonArray();
                final int size = input.readInt();
                for (int i = 0; i < size; i++) values.add(readValue(input, type == 7 ? 1 : type == 11 ? 3 : 4));
                return values;
            }
            case 9: {
                final int elementType = input.readByte();
                final int size = input.readInt();
                final JsonArray values = new JsonArray();
                for (int i = 0; i < size; i++) values.add(readValue(input, elementType));
                return tag(elementType, values);
            }
            case 10: {
                final JsonObject values = new JsonObject();
                int childType;
                while ((childType = input.readByte()) != 0) {
                    final String key = input.readUTF();
                    values.add(key, tag(childType, readValue(input, childType)));
                }
                return values;
            }
            default: throw new IOException("Unsupported NBT type: " + type);
        }
    }

    /**
     * Writes a JSON value as a binary NBT payload.
     *
     * @param output The binary output receiving the NBT payload.
     * @param type The numerical NBT type identifier of the payload.
     * @param value The JSON representation of the NBT value to be written.
     * @throws IOException If the payload cannot be written or its NBT type is unsupported.
     * @throws IllegalArgumentException If an END tag is used as a named tag or in a nonempty list.
     */
    private static void writeValue(final DataOutput output, final int type, final JsonElement value) throws IOException {
        switch (type) {
            case 1: output.writeByte(value.getAsBigDecimal().byteValueExact()); break;
            case 2: output.writeShort(value.getAsBigDecimal().shortValueExact()); break;
            case 3: output.writeInt(value.getAsBigDecimal().intValueExact()); break;
            case 4: output.writeLong(value.getAsBigDecimal().longValueExact()); break;
            case 5: output.writeFloat(value.getAsFloat()); break;
            case 6: output.writeDouble(value.getAsDouble()); break;
            case 8: output.writeUTF(value.getAsString()); break;
            case 7: case 11: case 12: {
                final JsonArray values = value.getAsJsonArray();
                output.writeInt(values.size());
                for (final JsonElement child : values) writeValue(output, type == 7 ? 1 : type == 11 ? 3 : 4, child);
                break;
            }
            case 9: {
                final JsonObject list = value.getAsJsonObject();
                final int elementType = typeId(list.get("type").getAsString());
                final JsonArray values = list.getAsJsonArray("value");
                if (elementType == 0 && values.size() != 0) throw new IllegalArgumentException("Nonempty END list");
                output.writeByte(elementType);
                output.writeInt(values.size());
                for (final JsonElement child : values) writeValue(output, elementType, child);
                break;
            }
            case 10: {
                for (final Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
                    final JsonObject child = entry.getValue().getAsJsonObject();
                    final int childType = typeId(child.get("type").getAsString());
                    if (childType == 0) throw new IllegalArgumentException("Named END tag");
                    output.writeByte(childType);
                    output.writeUTF(entry.getKey());
                    writeValue(output, childType, child.get("value"));
                }
                output.writeByte(0);
                break;
            }
            default: throw new IOException("Unsupported NBT type: " + type);
        }
    }

    /**
     * Finds and caches the native NBT reader or writer by its method signature,
     * supporting legacy, Spigot-remapped, and Mojang-mapped method names.
     * Minecraft 1.8 uses DataInputStream for the reader, while newer versions use DataInput.
     *
     * @param compoundClass The native NBT compound class for the server version.
     * @param writing True to locate the writer, or false to locate the reader.
     * @return The native NBT reader or writer method.
     * @throws ReflectiveOperationException If the native NBT reader or writer cannot be located.
     */
    private static Method streamMethod(final Class<?> compoundClass, final boolean writing) throws ReflectiveOperationException {
        final Map<Class<?>, Method> cache = writing ? WRITERS : READERS;
        final Method cached = cache.get(compoundClass);
        if (cached != null) {
            return cached;
        }
        final String packageName = compoundClass.getPackage().getName();
        for (final String name : new String[]{"NbtIo", "NBTCompressedStreamTools"}) {
            final Class<?> ioClass;
            try {
                ioClass = Class.forName(packageName + "." + name, false, compoundClass.getClassLoader());
            } catch (ClassNotFoundException ignored) {
                continue;
            }
            for (final Method method : ioClass.getMethods()) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                final Class<?>[] parameters = method.getParameterTypes();
                final boolean matches = writing
                        ? method.getReturnType() == void.class && Arrays.equals(parameters, new Class<?>[]{compoundClass, DataOutput.class})
                        : method.getReturnType() == compoundClass && parameters.length == 1
                        && (parameters[0] == DataInput.class || parameters[0] == DataInputStream.class);
                if (matches) {
                    cache.put(compoundClass, method);
                    return method;
                }
            }
        }
        throw new NoSuchMethodException("Cannot find native NBT " + (writing ? "writer" : "reader") + " for " + compoundClass.getName());
    }
}
