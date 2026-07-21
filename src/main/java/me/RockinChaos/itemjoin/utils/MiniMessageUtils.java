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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Adds MiniMessage (https://docs.advntr.dev/minimessage/format.html) tag support on top of
 * ItemJoin's existing '&' legacy color-code pipeline, without altering that pipeline itself.
 * MiniMessage hard-rejects any literal '§' character (confirmed: not suppressible via
 * strict(false)), so text is only ever handed to MiniMessage while still in its raw, un-'&'-
 * translated form (TAG_HINT gates this - strings with no plausible tag skip MiniMessage
 * entirely and go straight through the original legacy pipeline, untouched). Placeholders that
 * could be used as a tag argument (e.g. <head:%player_name%>) are substituted directly (not via
 * StringUtils.translateLayout, which also performs the '&'->'§' conversion) before that MiniMessage
 * parse. Built-in placeholders (player name/uuid) are Mojang-username-safe and can't smuggle in a
 * fake tag; custom PlaceHolder values are plugin/config-controlled, so this is safe in practice.
 * Accepted trade-off: legacy '&' codes coexisting with a MiniMessage tag on the same line are not
 * colorized (MiniMessage doesn't recognize '&' as a formatting instruction) - use MiniMessage's own
 * color tags on any line that also uses a MiniMessage tag.
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
     * additionally resolves MiniMessage tags. Only used today for the pre-1.14 legacy-secret item
     * name branch (ItemMap/ItemAnimation), where ObjectComponent tags like <head>/<sprite> could
     * never render anyway (the client predates the concept), so parseTags()'s legacy-only output
     * is sufficient; kept in its original order (MiniMessage parse, then '&'-translation) since
     * that's proven safe for this narrow, pre-1.14 use case.
     */
    public static String translateLayout(final String input, final Player player, final PlaceHolder... placeholders) {
        return StringUtils.translateLayout(parseTags(input), player, placeholders);
    }

    /**
     * Substitutes only the placeholder tokens that could plausibly appear as a MiniMessage tag
     * argument (e.g. <head:%player_name%>), without touching '&' legacy codes. Must run before
     * MINI_MESSAGE.deserialize() so a placeholder resolves before the tag using it is parsed;
     * deliberately does not delegate to StringUtils.translateLayout(), since that also converts
     * '&' to real '§' characters, which MiniMessage unconditionally refuses to parse.
     */
    private static String preResolvePlaceholders(final String input, final Player player, final PlaceHolder... placeholders) {
        String result = input;
        if (player != null) {
            result = result.replace(PlaceHolder.Holder.PLAYER.ph(), player.getName());
            result = result.replace(PlaceHolder.Holder.PLAYER_UUID.ph(), player.getUniqueId().toString());
        }
        for (final PlaceHolder placeholder : placeholders) {
            if (placeholder == null) {
                continue;
            }
            for (final Map.Entry<PlaceHolder.Holder, String> entry : placeholder.keys().entrySet()) {
                result = result.replace(entry.getKey().ph(), entry.getValue());
            }
        }
        return result;
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
     * Same resolution as translateLayout(), but returns an Adventure Component instead of a legacy
     * '§'-coded String. Bukkit/Paper's deprecated String-based ItemMeta setters (setDisplayName,
     * setLore) round-trip through a legacy parser that snaps arbitrary hex/gradient colors to the
     * nearest of the 16 named ChatColors, and more importantly cannot represent ObjectComponent-based
     * tags like <head>/<sprite> at all - the Component-based setters (customName, itemName, lore)
     * apply the parsed tree directly with no such downsampling or data loss, so this must be used
     * for item text (and, for the same ObjectComponent reason, for every chat message send below).
     * <p>
     * Strings with no plausible tag (TAG_HINT) skip MiniMessage entirely and go through the original
     * legacy pipeline unchanged - this is not just a fast path, it's required correctness: MiniMessage
     * unconditionally throws on any literal '§' character, which StringUtils.translateLayout()
     * introduces for every '&' code, so ordinary lang messages must never reach MINI_MESSAGE.deserialize().
     * When a tag is present, placeholders that could be a tag argument are substituted first (without
     * '&'->'§' translation) and MiniMessage parses the tag directly to a Component, preserving ObjectComponent.
     */
    public static Component translateLayoutComponent(final String input, final Player player, final PlaceHolder... placeholders) {
        Component parsed;
        if (input != null && !input.isEmpty() && TAG_HINT.matcher(input).find()) {
            final String preResolved = preResolvePlaceholders(input, player, placeholders);
            try {
                parsed = MINI_MESSAGE.deserialize(preResolved);
            } catch (final Exception e) {
                ServerUtils.logWarn("{MiniMessageUtils} Failed to parse MiniMessage tags in: \"" + preResolved + "\", falling back to legacy parsing.");
                ServerUtils.sendDebugTrace(e);
                parsed = LEGACY.deserialize(StringUtils.translateLayout(input, player, placeholders));
            }
        } else {
            parsed = LEGACY.deserialize(StringUtils.translateLayout(input, player, placeholders));
        }
        // Client applies its own default style (italic, light_purple) to item name/lore components
        // that have no explicit color/decoration anywhere in the tree (same as an anvil-renamed item).
        // Setting it explicitly on this wrapping root gives every un-styled segment a plain white,
        // non-italic default instead, while still letting actual tags/colors below override it.
        return Component.empty()
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
                .append(parsed);
    }

    /**
     * Applies translateLayoutComponent() to every entry of a lore list.
     */
    public static List<Component> translateLayoutComponentList(final List<String> input, final Player player, final PlaceHolder... placeholders) {
        final List<Component> output = new ArrayList<>();
        if (input != null) {
            for (final String line : input) {
                output.add(translateLayoutComponent(line, player, placeholders));
            }
        }
        return output;
    }

    /**
     * Drop-in replacement for LanguageAPI#sendLangMessage(String, CommandSender, PlaceHolder...).
     * Fetches the raw, untranslated lang entry and sends it as a Component (via Paper's Audience-
     * inherited CommandSender#sendMessage(Component)) instead of ChaosCore's legacy String-only
     * send, since ChaosCore's own dispatch can never carry ObjectComponent tags like <head>/<sprite>.
     */
    public static void sendLangMessage(final String key, final CommandSender sender, final PlaceHolder... placeholders) {
        final String raw = ItemJoin.getCore().getLang().getLangMessage(key);
        if (raw == null || raw.isEmpty()) {
            return;
        }
        final Player player = (sender instanceof Player) ? (Player) sender : null;
        sender.sendMessage(translateLayoutComponent(raw, player, placeholders));
    }

    /**
     * Drop-in replacement for LanguageAPI#dispatchMessage(CommandSender, String) that resolves
     * MiniMessage tags to a Component and sends it directly, bypassing ChaosCore's legacy String-
     * only NMS dispatch so ObjectComponent tags like <head>/<sprite> aren't dropped.
     */
    public static void dispatchMessage(final CommandSender sender, final String text) {
        final Player player = (sender instanceof Player) ? (Player) sender : null;
        sender.sendMessage(translateLayoutComponent(text, player));
    }

    /**
     * Drop-in replacement for LanguageAPI#dispatchMessage(CommandSender, String, String, String, ClickAction)
     * that resolves MiniMessage tags in both the text and hover text to Components and attaches
     * Adventure's own hover/click events, bypassing ChaosCore's legacy NMS-packet ChatComponent
     * builder entirely so ObjectComponent tags like <head>/<sprite> survive into the sent message.
     */
    public static void dispatchMessage(final CommandSender sender, final String text, final String hoverText, final String clickValue, final ClickAction action) {
        final Player player = (sender instanceof Player) ? (Player) sender : null;
        Component message = translateLayoutComponent(text, player);
        if (hoverText != null && !hoverText.isEmpty()) {
            message = message.hoverEvent(HoverEvent.showText(translateLayoutComponent(hoverText, player)));
        }
        if (clickValue != null && !clickValue.isEmpty() && action != null) {
            ClickEvent clickEvent;
            switch (action) {
                case OPEN_URL:
                    clickEvent = ClickEvent.openUrl(clickValue);
                    break;
                case RUN_COMMAND:
                    clickEvent = ClickEvent.runCommand(clickValue);
                    break;
                case SUGGEST_COMMAND:
                    clickEvent = ClickEvent.suggestCommand(clickValue);
                    break;
                case CHANGE_PAGE:
                    clickEvent = ClickEvent.changePage(clickValue);
                    break;
                default:
                    clickEvent = null;
            }
            if (clickEvent != null) {
                message = message.clickEvent(clickEvent);
            }
        }
        sender.sendMessage(message);
    }

}
