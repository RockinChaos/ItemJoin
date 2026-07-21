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

import me.RockinChaos.core.utils.ChatComponent.ClickAction;
import me.RockinChaos.core.utils.ServerUtils;
import me.RockinChaos.core.utils.StringUtils;
import me.RockinChaos.core.utils.types.PlaceHolder;
import me.RockinChaos.itemjoin.ItemJoin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Adds MiniMessage (https://docs.advntr.dev/minimessage/format.html) tag support on top of
 * ItemJoin's existing '&' legacy color-code pipeline, without altering that pipeline itself.
 * MiniMessage tags are resolved into legacy color codes before StringUtils.translateLayout() runs,
 * so existing '&' and '&#RRGGBB' coded configs continue to behave exactly as before.
 */
public class MiniMessageUtils {

    private static final Pattern TAG_HINT = Pattern.compile("<[a-zA-Z#/][^<>]{0,64}>");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    // hexColors() + useUnusualXRepeatedCharacterHexFormat() matches the legacy hex color format
    // ChaosCore's StringUtils#translateHexColorCodes already produces for '&#RRGGBB' codes, so
    // gradients/hex tags render at full RGB precision instead of degrading to the 16 named colors.
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character(ChatColor.COLOR_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /**
     * Resolves any MiniMessage tags present in the text into legacy color codes.
     * Strings without a plausible tag are returned untouched (fast path), and any parser
     * failure falls back to the original, unmodified text rather than breaking the caller.
     *
     * @param input - The raw text, possibly containing MiniMessage tags and/or '&' codes.
     * @return the text with MiniMessage tags resolved to legacy codes.
     */
    public static String parseTags(final String input) {
        if (input == null || input.isEmpty() || !TAG_HINT.matcher(input).find()) {
            return input;
        }
        try {
            return LEGACY.serialize(MINI_MESSAGE.deserialize(input));
        } catch (final Exception e) {
            ServerUtils.logWarn("{MiniMessageUtils} Failed to parse MiniMessage tags in: \"" + input + "\", leaving as-is.");
            ServerUtils.sendDebugTrace(e);
            return input;
        }
    }

    /**
     * Drop-in replacement for StringUtils.translateLayout(String, Player, PlaceHolder...) that
     * additionally resolves MiniMessage tags before placeholder substitution and legacy color
     * translation run, so a raw player-supplied placeholder value can never be misread as a tag.
     */
    public static String translateLayout(final String input, final Player player, final PlaceHolder... placeholders) {
        return StringUtils.translateLayout(parseTags(input), player, placeholders);
    }

    /**
     * Applies translateLayout() to every entry of a lore/message list.
     */
    public static List<String> translateLayoutList(final List<String> input, final Player player, final PlaceHolder... placeholders) {
        final List<String> output = new ArrayList<>();
        if (input != null) {
            for (final String line : input) {
                output.add(translateLayout(line, player, placeholders));
            }
        }
        return output;
    }

    /**
     * Drop-in replacement for LanguageAPI#sendLangMessage(String, CommandSender, PlaceHolder...).
     * Fetches the raw, untranslated lang entry and routes it through MiniMessage + translateLayout
     * before sending, since sendLangMessage() itself is opaque (no ChaosCore source available).
     */
    public static void sendLangMessage(final String key, final CommandSender sender, final PlaceHolder... placeholders) {
        final String raw = ItemJoin.getCore().getLang().getLangMessage(key);
        if (raw == null || raw.isEmpty()) {
            return;
        }
        final Player player = (sender instanceof Player) ? (Player) sender : null;
        sender.sendMessage(translateLayout(raw, player, placeholders));
    }

    /**
     * Drop-in replacement for LanguageAPI#dispatchMessage(CommandSender, String) that pre-resolves
     * MiniMessage tags before delegating to ChaosCore's dispatchMessage for the actual send.
     */
    public static void dispatchMessage(final CommandSender sender, final String text) {
        ItemJoin.getCore().getLang().dispatchMessage(sender, parseTags(text));
    }

    /**
     * Drop-in replacement for LanguageAPI#dispatchMessage(CommandSender, String, String, String, ClickAction)
     * that pre-resolves MiniMessage tags in both the text and hover text before delegating to
     * ChaosCore's dispatchMessage for the actual hover/click component construction and send.
     */
    public static void dispatchMessage(final CommandSender sender, final String text, final String hoverText, final String clickValue, final ClickAction action) {
        ItemJoin.getCore().getLang().dispatchMessage(sender, parseTags(text), parseTags(hoverText), clickValue, action);
    }

}
